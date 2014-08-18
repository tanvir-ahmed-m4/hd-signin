package edu.helpdesk.signin.model;

public class ModelObject {
	private String message;

	public ModelObject() {
	}
	
	public ModelObject(String msg){
		this.setMessage(msg);
	}
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
