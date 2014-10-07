package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.dto.Employee;

public class EmployeeCreatedEvent implements ApplicationEvent {
	private Employee employee;

	public EmployeeCreatedEvent(Employee e) {
		this.employee = e;
	}
	
	public EmployeeCreatedEvent() {
	}
	
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return "EmployeeCreatedEvent [employee=" + employee + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employee == null) ? 0 : employee.hashCode());
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
		EmployeeCreatedEvent other = (EmployeeCreatedEvent) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		return true;
	}
	
	
	
}
