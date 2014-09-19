package edu.helpdesk.signin.legacy;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import edu.helpdesk.signin.legacy.dao.mybatis.PrivilegesMapper;
import edu.helpdesk.signin.legacy.model.LegPrivilegeLevel;
import edu.helpdesk.signin.legacy.util.LegacyUtil;
import edu.helpdesk.signin.model.EmployeeType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/edu/helpdesk/signin/applicationContext.xml")
@WebAppConfiguration
public class Importer{
	private Logger log = LoggerFactory.getLogger(Importer.class);
	
	private Boolean isDryRun = true;

	@Autowired
	private PrivilegesMapper mapper;
	
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
		
		// read all employees
		// map all privilege levels to the existing employees, and create new employees for orphan privilige levels
		// write all employees

		log.info("Done importing employees");
	}



}
