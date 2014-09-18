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
	
	public boolean isAboveOrEqualTo(EmployeeType other){
		if(other == null){
			return false;
		}
		
		if(other.ordinal() <= this.ordinal()){
			return true;
		}
	
		return false;
	}
	
	public boolean isAbove(EmployeeType other){
		if(other == null){
			return false;
		}
		
		if(other.ordinal() < this.ordinal()){
			return true;
		}
	
		return false;
	}
	
}
