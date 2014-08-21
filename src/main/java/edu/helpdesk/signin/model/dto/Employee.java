package edu.helpdesk.signin.model.dto;

import edu.helpdesk.signin.model.EmployeeType;

public class Employee {
	private int id;
	private String firstName;
	private String lastName;
	private String riceId;
	private String netId;
	private EmployeeType employeeType;
	private boolean isEmployeeActive;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getRiceId() {
		return riceId;
	}
	public void setRiceId(String riceId) {
		this.riceId = riceId;
	}
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
	}
	public EmployeeType getEmployeeType() {
		return employeeType;
	}
	public void setEmployeeType(EmployeeType employeeType) {
		this.employeeType = employeeType;
	}
	public boolean getIsEmployeeActive() {
		return isEmployeeActive;
	}
	public void setIsEmployeeActive(boolean isEmployeeActive) {
		this.isEmployeeActive = isEmployeeActive;
	}
	
	@Override
	public String toString() {
		return "Employee [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", riceId=" + riceId + ", netId="
				+ netId + ", employeeType=" + employeeType
				+ ", isEmployeeActive=" + isEmployeeActive + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((employeeType == null) ? 0 : employeeType.hashCode());
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + id;
		result = prime * result + (isEmployeeActive ? 1231 : 1237);
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((riceId == null) ? 0 : riceId.hashCode());
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
		Employee other = (Employee) obj;
		if (employeeType != other.employeeType)
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (id != other.id)
			return false;
		if (isEmployeeActive != other.isEmployeeActive)
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		if (riceId == null) {
			if (other.riceId != null)
				return false;
		} else if (!riceId.equals(other.riceId))
			return false;
		return true;
	}
}
