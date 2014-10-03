package edu.helpdesk.signin.legacy.dao.mybatis;

import java.util.List;

import edu.helpdesk.signin.legacy.model.LegPrivilegeLevel;

public interface PrivilegesMapper {
	public List<LegPrivilegeLevel> getAllPrivilegesMappings();
}
