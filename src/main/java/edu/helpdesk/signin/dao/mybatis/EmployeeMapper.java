package edu.helpdesk.signin.dao.mybatis;

import java.util.List;

import edu.helpdesk.signin.model.Employee;

public interface EmployeeMapper {
	Integer createEmployee(Employee template);
	
	List<Employee> getAllEmployees();
	Employee getEmployeeByNetId(String netId);
	Employee getEmployeeByRiceId(String riceId);
	Employee getEmployeeById(Integer id);
	
	void updateEmployee(Employee updated);
	
	void deleteEmployee(Integer employeeId);
}
