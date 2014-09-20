package edu.helpdesk.signin.model;

public enum SigninType {
	UNKNOWN,
	SWIPE,
	COMPUTER,
	EDIT,
	CORRECTION,
	ADMIN
	;
	
	public Integer getId(){
		return this.ordinal();
	}
	
}
