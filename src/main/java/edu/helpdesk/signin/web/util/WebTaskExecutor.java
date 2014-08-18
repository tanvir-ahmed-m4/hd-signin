package edu.helpdesk.signin.web.util;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebTaskExecutor {
	private static final Logger log = LoggerFactory.getLogger(WebTaskExecutor.class);
	
	public static Response doWebTaskSafe(WebTask task){
		try{
			return task.doTask();
		}catch(IllegalArgumentException e){
			log.info("Exception processing web task, likely user error", e);
			return Response.serverError().build();
		}catch(Exception e){
			log.warn("Exception processing web task, likely server error", e);
			return Response.serverError().build();
		}
	}
}
