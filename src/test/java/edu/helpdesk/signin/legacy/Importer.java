package edu.helpdesk.signin.legacy;

import java.util.ArrayList;
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
import edu.helpdesk.signin.legacy.dao.mybatis.LegacyEmployeeMapper;
import edu.helpdesk.signin.legacy.dao.mybatis.PrivilegesMapper;
import edu.helpdesk.signin.legacy.model.LegEmployee;
import edu.helpdesk.signin.legacy.model.LegPrivilegeLevel;
import edu.helpdesk.signin.legacy.util.LegacyUtil;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.util.SigninUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/edu/helpdesk/signin/applicationContext.xml")
@WebAppConfiguration
public class Importer{
	private Logger log = LoggerFactory.getLogger(Importer.class);

	private Boolean isDryRun = true;

	@Autowired
	private PrivilegesMapper mapper;

	@Autowired
	private LegacyEmployeeMapper legacyEmployeeMapper;

	@Autowired
	private EmployeeDao employeeDao;
	
	public Importer() {
	}

	@Test
	public void runImport(){
		log.info("Running {}import of legacy database", isDryRun ? "dry run " : "");

		runEmployeeImport();

		// read all signin data
		// convert to new format
		// write all signin data


		// read all pay period ends
		// write all pay period ends
		log.info("Done running import");
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
			
			int written = 0, skipped = 0;
			for(int i = 0; i < employees.size(); i++){
				Employee e = employees.get(i);
				Employee existing = employeeDao.getEmployeeByNetId(e.getNetId());
				if(existing != null){
					skipped++;
					log.info("Employee {} ({}) already exists in the database, not updating", e.getNetId(), e.getFirstName());
				}
				else{
					employeeDao.createEmployee(e);
					written++;
				}
			}
			
			log.info("Done writing employees to database. Wrote {}, skipped {}", written, skipped);
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
