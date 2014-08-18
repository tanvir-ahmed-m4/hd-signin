package edu.helpdesk.signin.model.dto;

public class SignedInEmployee {
	private String name;
	private long signInTime;
	
	public SignedInEmployee() {
		this(null, 0);
	}
	
	public SignedInEmployee(String name, long signInTime){
		this.setName(name);
		this.setSignInTime(signInTime);
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSignInTime() {
		return signInTime;
	}
	public void setSignInTime(long signInTime) {
		this.signInTime = signInTime;
	}
	
}
