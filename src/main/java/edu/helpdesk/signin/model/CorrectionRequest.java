package edu.helpdesk.signin.model;

import java.util.Date;

import edu.helpdesk.signin.model.dto.Employee;

/**
 * 
 * @author galen
 *
 */
public class CorrectionRequest {
	private int id;
	private CorrectionRequestStatus status;
	private int signinId;
	private Employee submitter;
	private Employee completer;
	private Date newSigninTime;
	private Date newSignoutTime;
	private Date originalSigninTime;
	private Date originalSignoutTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CorrectionRequestStatus getStatus() {
		return status;
	}
	public void setStatus(CorrectionRequestStatus status) {
		this.status = status;
	}
	public int getSigninId() {
		return signinId;
	}
	public void setSigninId(int signinId) {
		this.signinId = signinId;
	}
	public Employee getSubmitter() {
		return submitter;
	}
	public void setSubmitter(Employee submitter) {
		this.submitter = submitter;
	}
	public Employee getCompleter() {
		return completer;
	}
	public void setCompleter(Employee completer) {
		this.completer = completer;
	}
	public Date getNewSigninTime() {
		return newSigninTime;
	}
	public void setNewSigninTime(Date newSigninTime) {
		this.newSigninTime = newSigninTime;
	}
	public Date getNewSignoutTime() {
		return newSignoutTime;
	}
	public void setNewSignoutTime(Date newSignoutTime) {
		this.newSignoutTime = newSignoutTime;
	}
	public Date getOriginalSigninTime() {
		return originalSigninTime;
	}
	public void setOriginalSigninTime(Date originalSigninTime) {
		this.originalSigninTime = originalSigninTime;
	}
	public Date getOriginalSignoutTime() {
		return originalSignoutTime;
	}
	public void setOriginalSignoutTime(Date originalSignoutTime) {
		this.originalSignoutTime = originalSignoutTime;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((newSigninTime == null) ? 0 : newSigninTime.hashCode());
		result = prime * result
				+ ((newSignoutTime == null) ? 0 : newSignoutTime.hashCode());
		result = prime
				* result
				+ ((originalSigninTime == null) ? 0 : originalSigninTime
						.hashCode());
		result = prime
				* result
				+ ((originalSignoutTime == null) ? 0 : originalSignoutTime
						.hashCode());
		result = prime * result + signinId;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result
				+ ((submitter == null) ? 0 : submitter.hashCode());
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
		CorrectionRequest other = (CorrectionRequest) obj;
		if (id != other.id)
			return false;
		if (newSigninTime == null) {
			if (other.newSigninTime != null)
				return false;
		} else if (!newSigninTime.equals(other.newSigninTime))
			return false;
		if (newSignoutTime == null) {
			if (other.newSignoutTime != null)
				return false;
		} else if (!newSignoutTime.equals(other.newSignoutTime))
			return false;
		if (originalSigninTime == null) {
			if (other.originalSigninTime != null)
				return false;
		} else if (!originalSigninTime.equals(other.originalSigninTime))
			return false;
		if (originalSignoutTime == null) {
			if (other.originalSignoutTime != null)
				return false;
		} else if (!originalSignoutTime.equals(other.originalSignoutTime))
			return false;
		if (signinId != other.signinId)
			return false;
		if (status != other.status)
			return false;
		if (submitter == null) {
			if (other.submitter != null)
				return false;
		} else if (!submitter.equals(other.submitter))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CorrectionRequest [id=" + id + ", status=" + status
				+ ", signinId=" + signinId + ", submitter=" + submitter
				+ ", newSigninTime=" + newSigninTime + ", newSignoutTime="
				+ newSignoutTime + ", originalSigninTime=" + originalSigninTime
				+ ", originalSignoutTime=" + originalSignoutTime + "]";
	}
	
	
}
