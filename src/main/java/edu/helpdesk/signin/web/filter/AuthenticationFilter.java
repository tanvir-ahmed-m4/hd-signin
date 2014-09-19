package edu.helpdesk.signin.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebUtils;

public class AuthenticationFilter implements Filter{
	private static final Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
	private static int threadId = 0;
	
	private static final String URL_PREFIX = PathConstants.ADMIN_PATH;

	@Autowired
	private EmployeeDao dao;

	public AuthenticationFilter() {}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		autowireCapableBeanFactory.autowireBean(this);
	}

	@Override
	public void doFilter(ServletRequest requestRaw, ServletResponse responseRaw,
			FilterChain chain) throws IOException, ServletException {
		
		if(!(requestRaw instanceof HttpServletRequest)){
			log.warn("Got a request that was not via http, ignoring");
			return;
		}

		HttpServletRequest request = (HttpServletRequest)requestRaw;
		HttpServletResponse response = (HttpServletResponse) responseRaw;
		String path = request.getPathInfo();

		if(path.startsWith(URL_PREFIX)){
			path = path.substring(URL_PREFIX.length());
		}

		String requestedLevel = path.substring(0, path.indexOf('/'));

		EmployeeType neededType = null;

		switch(requestedLevel){
		case PathConstants.ADMIN_SCC_PATH: neededType = EmployeeType.SCC; break;
		case PathConstants.ADMIN_SCC_LEAD_PATH: neededType = EmployeeType.SCC_LEAD; break;
		case PathConstants.ADMIN_SUPERVISOR_PATH: neededType = EmployeeType.SUPERVISOR; break;
		case PathConstants.ADMIN_SYSADMIN_PATH: neededType = EmployeeType.SYSADMIN; break;
		default: neededType = EmployeeType.SYSADMIN; break; /* Sysadmins can do as they please */
		}

		try{
			Employee e = WebUtils.get().getAuthenticatedUser(request);
			Map<String, String> errorMsg = new HashMap<String, String>();
			
			if(e == null){
				errorMsg.put("error", "No employee signed in");
			}
			
			if(errorMsg.size() == 0 && e.getEmployeeType() == null){
				errorMsg.put("error", "Employee type not set");
			}
			
			if(errorMsg.size() == 0 && !e.getIsEmployeeActive()){
				errorMsg.put("error", "Employee is not active");
			}
			
			if(errorMsg.size() == 0 && !e.getEmployeeType().isAboveOrEqualTo(neededType)){
				errorMsg.put("error", String.format("Employee does not have neccessary privilege level. Required level is %s, employee has %s", neededType, e.getEmployeeType()));
			}
			
			if(errorMsg.size() > 0){
				writeResponse(new JSONObject(errorMsg), response);
				return;
			}
		}catch(Exception ex){
			log.error("Exception while filtering", ex);
			response.sendError(500);
			return;
		}
		
		Thread.currentThread().setName(String.format("Ajax request %d", threadId++));
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {}

	private void writeResponse(JSONObject json, HttpServletResponse response){
		try{
			response.setStatus(401);
			response.getOutputStream().write(json.toString().getBytes("UTF8"));
			response.setContentType(MediaType.APPLICATION_JSON);
		}catch(Exception e){
			log.error("Exception writing response", e);
		}
	}

}
