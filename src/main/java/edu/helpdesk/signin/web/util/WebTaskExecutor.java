package edu.helpdesk.signin.web.util;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebTaskExecutor {
	private static final Logger log = LoggerFactory.getLogger(WebTaskExecutor.class);

	private static final int BAD_REQUEST = 400;
	private static final int SERVER_ERROR = 500;
	
	private static class ServerErrorResponse{
		private String error;

		public ServerErrorResponse(String msg){
			this.error = msg;
		}

		@SuppressWarnings("unused")
		public String getError() {
			return error;
		}

		@SuppressWarnings("unused")
		public void setError(String error) {
			this.error = error;
		}
	}

	public static Response doWebTaskSafe(WebTask task){
		try{
			return task.doTask();
		}catch(IllegalArgumentException e){
			log.info("Exception processing web task, likely user error: {}", e.getLocalizedMessage());
			return getErrorResponse(e.getLocalizedMessage());
		}catch(Exception e){
			String tag = UUID.randomUUID().toString();
			log.warn("Exception processing web task, likely server error. Tag: {}", tag, e);
			return Response.ok(new ServerErrorResponse(String.format("Internal Server error (tag %s)", tag))).status(SERVER_ERROR).build();
		}
	}

	private static Response getErrorResponse(String msg){
		return Response.ok(new ServerErrorResponse(msg)).status(BAD_REQUEST).build();
	}



}
