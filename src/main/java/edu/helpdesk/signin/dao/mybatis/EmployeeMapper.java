package edu.helpdesk.signin.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.helpdesk.signin.model.dto.Employee;

public interface EmployeeMapper {
	Integer createEmployee(@Param("template") Employee template);
	
	List<Employee> getAllEmployees();
	Employee getEmployeeByNetId(@Param("netId") String netId);
	Employee getEmployeeByRiceId(@Param("riceId") String riceId);
	Employee getEmployeeById(@Param("id") Integer id);
	
	void updateEmployee(@Param("updated") Employee updated);
	
	void deleteEmployee(@Param("employeeId") Integer employeeId);
}
