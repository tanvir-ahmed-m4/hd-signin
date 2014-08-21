package edu.helpdesk.signin.web.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import edu.helpdesk.signin.model.nto.SignedInEmployee;
import edu.helpdesk.signin.model.nto.SigninResult;
import edu.helpdesk.signin.model.nto.SigninResultSwipedIn;
import edu.helpdesk.signin.model.nto.SigninResultSwipedOut;
import edu.helpdesk.signin.web.util.PathConstants;
import edu.helpdesk.signin.web.util.WebTask;
import static edu.helpdesk.signin.web.util.WebTaskExecutor.doWebTaskSafe;

@Component
@Path(PathConstants.SIGNIN_PATH)
public class SigninPageResource {
	private static final Logger log = LoggerFactory.getLogger(SigninPageResource.class);
	private Random r = new Random();
	
	public SigninPageResource() {}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/currentemployees")
	public Response getSignedInEmployees(){
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
	public Response doSwipe(final String data){
		log.trace("swipe called with {}", data);
		
		return process(new WebTask() {
			
			@Override
			public Response doTask() {
				
				return Response.ok(doSwipeInternal(data)).build();
			}
		});
	}
	
	
	private Map<String, Boolean> signedIn = new HashMap<>(); /* TODO remove and replace with DAO */
	
	private SigninResult doSwipeInternal(String sid){
		Boolean isIn = false;
		
		if(signedIn.get(sid) != null){
			isIn = signedIn.get(sid);
		}
		
		isIn = !isIn;
		
		signedIn.put(sid, isIn);
		
		if(isIn){
			return new SigninResultSwipedIn(sid, false, null);
		}
		int shift = r.nextInt(1000 * 60 * 60 * 5);
		return new SigninResultSwipedOut(shift, shift + r.nextInt(1000 * 60 * 60 * 4), "Butts", sid, false, null);
		
	}
	
	private List<SignedInEmployee> getSignedInEmployeesInternal(){
		List<SignedInEmployee> out = new ArrayList<>();
		for(int i = 0, max = r.nextInt(2) + r.nextInt(5); i < max; i++){
			// return an employee that signed in sometime in the last 3 hours
			out.add(new SignedInEmployee("test employee " + i, System.currentTimeMillis() - r.nextInt(1000 * 60 * 60 * 3)));
		}
		
		
		return out;
	}
	
	
	private Response process(WebTask task){
		return doWebTaskSafe(task);
	}
}
