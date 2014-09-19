package edu.helpdesk.signin.legacy.model;

public class LegPrivilegeLevel {
	private String netId;
	private int level;
	
	public LegPrivilegeLevel() {
	}

	public String getNetId() {
		return netId;
	}

	public void setNetId(String netId) {
		this.netId = netId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	

	@Override
	public String toString() {
		return "LegPrivilegeLevel [netId=" + netId + ", level=" + level + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + level;
		result = prime * result + ((netId == null) ? 0 : netId.hashCode());
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
		LegPrivilegeLevel other = (LegPrivilegeLevel) obj;
		if (level != other.level)
			return false;
		if (netId == null) {
			if (other.netId != null)
				return false;
		} else if (!netId.equals(other.netId))
			return false;
		return true;
	}
	
	

}
