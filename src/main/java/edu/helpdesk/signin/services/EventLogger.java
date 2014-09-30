package edu.helpdesk.signin.services;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.EventLoggerDao;
import edu.helpdesk.signin.model.EventLoggerMessage;

public class EventLogger {
	private static final Logger log = LoggerFactory.getLogger(EventLogger.class);
	
	private static EventLogger INSTANCE = null;
	
	public static EventLogger get(){
		return INSTANCE;
	}
	
	@Autowired
	private EventLoggerDao dao;
	
	public EventLogger() {
		if(INSTANCE == null){
			INSTANCE = this;
		}
	}
	
	
	public void logEvent(String format, Object...args){
		try{
			EventLoggerMessage msg = new EventLoggerMessage();
			msg.setMessage( String.format(format, args));
			msg.setTime(new Date());
			
			log.info(msg.getMessage());
			
			this.dao.logMessage(msg);
		}catch(Exception e){
			log.warn("Exception logging message with format string '{}' and arguments '{}'", format, Arrays.toString(args), e);
		}
	}
	
	
	public List<EventLoggerMessage> getLog(){
		return getLog(0);
	}
	
	public List<EventLoggerMessage> getLog(int page){
		return dao.getOneHundredMessages(page);
	}

}
