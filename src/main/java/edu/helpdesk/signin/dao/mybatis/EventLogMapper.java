package edu.helpdesk.signin.dao.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.helpdesk.signin.model.EventLoggerMessage;

public interface EventLogMapper {
	public List<EventLoggerMessage> getOneHundredMessages(@Param("page") Integer page);
	public void logMessage(@Param("msg") EventLoggerMessage message);
}
