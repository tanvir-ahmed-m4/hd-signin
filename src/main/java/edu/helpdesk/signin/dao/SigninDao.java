package edu.helpdesk.signin.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import edu.helpdesk.signin.dao.mybatis.SigninMapper;
import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.model.dto.CorrectionRequestDto;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;
import static edu.helpdesk.signin.model.CorrectionRequestStatus.*;

public class SigninDao {

	@Autowired
	private SigninMapper mapper;

	@Autowired
	private EmployeeDao edao;

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
		return mapper.createRequest(request);
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

	public CorrectionRequest getCorrectionRequest(Integer id){
		this.validateId(id);
		return convert(mapper.getCorrectionRequestById(id));
	}

	public List<CorrectionRequest> getAllCorrectionRequests(){
		return convert(mapper.getAllCorrectionRequests());
	}

	public List<CorrectionRequest> getAllPendingCorrectionRequests(){
		return convert(mapper.getCorrectionRequestsByStatus(Arrays.asList(CorrectionRequestStatus.PENDING)));
	}

	public List<CorrectionRequest> getAllCompletedCorrectionRequests(){
		return convert(mapper.getCorrectionRequestsByStatus(Arrays.asList(CorrectionRequestStatus.APPROVED, CorrectionRequestStatus.DENIED)));
	}

	public List<WorkSession> getAllWorkSessionsForEmployee(Integer employeeId, Date date, Date date2) {
		return mapper.getAllWorkSessionsForEmployee(employeeId, date, date);
	}

	public List<WorkSession> getAllWorkSessionsForEmployee(Integer employeeId) {
		return mapper.getAllWorkSessionsForEmployee(employeeId, new Date(0), new Date(System.currentTimeMillis()));
	}

	public void deleteCorrectionRequest_DEV_ONLY(Integer id){
		validateId(id);
		mapper.deleteCorrectionRequest_DEV_ONLY(id);
	}

	private void checkStatus(CorrectionRequest c, CorrectionRequestStatus s){
		Preconditions.checkArgument(s.equals(c.getStatus()), "Correction request must have status of " + s.name());
	}

	private void validateCorrectionRequest(CorrectionRequest r, boolean validateId, boolean checkCompleter){
		if(validateId){
			validateId(r.getId());
		}

		if(checkCompleter){
			Preconditions.checkArgument(r.getCompleter() != null, "Correction Request completer cannot be null");
			validateId(r.getCompleter().getId());
		}

		validateId(r.getSigninId());

		Preconditions.checkArgument(r.getNewSigninTime() != null, "New signin time cannot be null");
		Preconditions.checkArgument(r.getNewSignoutTime() != null, "New signout time cannot be null");
		Preconditions.checkArgument(r.getNewSigninTime().before(r.getNewSignoutTime()), "New signout time must be after new signin time");

		//Preconditions.checkArgument(r.getOriginalSigninTime() != null, "Original signin time cannot be null");
		//Preconditions.checkArgument(r.getOriginalSignoutTime() != null, "Original signout time cannot be null");
		//Preconditions.checkArgument(r.getOriginalSigninTime().before(r.getOriginalSignoutTime()), "Original signout time must be after original signin time");

		Preconditions.checkArgument(r.getStatus() != null, "Correction request status cannot be null");

		Preconditions.checkArgument(r.getSubmitter() != null, "Submitter cannot be null");
		validateId(r.getSubmitter().getId());
	}

	private void validateId(Integer id){
		Preconditions.checkArgument(id != null, "ID cannot be null");
		validateId(id.intValue());
	}

	private void validateId(int id){
		Preconditions.checkArgument(id > 0, "ID must be greater than 0 (got " + id + ")");
	}

	private List<CorrectionRequest> convert(List<CorrectionRequestDto> dtos){
		List<CorrectionRequest> out = new ArrayList<>(dtos.size());
		for(CorrectionRequestDto r : dtos){
			out.add(convert(r));
		}
		return out;
	}

	private CorrectionRequest convert(CorrectionRequestDto dto){
		Employee submitter = dto.getSubmitter() == 0 ? null : edao.getEmployee(dto.getSubmitter());
		Employee completer = dto.getCompleter() == 0 ? null : edao.getEmployee(dto.getCompleter());
		return dto.toCorrectionRequest(submitter, completer);
	}

	public WorkSession getWorkSession(Integer id) {
		validateId(id);
		return mapper.getWorkSession(id);
	}

}
