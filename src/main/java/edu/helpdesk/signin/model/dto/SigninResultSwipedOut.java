package edu.helpdesk.signin.model.dto;

public class SigninResultSwipedOut extends SigninResult{
	
	private String snark;
	private int timeWorkedShift;
	private int timeWorkedDay;
	
	public SigninResultSwipedOut(int timeWorkedShift, int timeWorkedDay, String snark, String name,
			boolean hadError, String errorMsg) {
		super(false, name, hadError, errorMsg);
		this.setSnark(snark);
		this.setTimeWorkedShift(timeWorkedShift);
		this.setTimeWorkedDay(timeWorkedDay);
	}

	public String getSnark() {
		return snark;
	}

	public void setSnark(String snark) {
		this.snark = snark;
	}

	public int getTimeWorkedShift() {
		return timeWorkedShift;
	}

	public void setTimeWorkedShift(int timeWorkedShift) {
		this.timeWorkedShift = timeWorkedShift;
	}

	public int getTimeWorkedDay() {
		return timeWorkedDay;
	}

	public void setTimeWorkedDay(int timeWorkedDay) {
		this.timeWorkedDay = timeWorkedDay;
	}

}
