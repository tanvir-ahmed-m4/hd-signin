package edu.helpdesk.signin.web.util;

import java.util.UUID;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebTaskExecutor {
	private static final Logger log = LoggerFactory.getLogger(WebTaskExecutor.class);

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
			log.info("Exception processing web task, likely user error. Tag: {}", UUID.randomUUID(), e);
			return getErrorResponse(e.getLocalizedMessage());
		}catch(Exception e){
			log.warn("Exception processing web task, likely server error. Tag: {}",UUID.randomUUID(), e);
			return Response.serverError().build();
		}
	}

	private static Response getErrorResponse(String msg){
		return Response.ok(new ServerErrorResponse(msg)).status(401).build();
	}



}
