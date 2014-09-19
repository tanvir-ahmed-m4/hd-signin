package edu.helpdesk.signin.legacy.dao.mybatis;

import java.util.List;

import edu.helpdesk.signin.legacy.model.LegEmployee;

public interface LegacyEmployeeMapper {
	public List<LegEmployee> getAllEmployees();
}
