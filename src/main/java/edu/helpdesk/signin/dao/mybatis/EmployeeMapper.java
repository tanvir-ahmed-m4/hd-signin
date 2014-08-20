package edu.helpdesk.signin.dao.mybatis;

import java.util.List;

import edu.helpdesk.signin.model.Employee;

public interface EmployeeMapper {
	List<Employee> getAllEmployees();
}
