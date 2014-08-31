package edu.helpdesk.signin.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Preconditions;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
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
			
			log.debug("TESTING SWIPE");
			testDoSwipe();
			log.debug("END TESTING SWIPE");

			log.debug("TESTING CORRECTION FUNCTIONS");
			testCorrectionRequestFunctions();
			log.debug("END TESTING CORRECTION FUNCTIONS");
		}catch(Exception e){
			log.error("Exception running tests, tests failed", e);
			fail("Exception: " + e.getLocalizedMessage());
		}finally{
			log.debug("Cleaning up...");
			try{
				deleteTestEmployee();
			}catch(Exception e){
				log.warn("Failed to delete test employee", e);
			}
			log.debug("Done cleaning up");
		}
	}


	private void testCorrectionRequestFunctions() throws Exception{
		dao.doToggleSigninStatus(employee);
		log.info("Sleeping for 2 seconds to create a signin time");
		Thread.sleep(2000);
		dao.doToggleSigninStatus(employee);

		List<WorkSession> sessions = dao.getAllWorkSessionsForEmployee(employee.getId());

		if(sessions.size() == 0){
			fail("Failed to find any sessions for employee, despite creating one");
		}

		WorkSession target = sessions.get(sessions.size() - 1);
		// Test correction approval
		// TODO in the future, the employee may not be allowed to complete their own requests
		CorrectionRequest aRequest  = new CorrectionRequest(employee, target.getId(), target.getSigninTime(), new Date(System.currentTimeMillis() + 1000 * 60 * 10));
		CorrectionRequest createdARequest = dao.getCorrectionRequest(dao.createCorrectionRequest(aRequest));
		
		validateNewlyCreatedCorrectionRequest(aRequest, createdARequest);
		
		ensureContains(dao.getAllPendingCorrectionRequests(), createdARequest);
		ensureContains(dao.getAllCorrectionRequests(), createdARequest);
		ensureDoesNotContain(dao.getAllCompletedCorrectionRequests(), createdARequest);
		
		createdARequest.setCompleter(employee);
		dao.applyCorrectionRequest(createdARequest);
		createdARequest = dao.getCorrectionRequest(createdARequest.getId());
		ensureCorrectionRequestApplied(createdARequest, target, dao.getWorkSession(target.getId()));
		// Done testing correction approval
		
		// Test correction rejection
		target = dao.getWorkSession(target.getId());
		
		CorrectionRequest rRequest  = new CorrectionRequest(employee, target.getId(), new Date(System.currentTimeMillis() - 1000 * 60 * 10), target.getSignoutTime());
		CorrectionRequest createdRRequest = dao.getCorrectionRequest(dao.createCorrectionRequest(rRequest));
		
		validateNewlyCreatedCorrectionRequest(rRequest, createdRRequest);
		
		ensureContains(dao.getAllPendingCorrectionRequests(), createdRRequest);
		ensureContains(dao.getAllCorrectionRequests(), createdRRequest);
		ensureDoesNotContain(dao.getAllCompletedCorrectionRequests(), createdRRequest);
		
		createdRRequest.setCompleter(employee);
		dao.rejectCorrectionRequest(createdRRequest);
		createdRRequest = dao.getCorrectionRequest(createdRRequest.getId());
		ensureCorrectionRequestRejected(createdRRequest, target, dao.getWorkSession(target.getId()));
		// end of ensure rejected

		
		// ensure that the "get all" methods work as expected
		ensureContains(dao.getAllCorrectionRequests(), createdRRequest);
		ensureContains(dao.getAllCorrectionRequests(), createdARequest);
		
		ensureDoesNotContain(dao.getAllPendingCorrectionRequests(), createdRRequest);
		ensureDoesNotContain(dao.getAllPendingCorrectionRequests(), createdARequest);
		
		ensureContains(dao.getAllCompletedCorrectionRequests(), createdRRequest);
		ensureContains(dao.getAllCompletedCorrectionRequests(), createdARequest);
	}
	
	private void ensureCorrectionRequestApplied(CorrectionRequest request, WorkSession originalSession, WorkSession newSession){
		ensureCorrectionHasDifferentTime(request, originalSession);
		Preconditions.checkArgument(difference(request.getNewSigninTime(), newSession.getSigninTime()) < 10000, "Correction request was not succesfully applied (" + getExpectedString(request.getNewSigninTime(), newSession.getSigninTime()) + ")");
		Preconditions.checkArgument(difference(request.getNewSignoutTime(), newSession.getSignoutTime()) < 10000, "Correction request was not succesfully applied (" + getExpectedString(request.getNewSignoutTime(), newSession.getSignoutTime()) + ")");
	}
	
	private void ensureCorrectionRequestRejected(CorrectionRequest request, WorkSession originalSession, WorkSession newSession){
		ensureCorrectionHasDifferentTime(request, originalSession);
		Preconditions.checkArgument(difference(originalSession.getSigninTime(), newSession.getSigninTime()) < 10000, "Correction request was applied when it should not have been (" + getExpectedString(originalSession.getSigninTime(), newSession.getSigninTime()) + ")");
		Preconditions.checkArgument(difference(originalSession.getSignoutTime(), newSession.getSignoutTime()) < 10000, "Correction request was applied when it should not have been (" + getExpectedString(originalSession.getSignoutTime(), newSession.getSignoutTime()) + ")");
	}
	
	private String getExpectedString(Date d1, Date d2){
		return String.format("Expected %s, got %s", d1, d2);
	}
	
	private long difference(Date d1, Date d2){
		Preconditions.checkArgument(d1 != null && d2 != null, "Dates cannot be null");
		return Math.abs(d1.getTime() - d2.getTime());
	}
	
	private void ensureCorrectionHasDifferentTime(CorrectionRequest request, WorkSession session){
		// if either the signin time or signout time differ, then the request is valid
		if(difference(request.getNewSigninTime(), session.getSigninTime()) > 1000){
			return;
		}
		
		if(difference(request.getNewSignoutTime(), session.getSignoutTime()) > 1000){
			return;
		}
		
		String diff = "Session: " + session.getSigninTime() + " -> " + session.getSignoutTime() + ", request: " + request.getNewSigninTime() + " -> " + request.getNewSignoutTime();
		fail("The given correction request does not actually change the signin data, so the test can't verify that the changes work (" + diff + ")");
	}
	
	private void ensureContains(Collection<CorrectionRequest> requests, CorrectionRequest r){
		for(CorrectionRequest cr : requests){
			if(cr.getId().equals(r.getId()))
				return;
		}
		throw new NoSuchElementException("List of correction requests does not contain a correction request it should");
	}
	
	private void ensureDoesNotContain(Collection<CorrectionRequest> requests, CorrectionRequest r){
		for(CorrectionRequest cr : requests){
			if(cr.getId().equals(r.getId()))
				throw new IllegalArgumentException("Collection contained correction request it shouldn't have (id " + r.getId() + ")");
		}
	}

	
	private void validateNewlyCreatedCorrectionRequest(CorrectionRequest expected, CorrectionRequest actual){
		Preconditions.checkNotNull(actual, "Created correction request was null");
		Preconditions.checkArgument(actual.getCompleter() == null," Newly created correction request already has a completer");
		Preconditions.checkArgument(actual.getId() != null && actual.getId() > 0, "Created correction request has invalid id");
		assertTrue("New Signin times differ", difference(expected.getNewSigninTime(), actual.getNewSigninTime()) < 1000);
		assertTrue("New Signout times differ", difference(expected.getNewSignoutTime(), actual.getNewSignoutTime()) < 1000);
		assertTrue("Original Signin times incorect", actual.getOriginalSigninTime() != null);
		assertTrue("Orinal Signout times incorect", actual.getOriginalSignoutTime() != null);
		assertEquals("Signin data IDs differ", expected.getSigninId(), actual.getSigninId());
		assertEquals("Status is incorrect", CorrectionRequestStatus.PENDING, actual.getStatus());
		assertEquals("Submitter differs", expected.getSubmitter(), actual.getSubmitter());
	}
	

	private void testDoSwipe() throws Exception{
		validateJustSignedInWorkSession(dao.doToggleSigninStatus(employee));

		log.info("Sleeping for 2 seconds to ensure difference in signin and signout times");
		Thread.sleep(1000 * 2);
		validateJustSignedOutWorkSession(dao.doToggleSigninStatus(employee));
	}

	private void validateJustSignedInWorkSession(WorkSession session){
		Preconditions.checkArgument(session.getSigninTime() != null, "Signin time should not be null");
		Preconditions.checkArgument(difference(session.getSigninTime(), new Date()) < 10000, "Signin time invalid (this can be cause by a very slow network connection)");
		Preconditions.checkArgument(session.getSignoutTime() == null, "Signout time should be null");
	}

	private void validateJustSignedOutWorkSession(WorkSession session){
		Preconditions.checkArgument(session.getSigninTime() != null, "Signin time should not be null");
		Preconditions.checkArgument(session.getSignoutTime() != null, "Signout time should not be null");
		Preconditions.checkArgument(difference(session.getSignoutTime(), new Date()) < 10000, "Signout time invalid (this can be cause by a very slow network connection)");
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
