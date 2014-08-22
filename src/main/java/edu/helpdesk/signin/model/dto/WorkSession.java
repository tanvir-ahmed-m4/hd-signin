package edu.helpdesk.signin.model.dto;

import java.util.Date;

public class WorkSession {
	private int id;
	private int employeeId;
	private Date signinTime;
	private Date signoutTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
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
		return "WorkSession [id=" + id + ", employeeId=" + employeeId
				+ ", signinTime=" + signinTime + ", signoutTime=" + signoutTime
				+ "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + employeeId;
		result = prime * result + id;
		result = prime * result
				+ ((signinTime == null) ? 0 : signinTime.hashCode());
		result = prime * result
				+ ((signoutTime == null) ? 0 : signoutTime.hashCode());
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
		WorkSession other = (WorkSession) obj;
		if (employeeId != other.employeeId)
			return false;
		if (id != other.id)
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
		return true;
	}
	
	
}
