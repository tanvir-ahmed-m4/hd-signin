package edu.helpdesk.signin.dao;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/edu/helpdesk/signin/applicationContext.xml")
@WebAppConfiguration
public class SigninDaoTest {
	private static final Logger log = LoggerFactory.getLogger(SigninDaoTest.class);
	private static final String TEST_EMPLOYEE_NETID = "test-netid";

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninDao dao;

	private Employee employee;

	@Test
	public void runTests() throws Exception{
		try{
			this.employee = createTestEmployee();
			testDoSwipe();

			testCorrectionRequestFunctions();

			return;
		}catch(Exception e){
			log.error("Exception running tests, tests failed", e);
			fail("Exception: " + e.getLocalizedMessage());
		}finally{
			try{
				deleteTestEmployee();
			}catch(Exception e){
				log.warn("Failed to delete test employee", e);
			}
		}
	}


	private void testCorrectionRequestFunctions() throws Exception{
		dao.doToggleSigninStatus(employee);
		log.info("Sleeping for 5 seconds to create a signin time");
		Thread.sleep(5000);
		dao.doToggleSigninStatus(employee);


		List<WorkSession> sessions = dao.getAllWorkSessionsForEmployee(employee.getId());

		if(sessions.size() == 0){
			fail("Failed to find any sessions for employee, despite creating one");
		}

		WorkSession target = sessions.get(sessions.size() - 1);
		CorrectionRequest request = new CorrectionRequest(employee, target.getId(), target.getSigninTime(), new Date());
		
		Integer id = dao.createCorrectionRequest(request);
		List<CorrectionRequest> requests = dao.getAllCorrectionRequests();
		
		log.info("Created request: {}", requests);
		//dao.getAllPendingCorrectionRequests();

		//dao.applyCorrectionRequest(request);



		//dao.createCorrectionRequest(request);

		//dao.getAllPendingCorrectionRequests();

		//dao.rejectCorrectionRequest(request);


		//dao.getAllPendingCorrectionRequests();
		//dao.getAllCorrectionRequests();
		//dao.getAllCompletedCorrectionRequests();
	}


	private void testDoSwipe() throws Exception{
		validateJustSignedInWorkSession(dao.doToggleSigninStatus(employee));

		log.info("Sleeping for 2 seconds to ensure difference in signin and signout times");
		Thread.sleep(1000 * 2);
		validateJustSignedOutWorkSession(dao.doToggleSigninStatus(employee));
	}

	private void validateJustSignedInWorkSession(WorkSession session){
		//TODO
	}

	private void validateJustSignedOutWorkSession(WorkSession session){
		//TODO
	}


	private Employee createTestEmployee(){
		Employee e = employeeDao.getEmployeeByNetId(TEST_EMPLOYEE_NETID);
		if(e == null){
			e = new Employee();
			e.setNetId(TEST_EMPLOYEE_NETID);
			e.setEmployeeType(EmployeeType.SCC);
			e.setFirstName("FIRST NAME");
			e.setLastName("LAST NAME");
			e.setIsEmployeeActive(true);
			e.setRiceId("INVALID_RICE_ID");
			e = employeeDao.getEmployee(employeeDao.createEmployee(e));
		}
		return e;
	}

	private void deleteTestEmployee(){
		Employee e = employeeDao.getEmployeeByNetId(TEST_EMPLOYEE_NETID);
		employeeDao.deleteEmployee(e.getId());
	}


}
