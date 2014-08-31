package edu.helpdesk.signin.model.dto;

import java.util.Date;

import com.google.common.base.Preconditions;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;

/**
 * <h1>DO NOT USE THIS CLASS UNLESS YOU ARE USING SQL MAPPERS</h1>
 * <br />
 * Use {@link CorrectionRequest} instead
 * @author galen
 *
 */
public class CorrectionRequestDto {
	private int id;
	private CorrectionRequestStatus status;
	private int signinId;
	private int submitter;
	private int completer;
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
	public int getSubmitter() {
		return submitter;
	}
	public void setSubmitter(int submitter) {
		this.submitter = submitter;
	}
	public int getCompleter() {
		return completer;
	}
	public void setCompleter(int completer) {
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
		result = prime * result + completer;
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
		result = prime * result + submitter;
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
		CorrectionRequestDto other = (CorrectionRequestDto) obj;
		if (completer != other.completer)
			return false;
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
		if (submitter != other.submitter)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "CorrectionRequestDto [id=" + id + ", status=" + status
				+ ", signinId=" + signinId + ", submitter=" + submitter
				+ ", completer=" + completer + ", newSigninTime="
				+ newSigninTime + ", newSignoutTime=" + newSignoutTime
				+ ", originalSigninTime=" + originalSigninTime
				+ ", originalSignoutTime=" + originalSignoutTime + "]";
	}
	
	public CorrectionRequest toCorrectionRequest(Employee submitter, Employee completer){
		CorrectionRequest out = new CorrectionRequest();
		
		if(this.getSubmitter() != 0){
			Preconditions.checkArgument(submitter != null, "Submitter null when it shouldn't be");
			Preconditions.checkArgument(submitter.getId() == this.getSubmitter(), "Given submitter employee has inorrect ID");
		}
		else{
			Preconditions.checkArgument(submitter == null, "Submitter not null when it should be");
		}
		
		if(this.getCompleter() != 0){
			Preconditions.checkArgument(completer != null, "Completer null when it shouldn't be");
			Preconditions.checkArgument(completer.getId() == this.getCompleter(), "Given completer employee has inorrect ID");
		}
		else{
			Preconditions.checkArgument(completer == null, "Completer not null when it should be");
		}
		
		out.setCompleter(completer);
		out.setId(this.getId());
		out.setNewSigninTime(this.getNewSigninTime());
		out.setNewSignoutTime(this.getNewSignoutTime());
		out.setOriginalSigninTime(this.getOriginalSigninTime());
		out.setOriginalSignoutTime(this.getOriginalSignoutTime());
		out.setSigninId(this.getSigninId());
		out.setStatus(this.getStatus());
		out.setSubmitter(submitter);
		
		return out;
	}
	
	
	
}
