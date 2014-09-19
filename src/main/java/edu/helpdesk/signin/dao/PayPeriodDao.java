package edu.helpdesk.signin.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.PayPeriodMapper;
import edu.helpdesk.signin.model.PayPeriod;
import edu.helpdesk.signin.model.PeriodEnd;

public class PayPeriodDao {
	//private static final Logger log = LoggerFactory.getLogger(PayPeriodDao.class);

	@Autowired
	private PayPeriodMapper mapper;


	public Integer createPayPeriodEnd(Date end){
		return mapper.createPayPeriodEnd(end);
	}
	
	public List<PayPeriod> createPayPeriodEnds(List<Date> ends){
		return null;
	}

	public List<PeriodEnd> getAllPayPeriodEnds(){
		return mapper.getAllPayPeriodEnds();
	}

	public PeriodEnd getPayPeriodEnd(Integer id){
		return mapper.getPayPeriodEnd(id);
	}

	public void updatePayPeriodEnds(List<PeriodEnd> end){
		mapper.updatePayPeriodEnd(null);
	}

	public void deletePayPeriodEnds(List<Integer> id){
		mapper.deletePayPeriodEnd(null);
	}

	public PayPeriod getCurrentPayPeriod(){
		return getPayPeriodForDate(new Date());
	}

	public PayPeriod getPayPeriodForDate(Date d){
		return mapper.getPayPeriodForDate(d);
	}
}
