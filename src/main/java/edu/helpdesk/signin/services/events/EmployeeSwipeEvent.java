package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.dto.Employee;

public class EmployeeSwipeEvent implements ApplicationEvent {
	private Employee employee;
	private boolean isNowSwipedIn;
	
	public EmployeeSwipeEvent() {
	}
	
	public EmployeeSwipeEvent(Employee e, boolean isNowSignedIn){
		this.employee = e;
		this.isNowSwipedIn = isNowSignedIn;
		
	}
	
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public boolean isNowSwipedIn() {
		return isNowSwipedIn;
	}
	public void setNowSwipedIn(boolean isNowSwipedIn) {
		this.isNowSwipedIn = isNowSwipedIn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employee == null) ? 0 : employee.hashCode());
		result = prime * result + (isNowSwipedIn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeeSwipeEvent other = (EmployeeSwipeEvent) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (isNowSwipedIn != other.isNowSwipedIn)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EmployeeSwipeEvent [employee=" + employee + ", isNowSwipedIn="
				+ isNowSwipedIn + "]";
	}
	
	
	
	
}
