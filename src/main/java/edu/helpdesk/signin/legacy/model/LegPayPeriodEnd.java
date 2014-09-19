package edu.helpdesk.signin.legacy.model;

import java.util.Date;

public class LegPayPeriodEnd {
	private int id;
	private Date periodEnd;
	
	public LegPayPeriodEnd() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getPeriodEnd() {
		return periodEnd;
	}

	public void setPeriodEnd(Date periodEnd) {
		this.periodEnd = periodEnd;
	}

	@Override
	public String toString() {
		return "LegPayPeriodEnd [id=" + id + ", periodEnd=" + periodEnd + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((periodEnd == null) ? 0 : periodEnd.hashCode());
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
		LegPayPeriodEnd other = (LegPayPeriodEnd) obj;
		if (id != other.id)
			return false;
		if (periodEnd == null) {
			if (other.periodEnd != null)
				return false;
		} else if (!periodEnd.equals(other.periodEnd))
			return false;
		return true;
	}
	
	

}
