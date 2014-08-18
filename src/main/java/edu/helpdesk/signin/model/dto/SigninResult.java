package edu.helpdesk.signin.model.dto;

public abstract class SigninResult {
	private boolean isNowSignedIn;
	private String name;
	
	private boolean hasError;
	private String errorMessage;
	
	
	public SigninResult(boolean isNowSignedIn, String name, boolean hadError, String errorMsg) {
		this.setErrorMessage(errorMsg);
		this.setHasError(hadError);
		this.setisNowSignedIn(isNowSignedIn);
		this.setName(name);
	}
	
	public boolean getIsNowSignedIn() {
		return isNowSignedIn;
	}
	public void setisNowSignedIn(boolean isNowSignedIn) {
		this.isNowSignedIn = isNowSignedIn;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isHasError() {
		return hasError;
	}
	public void setHasError(boolean hasError) {
		this.hasError = hasError;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	
	
}
