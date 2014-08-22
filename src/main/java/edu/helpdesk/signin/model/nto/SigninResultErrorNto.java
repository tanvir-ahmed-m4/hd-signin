package edu.helpdesk.signin.model.nto;

public class SigninResultErrorNto extends SigninResultNto {

	public SigninResultErrorNto(boolean isNowSignedIn, String name, String errorMsg) {
		super(isNowSignedIn, name, true, errorMsg);
	}

}
