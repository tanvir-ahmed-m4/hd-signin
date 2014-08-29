package edu.helpdesk.signin.web.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.model.dto.Employee;

public class WebUtils {
	private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
	private static WebUtils INSTANCE;

	public static WebUtils get(){
		if(INSTANCE == null){
			throw new NullPointerException("No instance has been set, check the Spring configuration");
		}
		return INSTANCE;
	}

	@Autowired
	private EmployeeDao dao;
	
	
	private WebUtils(){
		INSTANCE = this;
	}

	public String getAuthenticatedUserNetid(HttpServletRequest request){
		String out = null;
		if(request != null)
			out = request.getRemoteUser();
		log.trace("Remote authenticated user for session '{}' is '{}'", getSessionId(request), out);
		return out;
	}
	
	public Employee getAuthenticatedUser(HttpServletRequest request){
		String netId = this.getAuthenticatedUserNetid(request);
		Employee out = null;
		
		if(netId != null){
			out = dao.getEmployeeByNetId(netId);
		}
		
		return out;
	}

	public String getSessionId(HttpServletRequest request){
		String out = null;
		if(request != null){
			if(request.getSession() != null){
				out = request.getSession().getId();
			}
		}
		return out;
	}

	public void logoutUser(HttpServletRequest request){
		try{
			request.getSession().invalidate();
		}catch(Exception e){
			log.warn("Exception invalidating session", e);
		}
	}
}
