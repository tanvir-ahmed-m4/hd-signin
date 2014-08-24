package edu.helpdesk.signin.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.nto.SigninResultErrorNto;
import edu.helpdesk.signin.model.nto.SigninUser;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebTask;
import edu.helpdesk.signin.web.util.WebTaskExecutor;
import edu.helpdesk.signin.web.util.WebUtils;

@Component
@Path(PathConstants.ADMIN_PATH)
public class AdminPageResource {
	@Autowired
	private EmployeeDao employeeDao;


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/currentuser")
	public Response getSignedInUser(@Context final HttpServletRequest request){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				SigninUser user = new SigninUser();
				String netId = WebUtils.get().getAuthenticatedUser(request);

				if(netId != null){
					user.setNetId(netId);
					Employee e = employeeDao.getEmployeeByNetId(netId);
					if(e != null){
						user.setFirstName(e.getFirstName());
						user.setLastName(e.getLastName());
						user.setType(e.getEmployeeType());
					}
				}
				return Response.ok(user).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/forcesignout")
	public Response doForceSignout(final String employeeRiceId){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Employee e = employeeDao.getEmployeeByRiceId(employeeRiceId);

				if(e == null){
					return Response.ok(new SigninResultErrorNto(false, employeeRiceId, "Failed to find an employee with the id " + employeeRiceId + " in the database")).build();
				}

				return null;
			}
		});
	}
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employees")
	public Response getAllEmployees(@QueryParam("filterinactive") final String filterInactiveStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			
			@Override
			public Response doTask() {
				boolean filterInactive = parseBool(filterInactiveStr, true);
				
				List<Employee> employees = employeeDao.getAllEmployees();
				
				List<Employee> out = new ArrayList<>(employees.size());
				
				if(filterInactive){
					for(int i = 0; i < employees.size(); i++){
						Employee e = employees.get(i);
						
						if(e.getIsEmployeeActive()){
							out.add(e);
						}
					}
				}
				else{
					out.addAll(employees);
				}
				
				return Response.ok(out).build();
			}
		});
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/employee")
	public Response updateEmployee(final Employee e){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				employeeDao.updateEmployee(e);
				return Response.ok().build();
			}
		});
	}
	
	
	private boolean parseBool(String val, boolean defaultVal){
		if(Strings.isNullOrEmpty(val))
			return defaultVal;
		return Boolean.parseBoolean(val);
	}
	

}
