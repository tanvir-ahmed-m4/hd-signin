package edu.helpdesk.signin.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.helpdesk.signin.model.EventLoggerMessage;

public class EventLogger {
	private static final Logger log = LoggerFactory.getLogger(EventLogger.class);
	
	private static EventLogger INSTANCE = null;
	
	public static EventLogger get(){
		if(INSTANCE == null){
			INSTANCE = new EventLogger();
		}
		return INSTANCE;
	}
	
	private int logSize = 1000;
	
	private List<EventLoggerMessage> messages = new LinkedList<>();
	
	public EventLogger() {
		if(INSTANCE == null){
			INSTANCE = this;
		}
	}
	
	
	public void logEvent(String format, Object...args){
		try{
			Date time = new Date();
			String msg = String.format(format, args);
			log.info(msg);
			
			this.messages.add(new EventLoggerMessage(time, msg));
			
			if(this.messages.size() > logSize){
				this.messages.remove(0);
			}
		}catch(Exception e){
			log.warn("Exception logging message with format string '{}' and arguments '{}'", format, Arrays.toString(args), e);
		}
	}
	
	
	public List<EventLoggerMessage> getLog(){
		List<EventLoggerMessage> out = new ArrayList<>();
		out.addAll(this.messages);
		return out;
	}

}
