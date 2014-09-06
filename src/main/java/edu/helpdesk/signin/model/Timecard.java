package edu.helpdesk.signin.model;

import java.util.List;

import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;

public class Timecard {
	private List<WorkSession> workSessions;
	private Employee employee;
	private PayPeriod period;

	public List<WorkSession> getWorkSessions() {
		return workSessions;
	}

	public void setWorkSessions(List<WorkSession> workSessions) {
		this.workSessions = workSessions;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public PayPeriod getPeriod() {
		return period;
	}

	public void setPeriod(PayPeriod period) {
		this.period = period;
	}

	@Override
	public String toString() {
		return "Timecard [workSessions=" + workSessions + ", employee="
				+ employee + ", period=" + period + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employee == null) ? 0 : employee.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result
				+ ((workSessions == null) ? 0 : workSessions.hashCode());
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
		Timecard other = (Timecard) obj;
		if (employee == null) {
			if (other.employee != null)
				return false;
		} else if (!employee.equals(other.employee))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (workSessions == null) {
			if (other.workSessions != null)
				return false;
		} else if (!workSessions.equals(other.workSessions))
			return false;
		return true;
	}
}
