package edu.helpdesk.signin.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.EmployeeMapper;
import edu.helpdesk.signin.model.Employee;


public class EmployeeDao {
	private static final Logger log = LoggerFactory.getLogger(EmployeeDao.class);
	
	@Autowired
	private EmployeeMapper mapper;
	
	public EmployeeDao() {
		log.debug("Employee DAO created");
	}
	
	public Integer createEmployee(Employee template){
		return mapper.createEmployee(template);
	}
	
	public void updateEmployee(Employee e){
		mapper.updateEmployee(e);
	}
	
	public List<Employee> getAllEmployees(){
		return mapper.getAllEmployees();
	}
	
	public Employee getEmployee(Integer id){
		return mapper.getEmployeeById(id);
	}
	
	public Employee getEmployeeByRiceId(String riceID){
		return mapper.getEmployeeByRiceId(riceID);
	}
	
	public Employee getEmployeeByNetId(String netID){
		return mapper.getEmployeeByNetId(netID);
	}
	
	public void deleteEmployee(Integer employeeId){
		mapper.deleteEmployee(employeeId);
	}
}
