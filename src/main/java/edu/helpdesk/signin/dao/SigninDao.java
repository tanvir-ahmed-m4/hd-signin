package edu.helpdesk.signin.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import edu.helpdesk.signin.dao.mybatis.SigninMapper;
import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;
import static edu.helpdesk.signin.model.CorrectionRequestStatus.*;

public class SigninDao {

	@Autowired
	private SigninMapper mapper;

	public List<Employee> getAllSignedInEmployees(){
		return mapper.getAllSignedInEmployees();
	}

	public WorkSession doToggleSigninStatus(Employee e){
		return mapper.doSwipe(e, new Date());
	}

	public Integer createCorrectionRequest(CorrectionRequest request){
		request.setCompleter(null);
		validateCorrectionRequest(request, false, false);
		checkStatus(request, PENDING);
		mapper.createRequest(request);
		return request.getId();
	}

	public void applyCorrectionRequest(CorrectionRequest request){
		validateCorrectionRequest(request, true, true);
		checkStatus(request, PENDING);
		mapper.applyCorrectionRequest(request);
	}

	public void rejectCorrectionRequest(CorrectionRequest request){
		validateCorrectionRequest(request, true, true);
		checkStatus(request, PENDING);
		mapper.rejectCorrectionRequest(request);
	}

	public List<CorrectionRequest> getAllCorrectionRequests(){
		return mapper.getAllCorrectionRequests();
	}

	public List<CorrectionRequest> getAllPendingCorrectionRequests(){
		return mapper.getCorrectionRequestsByStatus(Arrays.asList(CorrectionRequestStatus.PENDING));
	}

	public List<CorrectionRequest> getAllCompletedCorrectionRequests(){
		return mapper.getCorrectionRequestsByStatus(Arrays.asList(CorrectionRequestStatus.APPROVED, CorrectionRequestStatus.DENIED));
	}

	public List<WorkSession> getAllWorkSessionsForEmployee(Integer employeeId, Date date, Date date2) {
		return mapper.getAllWorkSessionsForEmployee(employeeId, date, date);
	}
	
	public List<WorkSession> getAllWorkSessionsForEmployee(Integer employeeId) {
		return mapper.getAllWorkSessionsForEmployee(employeeId, new Date(0), new Date(System.currentTimeMillis()));
	}
	
	public void deleteCorrectionRequest_DEV_ONLY(Integer id){
		validateRequestId(id);
		mapper.deleteCorrectionRequest_DEV_ONLY(id);
	}
	
	private void checkStatus(CorrectionRequest c, CorrectionRequestStatus s){
		Preconditions.checkArgument(s.equals(c.getStatus()), "Correction request must have status of " + s.name());
	}

	private void validateCorrectionRequest(CorrectionRequest r, boolean validateId, boolean checkCompleter){
		if(validateId){
			validateRequestId(r.getId());
		}
		
		if(checkCompleter){
			Preconditions.checkArgument(r.getCompleter() != null, "Correction Request completer cannot be null");
			validateRequestId(r.getCompleter().getId());
		}
		
		validateRequestId(r.getSigninId());
		
		Preconditions.checkArgument(r.getNewSigninTime() != null, "New signin time cannot be null");
		Preconditions.checkArgument(r.getNewSignoutTime() != null, "New signout time cannot be null");
		Preconditions.checkArgument(r.getNewSigninTime().before(r.getNewSignoutTime()), "New signout time must be after new signin time");
		
		//Preconditions.checkArgument(r.getOriginalSigninTime() != null, "Original signin time cannot be null");
		//Preconditions.checkArgument(r.getOriginalSignoutTime() != null, "Original signout time cannot be null");
		//Preconditions.checkArgument(r.getOriginalSigninTime().before(r.getOriginalSignoutTime()), "Original signout time must be after original signin time");
		
		Preconditions.checkArgument(r.getStatus() != null, "Correction request status cannot be null");
		
		Preconditions.checkArgument(r.getSubmitter() != null, "Submitter cannot be null");
		validateRequestId(r.getSubmitter().getId());
	}

	private void validateRequestId(Integer id){
		Preconditions.checkArgument(id != null, "ID cannot be null");
		validateRequestId(id.intValue());
	}

	private void validateRequestId(int id){
		Preconditions.checkArgument(id > 0, "ID must be greater than 0 (got " + id + ")");
	}
	
}
