package edu.helpdesk.signin.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.SigninMapper;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;

public class SigninDao {
	
	@Autowired
	private SigninMapper mapper;
	
	public WorkSession swipe(Employee e){
		return mapper.doSwipe(e, new Date());
	}
	
	public List<Employee> getAllSignedInEmployees(){
		return mapper.getAllSignedInEmployees();
	}
}
