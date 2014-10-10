package edu.helpdesk.signin.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import edu.helpdesk.signin.dao.mybatis.EmployeeMapper;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;


public class EmployeeDao {
	private static final Logger log = LoggerFactory.getLogger(EmployeeDao.class);
	
	@Autowired
	private EmployeeMapper mapper;
	
	public EmployeeDao() {
		log.debug("Employee DAO created");
	}
	
	public Integer createEmployee(Employee template){
		validateEmployee(template, false);
		mapper.createEmployee(template);
		return Integer.valueOf(template.getId());
	}
	
	public void updateEmployee(Employee e){
		validateEmployee(e, true);
		mapper.updateEmployee(e);
	}
	
	public List<Employee> getAllEmployees(){
		return mapper.getAllEmployees();
	}
	
	public List<Employee> getAllEmployeesAtOrAboveLevel(EmployeeType type, boolean includeInactive){
		List<Employee> raw = this.getAllEmployees();
		List<Employee> out = new ArrayList<Employee>();
		
		for(Employee e : raw){
			if(e.getIsEmployeeActive() || includeInactive){
				if(e.getEmployeeType().isAboveOrEqualTo(type)){
					out.add(e);
				}
			}
		}
		
		return out;
	}
	
	public Employee getEmployee(Integer id){
		validateId(id);
		return mapper.getEmployeeById(id);
	}
	
	public Employee getEmployeeByRiceId(String riceID){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(riceID), "Employee Rice ID cannot be null or empty");
		return mapper.getEmployeeByRiceId(riceID);
	}
	
	public Employee getEmployeeByNetId(String netID){
		Preconditions.checkArgument(!Strings.isNullOrEmpty(netID), "Employee Net ID cannot be null or empty");
		return mapper.getEmployeeByNetId(netID);
	}
	
	public void deleteEmployee(Integer employeeId){
		validateId(employeeId);
		mapper.deleteEmployee(employeeId);
	}
	
	private void validateId(Integer id){
		Preconditions.checkArgument(id != null, "Employee ID cannot be null");
		Preconditions.checkArgument(id.intValue() > 0, "Employee ID must be greater than 0");
	}
	
	private void validateEmployee(Employee e, boolean checkId){
		Preconditions.checkArgument(e != null, "Employee cannot be null");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(e.getFirstName()), "Employee First name cannot be null or empty");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(e.getLastName()), "Employee last name cannot be null or empty");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(e.getNetId()), "Employee Net ID cannot be null or empty");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(e.getRiceId()), "Employee Rice ID cannot be null or empty");
		Preconditions.checkArgument(null != e.getEmployeeType(), "Employee type cannot be null");
		
		if(checkId){
			validateId(e.getId());
		}
	}
}
