package edu.helpdesk.signin.model;

import java.util.Date;

/**
 * Class to represent a pay period.
 * The start of the period is inclusive, while the end is exclusive.
 * That is to say, the period spans for {@code (startOfPeriod, endOfPeriod]}
 * time
 * @author galen
 *
 */
public class PayPeriod {
	private Date startOfPeriod;
	private Date endOfPeriod;
	
	public PayPeriod() {
	}
	
	public PayPeriod(Date start, Date end) {
		this.setStartOfPeriod(start);
		this.setEndOfPeriod(end);
	}
	
	public Date getStartOfPeriod() {
		return startOfPeriod;
	}
	
	public void setStartOfPeriod(Date startOfPeriod) {
		this.startOfPeriod = startOfPeriod;
	}
	
	public Date getEndOfPeriod() {
		return endOfPeriod;
	}
	
	public void setEndOfPeriod(Date endOfPeriod) {
		this.endOfPeriod = endOfPeriod;
	}
	
	@Override
	public String toString() {
		return "PayPeriod [startOfPeriod=" + startOfPeriod + ", endOfPeriod="
				+ endOfPeriod + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((endOfPeriod == null) ? 0 : endOfPeriod.hashCode());
		result = prime * result
				+ ((startOfPeriod == null) ? 0 : startOfPeriod.hashCode());
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
		PayPeriod other = (PayPeriod) obj;
		if (endOfPeriod == null) {
			if (other.endOfPeriod != null)
				return false;
		} else if (!endOfPeriod.equals(other.endOfPeriod))
			return false;
		if (startOfPeriod == null) {
			if (other.startOfPeriod != null)
				return false;
		} else if (!startOfPeriod.equals(other.startOfPeriod))
			return false;
		return true;
	}
}
