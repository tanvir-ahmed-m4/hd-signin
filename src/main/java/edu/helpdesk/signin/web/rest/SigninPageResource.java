package edu.helpdesk.signin.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.dao.SigninDao;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;
import edu.helpdesk.signin.model.nto.SignedInEmployee;
import edu.helpdesk.signin.model.nto.SigninResultErrorNto;
import edu.helpdesk.signin.model.nto.SigninResultNto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedINto;
import edu.helpdesk.signin.model.nto.SigninResultSwipedOutNto;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebTask;
import static edu.helpdesk.signin.web.util.WebTaskExecutor.doWebTaskSafe;

@Component
@Path(PathConstants.SIGNIN_PATH)
public class SigninPageResource {
	private static final Logger log = LoggerFactory.getLogger(SigninPageResource.class);

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private SigninDao signinDao;

	//////////////////////////////////////////////////////////////////////////
	/////////////////////////    Constructor    //////////////////////////////
	//////////////////////////////////////////////////////////////////////////

	public SigninPageResource() {}

	//////////////////////////////////////////////////////////////////////////
	/////////////////////////    Public API methods    ///////////////////////
	//////////////////////////////////////////////////////////////////////////

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/currentemployees")
	public Response getSignedInEmployees(){
		log.trace("getSignedInEmployees called");
		return process(new WebTask() {
			@Override
			public Response doTask() {
				return Response.ok(getSignedInEmployeesInternal()).build();
			}
		});
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/swipe")
	public Response doSwipe(@Context HttpServletRequest context, final String data){
		return process(new WebTask() {
			@Override
			public Response doTask() {
				return Response.ok(doSwipeInternal(data)).build();
			}
		});
	}

	//////////////////////////////////////////////////////////////////////////
	/////////////////////    Internal helper methods    //////////////////////
	//////////////////////////////////////////////////////////////////////////


	private SigninResultNto doSwipeInternal(String sid){

		Employee e = employeeDao.getEmployeeByRiceId(sid);

		if(e != null){
			WorkSession result = this.signinDao.swipe(e);
			if(result.getSignoutTime() == null){
				return new SigninResultSwipedINto(e.getFirstName() + " " + e.getLastName());
			}
			else{
				int time = (int) (result.getSignoutTime().getTime() - result.getSigninTime().getTime());
				
				// TODO get time signed in for the day, and snark
				return new SigninResultSwipedOutNto(time, 0, "Butts", getName(e));
			}
		}
		else{
			return new SigninResultErrorNto(false, null, "No employee with Rice ID '" + sid + "' found in database");
		}
	}

	private List<SignedInEmployee> getSignedInEmployeesInternal(){
		List<Employee> employees = signinDao.getAllSignedInEmployees();
		List<SignedInEmployee> out = new ArrayList<>(employees.size());

		for(int i = 0; i < employees.size(); i++){
			Employee e = employees.get(i);
			// TODO get signed in times
			out.add(new SignedInEmployee(getName(e), -1));
		}

		return out;
	}

	private String getName(Employee e){
		if(e == null)
			return null;
		return String.format("%s %s", e.getFirstName(), e.getLastName());
	}


	private Response process(WebTask task){
		return doWebTaskSafe(task);
	}
}
