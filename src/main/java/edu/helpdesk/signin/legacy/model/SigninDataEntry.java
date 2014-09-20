package edu.helpdesk.signin.legacy.model;

import java.util.Date;

import edu.helpdesk.signin.model.SigninType;

public class SigninDataEntry {
	private int employeeId;
	private SigninType type;
	private Date signinTime;
	private Date signoutTime;
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	public SigninType getType() {
		return type;
	}
	public void setType(SigninType type) {
		this.type = type;
	}
	public Date getSigninTime() {
		return signinTime;
	}
	public void setSigninTime(Date signinTime) {
		this.signinTime = signinTime;
	}
	public Date getSignoutTime() {
		return signoutTime;
	}
	public void setSignoutTime(Date signoutTime) {
		this.signoutTime = signoutTime;
	}
	@Override
	public String toString() {
		return "SigninDataEntry [employeeId=" + employeeId + ", type=" + type
				+ ", signinTime=" + signinTime + ", signoutTime=" + signoutTime
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + employeeId;
		result = prime * result
				+ ((signinTime == null) ? 0 : signinTime.hashCode());
		result = prime * result
				+ ((signoutTime == null) ? 0 : signoutTime.hashCode());
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
		SigninDataEntry other = (SigninDataEntry) obj;
		if (employeeId != other.employeeId)
			return false;
		if (signinTime == null) {
			if (other.signinTime != null)
				return false;
		} else if (!signinTime.equals(other.signinTime))
			return false;
		if (signoutTime == null) {
			if (other.signoutTime != null)
				return false;
		} else if (!signoutTime.equals(other.signoutTime))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
	
	

}
