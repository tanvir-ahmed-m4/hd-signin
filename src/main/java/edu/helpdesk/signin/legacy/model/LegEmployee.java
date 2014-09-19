package edu.helpdesk.signin.legacy.model;

public class LegEmployee {
	private String riceId;
	private String netId;
	private String name;
	private int isWorkStudy;
	private double hourlyWage;
	
	public LegEmployee() {
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsWorkStudy() {
		return isWorkStudy;
	}

	public void setIsWorkStudy(int isWorkStudy) {
		this.isWorkStudy = isWorkStudy;
	}

	public double getHourlyWage() {
		return hourlyWage;
	}

	public void setHourlyWage(double hourlyWage) {
		this.hourlyWage = hourlyWage;
	}

	@Override
	public String toString() {
		return "LegEmployee [riceId=" + riceId + ", netId=" + netId + ", name="
				+ name + ", isWorkStudy=" + isWorkStudy + ", hourlyWage="
				+ hourlyWage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(hourlyWage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + isWorkStudy;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		LegEmployee other = (LegEmployee) obj;
		if (Double.doubleToLongBits(hourlyWage) != Double
				.doubleToLongBits(other.hourlyWage))
			return false;
		if (isWorkStudy != other.isWorkStudy)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
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
