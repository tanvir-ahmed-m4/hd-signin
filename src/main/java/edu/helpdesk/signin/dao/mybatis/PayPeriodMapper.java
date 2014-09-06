package edu.helpdesk.signin.dao.mybatis;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.PeriodEnd;

public interface PayPeriodMapper {

	public Integer createPayPeriodEnd(@Param("end") PeriodEnd end);
	public List<PeriodEnd> getAllPayPeriodEnds();
	public PeriodEnd getPayPeriodEnd(@Param("id") Integer id);
	public void updatePayPeriodEnd(@Param("end") PeriodEnd end);
	public void deletePayPeriodEnd(@Param("id")Integer id);
	public PayPeriod getPayPeriodForDate(@Param("d")Date d);
	
	
}
