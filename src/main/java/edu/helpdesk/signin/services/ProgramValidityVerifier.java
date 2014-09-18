package edu.helpdesk.signin.services;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.dao.mybatis.SigninMapper;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;

public class ProgramValidityVerifier {
	private static final Logger log = LoggerFactory.getLogger(ProgramValidityVerifier.class);

	@Value("${sysadmin.netid}")
	private String netids;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninMapper smapper;

	public ProgramValidityVerifier() {}

	@PostConstruct
	public void validate(){
		initLogging();
		validateIntegrity();
	}

	private void validateIntegrity(){
		try{
			log.debug("Validating program integrity...");

			String[] netIds = netids.split(",");

			for(int i = 0; i < netIds.length; i++){
				netIds[i] = netIds[i].trim();
			}

			for(int i = 0; i < netIds.length; i++){
				String nid = netIds[i];
				try{

					Employee sysadmin = employeeDao.getEmployeeByNetId(nid);

					if(sysadmin == null){
						sysadmin = new Employee();
						sysadmin.setNetId(nid);
						sysadmin.setRiceId("INVALID:" + nid);
						sysadmin.setFirstName("SYSADMIN: " + nid);
						sysadmin.setLastName("SYSADMIN");
						sysadmin.setIsEmployeeActive(true);
						sysadmin.setEmployeeType(EmployeeType.SYSADMIN);
						log.warn("Employee with net ID {} does not exist, but is "
								+ "defined as a Systems Administrator in the "
								+ "configuration file. Now creating a new eployee with "
								+ "the given net ID and the privilege level '{}'",
								nid, EmployeeType.SYSADMIN);
						employeeDao.createEmployee(sysadmin);
					}
					else{
						if(!EmployeeType.SYSADMIN.equals(sysadmin.getEmployeeType())){
							log.warn("Employee with net ID '{}', who is defined as a "
									+ "Systems Administrator in the configuration file, "
									+ "currently only has privilege level '{}'. Now "
									+ "promoting to '{}'", 
									nid, sysadmin.getEmployeeType(), 
									EmployeeType.SYSADMIN);
							sysadmin.setEmployeeType(EmployeeType.SYSADMIN);
							employeeDao.updateEmployee(sysadmin);
						}
					}
				}catch(Exception e){
					log.warn("Exception making sure sysadmin with netid {} exists", nid, e);
				}
			}
			log.info("Done validating program integrity");
		}catch(Exception e){
			log.error("Exception validating program", e);
		}
	}

	private void initLogging(){
		try{
			log.info("Disabling Java Commons Logging for Jersey...");
			java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
			java.util.logging.Handler[] handlers = rootLogger.getHandlers();
			for (int i = 0; i < handlers.length; i++) {
				rootLogger.removeHandler(handlers[i]);
			}
			log.info("Done disabling Java Commons Logging for Jersey, now installing SLF4JBridgeHandler");
			org.slf4j.bridge.SLF4JBridgeHandler.install();
			log.info("Done installing SLF4JBridgeHandler for Jersey logging");
		}catch(Exception e){
			log.warn("Exception disabling JUL and enabling SLF4J for Jersey", e);
		}
	}
}
