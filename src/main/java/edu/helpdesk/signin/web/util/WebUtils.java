package edu.helpdesk.signin.web.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebUtils {
	private static final Logger log = LoggerFactory.getLogger(WebUtils.class);
	private static final WebUtils INSTANCE = new WebUtils();


	public static WebUtils get(){
		return INSTANCE;
	}

	private WebUtils(){}

	public String getAuthenticatedUser(HttpServletRequest request){
		String out = null;
		if(request != null)
			out = request.getRemoteUser();
		log.trace("Remote authenticated user for session '{}' is '{}'", getSessionId(request), out);
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
