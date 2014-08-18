package edu.helpdesk.signin.web.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.helpdesk.signin.model.ModelObject;


@Path("/example")
public class RestApiExample {
	private static final Logger log = LoggerFactory.getLogger(RestApiExample.class);
	
	public RestApiExample() {
		System.out.println("Test");
	}
	
	@GET
	@Path("/test")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExampleJson(){
		// method accessible at http://localhost:8080/signin/rest/example/test
		// after running mvn jetty:run from the command line
		log.debug("getExampleJson called");
		return Response.ok(new ModelObject("testing")).build();
	}
}
