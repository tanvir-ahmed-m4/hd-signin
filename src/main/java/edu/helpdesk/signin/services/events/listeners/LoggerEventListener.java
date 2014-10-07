package edu.helpdesk.signin.services.events.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.services.EventLogger;
import edu.helpdesk.signin.services.events.ApplicationEvent;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.EmployeeSwipeEvent;

public class LoggerEventListener implements ApplicationEventListener<ApplicationEvent>{
	private static final Logger log = LoggerFactory.getLogger(LoggerEventListener.class);
	
	@Autowired
	private EventLogger logger;
	
	public LoggerEventListener() {
		log.debug("{} created", LoggerEventListener.class.getCanonicalName());
	}
	
	@Override
	public void handleEvent(ApplicationEvent event) throws Exception {
		handleEventInternal(event);
	}
	
	
	private void handleEventInternal(ApplicationEvent e){
		if(e instanceof EmployeeSwipeEvent){
			handleEmployeeSwipeEvent((EmployeeSwipeEvent) e);
			return;
		}
		else{
			handleGenericEvent(e);
			return;
		}
	}
	
	private void handleEmployeeSwipeEvent(EmployeeSwipeEvent evt){
		Employee e = evt.getEmployee();
		log.debug("Employee {} swiped {}", evt.getEmployee().getFirstName(), evt.isNowSwipedIn() ? "in" : "out");
		logger.logEvent("%s %s swiped %s", e.getFirstName(), e.getLastName(), evt.isNowSwipedIn() ? "in" : "out");
	}
	
	private void handleGenericEvent(ApplicationEvent e){
		log.debug("Unclassified event of type {}: {}", e.getClass().getCanonicalName(), e.toString());
	}

}
