package edu.helpdesk.signin.model.dto;

public class SigninResultSwipedIn extends SigninResult{

	public SigninResultSwipedIn(String name,
			boolean hadError, String errorMsg) {
		super(true, name, hadError, errorMsg);
	}

}
