package edu.helpdesk.signin.dao;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.dao.mybatis.EmployeeMapper;


public class EmployeeDao {
	private static final Logger log = LoggerFactory.getLogger(EmployeeDao.class);
	
	@Autowired
	private EmployeeMapper mapper;
	
	public EmployeeDao() {
		log.debug("Emplyee DAO created");
	}
	
	@PostConstruct
	public void done(){
		log.debug("Mapper: {}", mapper.getAllEmployees());
	}
	
}
