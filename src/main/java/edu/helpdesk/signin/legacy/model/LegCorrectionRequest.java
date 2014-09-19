package edu.helpdesk.signin.legacy.model;

import java.util.Date;

public class LegCorrectionRequest {
	private int id;
	private int swipeId;
	private Date timeIn;
	private Date timeOut;
	private int approved;
	private String approvedBy;
	private String password;
	
	public LegCorrectionRequest() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSwipeId() {
		return swipeId;
	}

	public void setSwipeId(int swipeId) {
		this.swipeId = swipeId;
	}

	public Date getTimeIn() {
		return timeIn;
	}

	public void setTimeIn(Date timeIn) {
		this.timeIn = timeIn;
	}

	public Date getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Date timeOut) {
		this.timeOut = timeOut;
	}

	public int getApproved() {
		return approved;
	}

	public void setApproved(int approved) {
		this.approved = approved;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "LegCorrectionRequest [id=" + id + ", swipeId=" + swipeId
				+ ", timeIn=" + timeIn + ", timeOut=" + timeOut + ", approved="
				+ approved + ", approvedBy=" + approvedBy + ", password="
				+ password + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + approved;
		result = prime * result
				+ ((approvedBy == null) ? 0 : approvedBy.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + swipeId;
		result = prime * result + ((timeIn == null) ? 0 : timeIn.hashCode());
		result = prime * result + ((timeOut == null) ? 0 : timeOut.hashCode());
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
		LegCorrectionRequest other = (LegCorrectionRequest) obj;
		if (approved != other.approved)
			return false;
		if (approvedBy == null) {
			if (other.approvedBy != null)
				return false;
		} else if (!approvedBy.equals(other.approvedBy))
			return false;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (swipeId != other.swipeId)
			return false;
		if (timeIn == null) {
			if (other.timeIn != null)
				return false;
		} else if (!timeIn.equals(other.timeIn))
			return false;
		if (timeOut == null) {
			if (other.timeOut != null)
				return false;
		} else if (!timeOut.equals(other.timeOut))
			return false;
		return true;
	}
	
	
	

}
