package edu.helpdesk.signin.model.nto;

public class SigninResultSwipedIn extends SigninResult{

	public SigninResultSwipedIn(String name,
			boolean hadError, String errorMsg) {
		super(true, name, hadError, errorMsg);
	}

}
