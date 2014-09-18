package edu.helpdesk.signin.model;

import java.util.Date;

public class EventLoggerMessage {
	private Date time;
	private String message;
	
	public EventLoggerMessage() {
	}
	
	public EventLoggerMessage(Date time, String message) {
		this.time = time;
		this.message = message;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
