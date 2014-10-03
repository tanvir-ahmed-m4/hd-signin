package edu.helpdesk.signin.dao.mybatis;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.PeriodEnd;

public interface PayPeriodMapper {

	public List<PeriodEnd> getAllPayPeriodEnds();
	public PeriodEnd getPayPeriodEnd(@Param("id") Integer id);
	public List<PeriodEnd> getEndsBetweenDates(@Param("before") Date before,
			@Param("after") Date after);
	public PayPeriod getPayPeriodForDate(@Param("d")Date d);
	
	
	public void createPayPeriodEnds(@Param("ends") List<Date> ends);
	
	public void deleteAllPayPeriodEnds();
	public void deletePayPeriodEnds(@Param("ids") List<Integer> ids);
	
	
}
