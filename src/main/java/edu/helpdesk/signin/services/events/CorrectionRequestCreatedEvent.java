package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.CorrectionRequest;


public class CorrectionRequestCreatedEvent implements ApplicationEvent {
	private CorrectionRequest createdRequest;

	public CorrectionRequestCreatedEvent() {
	}
	
	public CorrectionRequestCreatedEvent(CorrectionRequest request) {
		this.createdRequest = request;
	}
	
	public CorrectionRequest getCreatedRequest() {
		return createdRequest;
	}

	public void setCreatedRequest(CorrectionRequest createdRequest) {
		this.createdRequest = createdRequest;
	}

	@Override
	public String toString() {
		return "CorrectionRequestCreatedEvent [createdRequest="
				+ createdRequest + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdRequest == null) ? 0 : createdRequest.hashCode());
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
		CorrectionRequestCreatedEvent other = (CorrectionRequestCreatedEvent) obj;
		if (createdRequest == null) {
			if (other.createdRequest != null)
				return false;
		} else if (!createdRequest.equals(other.createdRequest))
			return false;
		return true;
	}
	
	
	
}
