package edu.helpdesk.signin.model;

public enum CorrectionRequestStatus {
	PENDING,
	APPROVED,
	DENIED;
	
	
	int getId(){
		return this.ordinal();
	}
	
}
