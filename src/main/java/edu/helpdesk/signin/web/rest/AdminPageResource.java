package edu.helpdesk.signin.web.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.dao.PayPeriodDao;
import edu.helpdesk.signin.dao.SigninDao;
import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.PeriodEnd;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;
import edu.helpdesk.signin.model.nto.SigninResultErrorNto;
import edu.helpdesk.signin.model.nto.SigninResultNto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedINto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedOutNto;
import edu.helpdesk.signin.model.nto.SigninUser;
import edu.helpdesk.signin.services.EventLogger;
import edu.helpdesk.signin.services.TimecardFactory;
import edu.helpdesk.signin.util.AuthenticationUtil;
import edu.helpdesk.signin.util.SigninUtil;
import edu.helpdesk.signin.web.util.Description;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebTask;
import edu.helpdesk.signin.web.util.WebTaskExecutor;
import edu.helpdesk.signin.web.util.WebUtils;

@Component
@Path(PathConstants.ADMIN_PATH)
public class AdminPageResource {
	private static final Logger log = LoggerFactory.getLogger(AdminPageResource.class);
	private static final String GET_RESOLVED = "includeResolved";
	private static final String START_DATE = "periodStart";
	private static final String END_DATE = "periodEnd";


	@Autowired
	private EventLogger logger;
	
	@Autowired
	private WebUtils utils;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninDao signinDao;

	@Autowired
	private PayPeriodDao payPeriodDao;

	public AdminPageResource() {
		log.info("Administrative page resource created");
	}



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
						return Response.ok(e).build();
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

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/finger")
	@Description("Fingers")
	public Response finger(@QueryParam("netid") final String netid){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() throws Exception {
				return Response.ok(fingerInternal(netid)).build();
			}

		});
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/log")
	@Description("Gets event log")
	public Response getEventLog(@QueryParam("page") final String pageStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() throws Exception {
				int page;
				if(pageStr != null){
					page = parseInt(pageStr);
				}
				else{
					page = 0;
				}
				return Response.ok(logger.getLog(page)).build();
			}

		});
	}


	////////////////////////////////////////////////////////////////////////////
	////////////////    Correction Request REST functions    ///////////////////
	////////////////////////////////////////////////////////////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/correction/{id}")
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
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/correction")
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
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/employee/{id}/correction")
	@Description("Get all correction requests for an employee")
	public Response createCorrectionRequest(@Context final HttpServletRequest request, @PathParam("id") final String idStr, @QueryParam(GET_RESOLVED) final String getResolvedStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() throws Exception {
				boolean includeResolved = parseBool(getResolvedStr, false);
				int employeeId = parseInt(idStr);

				Employee signedIn = utils.getAuthenticatedUser(request);
				Preconditions.checkArgument(signedIn != null, "Signed in user is not an employee");

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
				request.setStatus(CorrectionRequestStatus.PENDING);
				CorrectionRequest out = signinDao.getCorrectionRequest(signinDao.createCorrectionRequest(request));
				
				logger.logEvent("%s submitted a correction request with id %d",
						buildFullName(request.getSubmitter()), out.getId());
				
				return Response.ok(out).build();
			}
		});
	}



	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/correction/{id}/approve")
	@Description("Approve a correction request")
	public Response approveCorrectionRequest(@Context final HttpServletRequest ajaxRequest, @PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				CorrectionRequest request = signinDao.getCorrectionRequest(parseInt(idStr));
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				Preconditions.checkArgument(e != null, "Signed in user is not an employee");
				Preconditions.checkArgument(request != null, "No request with id " + idStr + " in database");

				request.setCompleter(e);
				signinDao.applyCorrectionRequest(request);
				CorrectionRequest out = signinDao.getCorrectionRequest(request.getId());
				
				logger.logEvent("%s approved correction request %d for %s",
						buildFullName(out.getCompleter()), out.getId(), buildFullName(out.getSubmitter()));
				
				return Response.ok(out).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/correction/{id}/deny")
	@Description("Deny a correction request")
	public Response denyCorrectionRequest(@Context final HttpServletRequest ajaxRequest, @PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				CorrectionRequest request = signinDao.getCorrectionRequest(parseInt(idStr));
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				Preconditions.checkArgument(e != null, "Signed in user is not an employee");
				Preconditions.checkArgument(request != null, "No request with id " + idStr + " in database");

				request.setCompleter(e);
				signinDao.rejectCorrectionRequest(request);

				CorrectionRequest out = signinDao.getCorrectionRequest(request.getId());
				logger.logEvent("%s denied correction request %d for %s",
						buildFullName(out.getCompleter()), out.getId(), buildFullName(out.getSubmitter()));
				
				return Response.ok(out).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/correction/{id}/cancel")
	@Description("ALlows a user to cancel their own orrection request")
	public Response cancelCorrectionRequest(@Context final HttpServletRequest ajaxRequest, @PathParam("id") final String idStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				CorrectionRequest request = signinDao.getCorrectionRequest(parseInt(idStr));
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				Preconditions.checkArgument(e != null, "Signed in user is not an employee");
				Preconditions.checkArgument(request != null, "No request with id " + idStr + " in database");
				Preconditions.checkArgument(e.getId() == request.getSubmitter().getId(), "Cannot cancel another employee's correction request");

				request.setCompleter(e);
				signinDao.rejectCorrectionRequest(request);
				
				CorrectionRequest out = signinDao.getCorrectionRequest(request.getId());
				
				logger.logEvent("%s cancelled correction request %d", buildFullName(out.getSubmitter()), out.getId());
				
				return Response.ok(out).build();
			}
		});
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/correction/own")
	@Description("ALlows a user to get their own correction requests")
	public Response getOwnCorrectionRequests(@Context final HttpServletRequest ajaxRequest, @QueryParam(GET_RESOLVED) final String includeResolvedStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() {
				boolean includeResolved = parseBool(includeResolvedStr, false);
				Employee e = utils.getAuthenticatedUser(ajaxRequest);

				Preconditions.checkArgument(e != null, "Signed in user is not an employee");

				List<CorrectionRequest> requests;
				
				
				if(includeResolved){
					requests = signinDao.getAllCorrectionRequests();
				}
				else{
					requests = signinDao.getAllPendingCorrectionRequests();
				}
				
				List<CorrectionRequest> out = new ArrayList<>();
				for(CorrectionRequest cr : requests){
					if(cr.getSubmitter().getId() == e.getId()){
						out.add(cr);
					}
				}

				return Response.ok(out).build();
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
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee/signedin")
	@Description("Get all signed in employees")
	public Response getAllSignedInEmployees(){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				return Response.ok(signinDao.getAllSignedInEmployees()).build();
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
	public Response createEmployee(@Context final HttpServletRequest request, final Employee e){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Integer id = employeeDao.createEmployee(e);
				Employee out = employeeDao.getEmployee(id);
				
				logger.logEvent("A new employee named %s was created by %s", 
						buildFullName(out), 
						buildFullName(WebUtils.get().getAuthenticatedUser(request)));
				
				return Response.ok(out).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/employee")
	@Description("Update an existing employee using the given data")
	public Response updateEmployee(@Context final HttpServletRequest request, final Employee e){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() throws Exception {
				Employee signedIn = utils.getAuthenticatedUser(request);

				Preconditions.checkArgument(e != null, "Passed in employee is invalid");
				Preconditions.checkArgument(signedIn != null, "Signed in user is not an employee");

				if(e.getId() != signedIn.getId()){
					AuthenticationUtil.get().verifyMinimumPermissionLevel(EmployeeType.SCC_LEAD, signedIn);
				}

				employeeDao.updateEmployee(e);
				Employee out = employeeDao.getEmployee(e.getId());
				
				logger.logEvent("%s updated the information for %s", buildFullName(signedIn), buildFullName(out));
				
				return Response.ok(out).build();
			}
		});
	}

	////////////////////////////////////////////////////////////////////////////
	////////////////////    Timecard REST functions    /////////////////////////
	////////////////////////////////////////////////////////////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_LEAD_PATH + "/employee/{id}/timecard")
	@Description("Gets a timecard")
	public Response getTimecard(@Context final HttpServletRequest request, @PathParam("id") final Integer id, @QueryParam(START_DATE) final String startDate, @QueryParam(END_DATE) final String endDate){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() throws Exception{
				PayPeriod period;

				Employee e = employeeDao.getEmployee(id);
				Employee loggedIn = WebUtils.get().getAuthenticatedUser(request);

				Preconditions.checkArgument(e != null, "No employee with id '" + id + "' in database");
				Preconditions.checkArgument(loggedIn != null, "No employee currently signed in");

				// both null, use current period
				if(startDate == null && endDate == null){
					period = payPeriodDao.getCurrentPayPeriod();
				}
				else if(startDate == null || endDate == null){
					// one null, the other not. Not allowed
					throw new IllegalArgumentException(String.format("%s and %s must either both be set, or both be blank", START_DATE, END_DATE));
				}
				else{
					// both set, parse them
					long start = parseLong(startDate);
					long end = parseLong(endDate);
					period = new PayPeriod(new Date(start), new Date(end));
				}

				return Response.ok(TimecardFactory.get().getTimecard(period, e)).build();
			}
		});
	}

	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/employee/timecard/own")
	@Description("Gets a timecard for the currently signed in employee")
	public Response getOwnTimecard(@Context final HttpServletRequest request, @QueryParam(START_DATE) final String startDate, @QueryParam(END_DATE) final String endDate){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() throws Exception{
				PayPeriod period;

				Employee loggedIn = WebUtils.get().getAuthenticatedUser(request);
				Preconditions.checkArgument(loggedIn != null, "No employee currently signed in");

				// both null, use current period
				if(startDate == null && endDate == null){
					period = payPeriodDao.getCurrentPayPeriod();
				}
				else if(startDate == null || endDate == null){
					// one null, the other not. Not allowed
					throw new IllegalArgumentException(String.format("%s and %s must either both be set, or both be blank", START_DATE, END_DATE));
				}
				else{
					// both set, parse them
					long start = parseLong(startDate);
					long end = parseLong(endDate);
					period = new PayPeriod(new Date(start), new Date(end));
				}

				return Response.ok(TimecardFactory.get().getTimecard(period, loggedIn)).build();
			}
		});
	}

	////////////////////////////////////////////////////////////////////////////
	//////////////////    Pay period helper functions    ///////////////////////
	////////////////////////////////////////////////////////////////////////////


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SCC_PATH + "/periods")
	@Description("Get pay period end dates. if includeFuture is true, period ends in the future will be returned. If includeIds is true, the IDs for each period will be returned")
	public Response getPeriods(@QueryParam("includeFuture") final String includeFutureStr, @QueryParam("includeIds") final String includeIdsStr){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				Boolean includeFuture = parseBool(includeFutureStr, false);
				Boolean includeIds = parseBool(includeIdsStr, false);

				List<PeriodEnd> ends = payPeriodDao.getAllPayPeriodEnds();

				List<?> out;
				List<PeriodEnd> temp = new ArrayList<>();

				final Date maxEnd = new Date(payPeriodDao.getCurrentPayPeriod().getEndOfPeriod().getTime() + 1); 


				for(int i = 0; i < ends.size(); i++){
					if(includeFuture){
						temp.add(ends.get(i));
					}
					else if(ends.get(i).getEnd().before(maxEnd)){
						temp.add(ends.get(i));
					}
				}

				if(includeIds){
					out = temp;
				}
				else{
					List<Date> tmp = new ArrayList<Date>();
					for(int i = 0; i < temp.size(); i++){
						tmp.add(temp.get(i).getEnd());
					}
					out = tmp;
				}

				return Response.ok(out).build();
			}
		});
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/periods")
	@Description("Create pay periods ends for the given dates")
	public Response createPeriods(final List<Date> periodEnds){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				payPeriodDao.createPayPeriodEnds(periodEnds);
				return Response.ok().build();
			}
		});
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Path(PathConstants.ADMIN_SUPERVISOR_PATH + "/periods")
	@Description("Delete all pay periods with the given ids")
	public Response deletePeriods(final List<Integer> ids){
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {

			@Override
			public Response doTask() {
				payPeriodDao.deletePayPeriodEnds(ids);
				return Response.ok().build();
			}
		});
	}


	////////////////////////////////////////////////////////////////////////////
	//////////////////    Internal helper functions    /////////////////////////
	////////////////////////////////////////////////////////////////////////////

	private String buildFullName(Employee e){
		return e == null ? "" : String.format("%s %s (id %d)", e.getFirstName(), e.getLastName(), e.getId());
	}
	
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

	private Long parseLong(String val){
		try{
			return Long.parseLong(val);
		}catch(NumberFormatException e){
			String errMsg = "Cannot parse '" + val + "' into a long";
			throw new IllegalArgumentException(errMsg);
		}
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

	private Map<String, String> fingerInternal(String netid) throws Exception{
		return SigninUtil.get().finger(netid);
	}

}
