package edu.helpdesk.signin.legacy;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.dao.PayPeriodDao;
import edu.helpdesk.signin.dao.mybatis.SigninMapper;
import edu.helpdesk.signin.legacy.dao.mybatis.LegacyEmployeeMapper;
import edu.helpdesk.signin.legacy.dao.mybatis.LegacyPayPeriodEndMapper;
import edu.helpdesk.signin.legacy.dao.mybatis.LegacySigninMapper;
import edu.helpdesk.signin.legacy.dao.mybatis.PrivilegesMapper;
import edu.helpdesk.signin.legacy.model.LegEmployee;
import edu.helpdesk.signin.legacy.model.LegPrivilegeLevel;
import edu.helpdesk.signin.legacy.model.LegSigninData;
import edu.helpdesk.signin.legacy.model.SigninDataEntry;
import edu.helpdesk.signin.legacy.util.LegacyUtil;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.SigninType;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.util.SigninUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/edu/helpdesk/signin/applicationContext.xml")
@WebAppConfiguration
public class Importer{
	private Logger log = LoggerFactory.getLogger(Importer.class);

	private Boolean isDryRun = false;

	@Autowired
	private LegacySigninMapper legacySigninMapper;

	@Autowired
	private PrivilegesMapper mapper;

	@Autowired
	private LegacyEmployeeMapper legacyEmployeeMapper;


	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninMapper signinMapper;

	@Autowired
	private LegacyPayPeriodEndMapper legacyPayPeriodMapper;
	
	@Autowired
	private PayPeriodDao payPeriodDao;

	public Importer() {
	}

	@Test
	public void runImport(){
		log.info("Running {}import of legacy database", isDryRun ? "dry run " : "");
		boolean success = true;
		try{
			// import the employees
			runEmployeeImport();

			// import the signin data
			runSigninDataImport();

			// import the pay period ends
			runPayPeriodEndImport();

		}catch(Exception e){
			log.error("EXCEPTION!!!", e);
			log.error("EXCEPTION WHILE RUNNING IMPORT!!!");
			log.error("This means that the database is in an inconsistant state, and will need to be manually recovered");
			log.error("Check the logs to see what went wrong");
			success = false;
		}
		log.info("Completed import {}successfully", success ? "" : "un");
	}


	private void runPayPeriodEndImport(){
		List<Date> ends = legacyPayPeriodMapper.getAllPeriodEnds();
		payPeriodDao.createPayPeriodEnds(ends);
		log.info("Added {} pay period ends to the database", ends.size());
	}
	

	private void runSigninDataImport(){
		log.info("Importing signin data");
		List<SigninDataEntry> entries = new ArrayList<>();
		List<Employee> employees = employeeDao.getAllEmployees();
		Map<String, Integer> employeeIds = new HashMap<>();

		for(Employee e : employees){
			employeeIds.put(e.getRiceId().toLowerCase(), e.getId());
		}
		
		List<LegSigninData> errorEntries = new ArrayList<>();
		List<LegSigninData> legacySigninData = legacySigninMapper.getAllSigninData();
		log.info("Read in {} legacy entries", legacySigninData.size());

		int errors = 0;
		for(LegSigninData d : legacySigninData){
			if(employeeIds.containsKey(d.getRiceId().toLowerCase())){
				entries.add(upgrade(d, employeeIds));
			}
			else{
				errorEntries.add(d);
				errors++;
			}
		}

		if(isDryRun == false){
			log.info("Inserting {} signin data entries into the database...", entries.size());
			signinMapper.importLegacyData_DEV_ONLY(entries);
			log.info("Done inserting entries");
		}
		
		log.info("Upgraded {} signin data entries, failed to upgrade {} entries ({})", entries.size(), errors, errorEntries);
		log.info("Done importing signin data");
	}

	private SigninDataEntry upgrade(LegSigninData d, Map<String, Integer> map){
		SigninDataEntry out = new SigninDataEntry();
		out.setEmployeeId(map.get(d.getRiceId().toLowerCase()));
		out.setSigninTime(d.getTimeIn());
		out.setSignoutTime(d.getTimeOut());
		out.setType(getType(d.getType()));
		return out;
	}
	private SigninType getType(String keyCode){
		switch(keyCode.toLowerCase()){
		case "swipe": return SigninType.SWIPE;
		case "computer": return SigninType.COMPUTER;
		case "type": return SigninType.SWIPE;
		case "edit": return SigninType.EDIT;
		case "correction": return SigninType.CORRECTION;
		case "admin": return SigninType.ADMIN;
		default: return SigninType.UNKNOWN;

		}
	}


	private void runEmployeeImport(){
		log.info("Importing employees...");
		Map<String, EmployeeType> levels = new HashMap<>();

		log.info("Reading in permission levels...");
		for(LegPrivilegeLevel l :  mapper.getAllPrivilegesMappings()){
			levels.put(l.getNetId().toLowerCase(), LegacyUtil.get().fromInt(l.getLevel()));
		}
		log.info("Read in {} permission levels", levels.size());

		List<LegEmployee> legacyEmployees = legacyEmployeeMapper.getAllEmployees();
		List<Employee> employees = new ArrayList<>();

		log.info("Read in {} employees", legacyEmployees.size());
		
		for(LegEmployee e : legacyEmployees){
			employees.add(upgrade(e, levels));
			levels.remove(e.getNetId());
		}

		log.info("There are {} orphan permission levels, creating employees for them...", levels.size());
		for(Map.Entry<String, EmployeeType> entry : levels.entrySet()){
			employees.add(generateEmployee(entry.getKey(), entry.getValue()));
		}
		log.info("Done generating employees for orphan permission levels");

		if(isDryRun == false){
			log.info("Writing {} employees to the database", employees.size());

			int createdCount = 0, updatedCount = 0;
			for(int i = 0; i < employees.size(); i++){
				Employee e = employees.get(i);
				Employee existing = employeeDao.getEmployeeByNetId(e.getNetId());

				if(existing != null){
					updatedCount++;
					log.info("Employee {} ({}) already exists in the database, updating", e.getNetId(), e.getFirstName());
					e.setEmployeeType(existing.getEmployeeType());
					e.setId(existing.getId());
					employeeDao.updateEmployee(e);
				}
				else{
					employeeDao.createEmployee(e);
					createdCount++;
				}
			}

			log.info("Done writing employees to database. Created {} new employees, updated {}", createdCount, updatedCount);
		}

		log.info("Done importing employees");
	}


	private Employee generateEmployee(String netid, EmployeeType permissionLevel){
		String name = "unknown," + netid;

		try{
			Map<String, String> map = SigninUtil.get().finger(netid);
			name = map.get("name");
		}catch(Exception e){
			log.warn("Exception while fingering {}", netid, e);
		}

		String[] names = new String[2];
		names[0] = name.substring(name.lastIndexOf(','));
		names[1] = name.substring(0, name.lastIndexOf(','));

		Employee out = new Employee();
		out.setEmployeeType(permissionLevel);
		out.setFirstName(names[0].replace(',', ' ').trim());
		out.setIsEmployeeActive(true);
		out.setLastName(names.length > 0 ? names[1].replace(',', ' ').trim() : "");
		out.setNetId(netid);
		out.setRiceId("unknown:"+netid);
		return out;
	}


	private Employee upgrade(LegEmployee employee, Map<String, EmployeeType> permissions){
		EmployeeType permission = permissions.get(employee.getNetId());

		if(permission == null){
			permission = EmployeeType.NONE;
			log.warn("Failed to find permission level for {}", employee.getName());
		}

		String[] names = employee.getName().split(" ", 2);
		Employee out = new Employee();
		out.setEmployeeType(permission);
		out.setFirstName(names[0].trim());
		out.setIsEmployeeActive(true);
		out.setLastName(names.length > 0 ? names[1].trim() : "");
		out.setNetId(employee.getNetId());
		out.setRiceId(employee.getRiceId());
		return out;
	}



}
