package edu.helpdesk.signin.legacy.dao.mybatis;

import java.util.List;

import edu.helpdesk.signin.legacy.model.LegSigninData;

public interface LegacySigninMapper {
	List<LegSigninData> getAllSigninData();
}
