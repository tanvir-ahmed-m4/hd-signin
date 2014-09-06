package edu.helpdesk.signin.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.dao.SigninDao;
import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;
import edu.helpdesk.signin.model.nto.SigninResultErrorNto;
import edu.helpdesk.signin.model.nto.SigninResultNto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedINto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedOutNto;
import edu.helpdesk.signin.model.nto.SigninUser;
import edu.helpdesk.signin.services.TimecardFactory;
import edu.helpdesk.signin.web.util.Description;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebTask;
import edu.helpdesk.signin.web.util.WebTaskExecutor;
import edu.helpdesk.signin.web.util.WebUtils;

@Component
@Path(PathConstants.ADMIN_PATH)
public class AdminPageResource {
	private static final String GET_RESOLVED = "includeResolved";


	@Autowired
	private WebUtils utils;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninDao signinDao;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/currentuser")
	@Description("Get the currently signed in user")
	public Response getSignedInUser(@Context final HttpServletRequest request){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				SigninUser user = new SigninUser();
				String netId = utils.getAuthenticatedUserNetid(request);

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
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/togglesignin")
	@Description("Toggle the signin status for the employee with the given Rice ID")
	public Response doToggleSignin(final String employeeRiceId){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Employee e = employeeDao.getEmployeeByRiceId(employeeRiceId);
				SigninResultNto out;

				if(e == null){
					out = new SigninResultErrorNto(false, employeeRiceId, "Failed to find an employee with the id " + employeeRiceId + " in the database");
				}
				else{
					WorkSession session = signinDao.doToggleSigninStatus(e);

					if(session.getSignoutTime() == null){
						out = new SigninResultSwipedINto(e.getFirstName());
					}
					else{
						out = new SigninResultSwipedOutNto((int) (session.getSignoutTime().getTime() - session.getSigninTime().getTime()), 0, "BUTTS", e.getFirstName());
					}
				}
				return Response.ok(out).build();
			}
		});
	}


	////////////////////////////////////////////////////////////////////////////
	////////////////    Correction Request REST functions    ///////////////////
	////////////////////////////////////////////////////////////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/correction/{id}")
	@Description("Get the correction with the given ID")
	public Response getCorrectionRequestById(@PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				Integer id = parseInt(idStr);
				return Response.ok(signinDao.getCorrectionRequest(id)).build();
			}
		});
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/correction")
	@Description("Get all correction requests. if " + GET_RESOLVED + " is set to true, resolved correction requests will be returned. Otherwise, only pending correction requests will be returned")
	public Response getCorrectionRequests(@QueryParam(GET_RESOLVED) final String getResolvedStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				boolean includeResolved = parseBool(getResolvedStr, false);
				return Response.ok(getCorrectionRequestsInternal(includeResolved)).build();
			}
		});
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee/{id}/correction")
	@Description("Get all correction requests for an employee")
	public Response createCorrectionRequest(@PathParam("id") final String idStr, @QueryParam(GET_RESOLVED) final String getResolvedStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				boolean includeResolved = parseBool(getResolvedStr, false);
				int employeeId = parseInt(idStr);
				List<CorrectionRequest> allCorrections = getCorrectionRequestsInternal(includeResolved);
				List<CorrectionRequest> out = new ArrayList<>();

				for(int i = 0; i < allCorrections.size(); i++){
					CorrectionRequest c = allCorrections.get(i);

					if(c.getSubmitter().getId() == employeeId){
						out.add(c);
					}
				}

				return Response.ok(out).build();
			}
		});
	}


	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/correction")
	@Description("Create a new correction request")
	public Response getCorrectionRequestsForEmployee(final CorrectionRequest request){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				return Response.ok(signinDao.getCorrectionRequest(signinDao.createCorrectionRequest(request))).build();
			}
		});
	}



	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/correction/{id}/approve")
	@Description("Approve a correction request")
	public Response approveCorrectionRequest(@Context final HttpServletRequest ajaxRequest, @PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				CorrectionRequest request = signinDao.getCorrectionRequest(parseInt(idStr));
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				if(request == null){
					//TODO error handling
					return null;
				}

				if(e == null){
					//TODO error handling
					return null;
				}

				request.setCompleter(e);
				signinDao.applyCorrectionRequest(request);

				return Response.ok(signinDao.getCorrectionRequest(request.getId())).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/correction/{id}/deny")
	@Description("Deny a correction request")
	public Response denyCorrectionRequest(@Context final HttpServletRequest ajaxRequest, @PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				CorrectionRequest request = signinDao.getCorrectionRequest(parseInt(idStr));
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				if(request == null){
					//TODO error handling
					return null;
				}

				if(e == null){
					//TODO error handling
					return null;
				}

				request.setCompleter(e);
				signinDao.rejectCorrectionRequest(request);

				return Response.ok(signinDao.getCorrectionRequest(request.getId())).build();
			}
		});
	}



	////////////////////////////////////////////////////////////////////////////
	////////////////////    Employee REST functions    /////////////////////////
	////////////////////////////////////////////////////////////////////////////


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee")
	@Description("Get all employees. If filterInactive is set to false, all "
			+ "employees will be returned. Otherwise, only currently active "
			+ "employees are returned")
	public Response getAllEmployees(@QueryParam("filterInactive") final String filterInactiveStr){
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee/{id}")
	@Description("Get the employee with id {id}")
	public Response getEmployee(@PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Integer id = parseInt(idStr);
				return Response.ok(employeeDao.getEmployee(id)).build();
			}
		});
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee")
	@Description("Create a new employee, using the given data")
	public Response createEmployee(final Employee e){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Integer id = employeeDao.createEmployee(e);
				return Response.ok(employeeDao.getEmployee(id)).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/employee")
	@Description("Update an existing employee using the given data")
	public Response updateEmployee(final Employee e){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				employeeDao.updateEmployee(e);
				return Response.ok().build();
			}
		});
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee")
	@Description("Delete an employee with the given ID")
	public Response deleteEmployee(final Integer id){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				employeeDao.deleteEmployee(id);
				return Response.ok().build();
			}
		});
	}


	////////////////////////////////////////////////////////////////////////////
	////////////////////    Timecard REST functions    /////////////////////////
	////////////////////////////////////////////////////////////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/employee/{id}/timecard")
	@Description("Gets a timecard")
	public Response getTimecard(@PathParam("id") final Integer id){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				return Response.ok(TimecardFactory.get().getCurrentTimecard(employeeDao.getEmployee(id))).build();
			}
		});
	}

	

	////////////////////////////////////////////////////////////////////////////
	//////////////////    Internal helper functions    /////////////////////////
	////////////////////////////////////////////////////////////////////////////

	private List<CorrectionRequest> getCorrectionRequestsInternal(boolean includeResolved){
		List<CorrectionRequest> out;

		if(includeResolved){
			out = signinDao.getAllCorrectionRequests();
		}
		else{
			out = signinDao.getAllPendingCorrectionRequests();
		}

		return out;
	}


	private Integer parseInt(String val){
		try{
			return Integer.parseInt(val);
		}catch(NumberFormatException e){
			String errMsg = "Cannot parse '" + val + "' into an integer";
			throw new IllegalArgumentException(errMsg);
		}
	}

	private boolean parseBool(String val, boolean defaultVal){
		if("false".equalsIgnoreCase(val))
			return false;
		if("true".equalsIgnoreCase(val))
			return true;
		return defaultVal;
	}
}
