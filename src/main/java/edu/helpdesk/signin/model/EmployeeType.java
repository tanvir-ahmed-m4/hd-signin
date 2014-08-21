package edu.helpdesk.signin.model;

public enum EmployeeType {
	NONE,
	SCC,
	SCC_LEAD,
	SUPERVISOR,
	SYSADMIN;
	
	
	public int getId(){
		return this.ordinal();
	}
	
}
