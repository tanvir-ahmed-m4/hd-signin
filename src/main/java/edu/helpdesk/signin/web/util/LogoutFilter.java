package edu.helpdesk.signin.web.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutFilter implements Filter {
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest requestRaw, ServletResponse responseRaw,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) requestRaw;
		HttpServletResponse response = (HttpServletResponse) responseRaw;
		
		// invalidate the user's session
		request.getSession().invalidate();
		
		// send them to the CAS logout page
		response.setStatus(302);
		response.setHeader("Location", "https://netid.rice.edu/cas/logout?" + request.getQueryString());
	}

	@Override
	public void destroy() {
		
	}

}
