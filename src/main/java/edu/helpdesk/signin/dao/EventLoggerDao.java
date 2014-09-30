package edu.helpdesk.signin.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.EventLogMapper;
import edu.helpdesk.signin.model.EventLoggerMessage;

public class EventLoggerDao {
	
	@Autowired
	private EventLogMapper mapper;
	
	public List<EventLoggerMessage> getOneHundredMessages(int page){
		return mapper.getOneHundredMessages(page);
	}
	
	
	public void logMessage(EventLoggerMessage msg){
		mapper.logMessage(msg);
	}
	
	
}
