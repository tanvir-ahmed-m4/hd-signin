package edu.helpdesk.signin.util;

import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;

public class AuthenticationUtil {
	private static AuthenticationUtil INSTANCE = null;
	
	public static AuthenticationUtil get(){
		if(INSTANCE == null){
			INSTANCE = new AuthenticationUtil();
		}
		return INSTANCE;
	}
	
	public AuthenticationUtil() {
	}
	
	
	public void verifyMinimumPermissionLevel(EmployeeType required, EmployeeType actual) throws AuthenticationFailedException{
		if(actual.isAboveOrEqual(required)){
			return;
		}
		else{
			throw new AuthenticationFailedException(required, actual);
		}
	}
	
	public void verifyMinimumPermissionLevel(EmployeeType required, Employee actual) throws AuthenticationFailedException{
		if(actual == null){
			throw new AuthenticationFailedException("Required authentication level of " + required + ", but got null");
		}
		
		verifyMinimumPermissionLevel(required, actual.getEmployeeType());
	}
	
	
	
	
	
	
	
}
