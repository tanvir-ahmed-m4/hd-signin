package edu.helpdesk.signin.model.nto;

import edu.helpdesk.signin.model.EmployeeType;

public class SigninUser {
	private String netId;
	private String firstName;
	private String lastName;
	private EmployeeType type;
	
	public SigninUser() {
		this(null, null, null, null);
	}
	
	public SigninUser(String netId, String firstName, String lastName, EmployeeType type) {
		this.setNetId(netId);
		this.setFirstName(firstName);
		this.setLastName(lastName);
		this.setType(getType());
	}
	
	public String getNetId() {
		return netId;
	}
	public void setNetId(String netId) {
		this.netId = netId;
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
	public EmployeeType getType() {
		return type;
	}
	public void setType(EmployeeType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SigninUser [netId=" + netId + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", type=" + type + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		SigninUser other = (SigninUser) obj;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
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
		if (type != other.type)
			return false;
		return true;
	}
}
