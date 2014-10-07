package edu.helpdesk.signin.services.events;

import edu.helpdesk.signin.model.CorrectionRequest;

public class CorrectionRequestResolvedEvent implements ApplicationEvent {
	private CorrectionRequest request;

	public CorrectionRequestResolvedEvent(CorrectionRequest r) {
		this.request = r;
	}
	
	public CorrectionRequestResolvedEvent() {
	}
	
	public CorrectionRequest getRequest() {
		return request;
	}

	public void setRequest(CorrectionRequest request) {
		this.request = request;
	}

	@Override
	public String toString() {
		return "CorrectionRequestResolvedEvent [request=" + request + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((request == null) ? 0 : request.hashCode());
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
		CorrectionRequestResolvedEvent other = (CorrectionRequestResolvedEvent) obj;
		if (request == null) {
			if (other.request != null)
				return false;
		} else if (!request.equals(other.request))
			return false;
		return true;
	}
	
}
