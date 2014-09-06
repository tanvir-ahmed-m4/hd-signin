package edu.helpdesk.signin.services;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.PayPeriodDao;
import edu.helpdesk.signin.dao.SigninDao;
import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.Timecard;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.model.dto.WorkSession;

public class TimecardFactory {
	private static TimecardFactory INSTANCE = null;
	
	@Autowired
	private SigninDao dao;
	
	@Autowired
	private PayPeriodDao payPeriodDao;
	
	public static TimecardFactory get(){
		return INSTANCE;
	}

	public TimecardFactory() {
		if(INSTANCE == null)
			INSTANCE = this;
	}
	
	
	public Timecard getCurrentTimecard(Employee e){
		return this.getTimecard(payPeriodDao.getCurrentPayPeriod(), e);
	}
	
	public Timecard getTimecard(PayPeriod period, Employee e){
		List<WorkSession> sessions = dao.getAllWorkSessionsForEmployee(e.getId(), getMidnightNextDay(period.getStartOfPeriod()), period.getEndOfPeriod());
		Timecard out = new Timecard();
		out.setEmployee(e);
		out.setPeriod(period);
		out.setWorkSessions(sessions);
		return out;
	}
	
	private Date getMidnightNextDay(Date d){
		Calendar date = new GregorianCalendar();
		date.setTime(d);
		
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		// next day
		date.add(Calendar.DAY_OF_MONTH, 1);
		
		return date.getTime();
	}
	


}
