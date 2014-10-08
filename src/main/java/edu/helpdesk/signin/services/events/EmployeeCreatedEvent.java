package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.dto.Employee;

public class EmployeeCreatedEvent implements ApplicationEvent {
	private Employee creator;
	private Employee employee;

	public EmployeeCreatedEvent(Employee created, Employee creator) {
		this.employee = created;
		this.creator = creator;
	}
	public EmployeeCreatedEvent() {
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Employee getCreator() {
		return creator;
	}
	public void setCreator(Employee creator) {
		this.creator = creator;
	}
	@Override
	public String toString() {
		return "EmployeeCreatedEvent [creator=" + creator + ", employee="
				+ employee + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
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
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		return true;
	}
}
