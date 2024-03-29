package edu.helpdesk.signin.dao.mybatis;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.model.dto.CorrectionRequestDto;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;

public interface SigninMapper {

	List<Employee> getAllSignedInEmployees();
	
	WorkSession doSwipe(@Param("e") Employee e, @Param("date") Date time) throws SQLException;
	
	Integer createRequest(@Param("c") CorrectionRequest c) throws SQLException;
	void applyCorrectionRequest(@Param("c") CorrectionRequest c) throws SQLException;
	void rejectCorrectionRequest(@Param("c") CorrectionRequest c);
	void deleteCorrectionRequest_DEV_ONLY(@Param("id") Integer correctionRequestId);
	
	List<CorrectionRequestDto> getAllCorrectionRequests();
	List<CorrectionRequestDto> getCorrectionRequestsByStatus(@Param("s") List<CorrectionRequestStatus> s);
	CorrectionRequestDto getCorrectionRequestById(@Param("id") Integer id);
	
	List<WorkSession> getAllWorkSessionsForEmployee(@Param("id") Integer id, @Param("after") Date sessionStartsAfter, @Param("before") Date sessionStartsBefore);
	WorkSession getWorkSession(@Param("id") Integer id);

}
