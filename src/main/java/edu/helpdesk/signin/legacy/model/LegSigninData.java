package edu.helpdesk.signin.legacy.model;

import java.util.Date;

public class LegSigninData {
	private int id;
	private String riceId;
	private Date timeIn;
	private Date timeOut;
	private long elapsed;
	private String type;
	private String ipIn;
	private String ipOut;
	
	public LegSigninData() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRiceId() {
		return riceId;
	}

	public void setRiceId(String riceId) {
		this.riceId = riceId;
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

	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIpIn() {
		return ipIn;
	}

	public void setIpIn(String ipIn) {
		this.ipIn = ipIn;
	}

	public String getIpOut() {
		return ipOut;
	}

	public void setIpOut(String ipOut) {
		this.ipOut = ipOut;
	}

	
	@Override
	public String toString() {
		return "LegSigninData [id=" + id + ", riceId=" + riceId + ", timeIn="
				+ timeIn + ", timeOut=" + timeOut + ", elapsed=" + elapsed
				+ ", type=" + type + ", ipIn=" + ipIn + ", ipOut=" + ipOut
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (elapsed ^ (elapsed >>> 32));
		result = prime * result + id;
		result = prime * result + ((ipIn == null) ? 0 : ipIn.hashCode());
		result = prime * result + ((ipOut == null) ? 0 : ipOut.hashCode());
		result = prime * result + ((riceId == null) ? 0 : riceId.hashCode());
		result = prime * result + ((timeIn == null) ? 0 : timeIn.hashCode());
		result = prime * result + ((timeOut == null) ? 0 : timeOut.hashCode());
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
		LegSigninData other = (LegSigninData) obj;
		if (elapsed != other.elapsed)
			return false;
		if (id != other.id)
			return false;
		if (ipIn == null) {
			if (other.ipIn != null)
				return false;
		} else if (!ipIn.equals(other.ipIn))
			return false;
		if (ipOut == null) {
			if (other.ipOut != null)
				return false;
		} else if (!ipOut.equals(other.ipOut))
			return false;
		if (riceId == null) {
			if (other.riceId != null)
				return false;
		} else if (!riceId.equals(other.riceId))
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
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	
	

}
