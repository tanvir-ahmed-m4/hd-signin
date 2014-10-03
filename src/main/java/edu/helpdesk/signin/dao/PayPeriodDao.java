package edu.helpdesk.signin.dao;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.PayPeriodMapper;
import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.PeriodEnd;

public class PayPeriodDao {
	private static final Logger log = LoggerFactory.getLogger(PayPeriodDao.class);

	@Autowired
	private PayPeriodMapper mapper;

	
	public void createPayPeriodEnd(Date end){
		createPayPeriodEnds(Arrays.asList(end));
	}
	
	public void createPayPeriodEnds(List<Date> ends){
		mapper.createPayPeriodEnds(ends);
	}

	
	public List<PeriodEnd> getAllPayPeriodEnds(){
		return mapper.getAllPayPeriodEnds();
	}

	public PeriodEnd getPayPeriodEnd(Integer id){
		return mapper.getPayPeriodEnd(id);
	}
	
	/**
	 * Get all pay periods that exist in the range {@code [before, after)}
	 * @param before The date all periods must fall on or after
	 * @param after The date that all periods must be before
	 * @return
	 */
	public List<PeriodEnd> getEndsBetweenDates(Date before, Date after){
		return mapper.getEndsBetweenDates(before, after);
	}
	
	public PayPeriod getCurrentPayPeriod(){
		return getPayPeriodForDate(new Date());
	}
	
	public PayPeriod getPayPeriodForDate(Date d){
		return mapper.getPayPeriodForDate(d);
	}
	
	

	public void deletePayPeriodEnds(List<Integer> id){
		mapper.deletePayPeriodEnds(id);
	}
	
	public void deleteAllPayPeriodEnds(){
		log.info("Deleting all pay period ends");
		mapper.deleteAllPayPeriodEnds();
	}

	
}
