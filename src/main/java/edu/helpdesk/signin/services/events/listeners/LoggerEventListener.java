package edu.helpdesk.signin.services.events.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.services.EventLogger;
import edu.helpdesk.signin.services.events.ApplicationEvent;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestCreatedEvent;
import edu.helpdesk.signin.services.events.CorrectionRequestResolvedEvent;
import edu.helpdesk.signin.services.events.EmployeeCreatedEvent;
import edu.helpdesk.signin.services.events.EmployeeSwipeEvent;
import edu.helpdesk.signin.services.events.EmployeeUpdatedEvent;

public class LoggerEventListener implements ApplicationEventListener<ApplicationEvent>{
	private static final Logger log = LoggerFactory.getLogger(LoggerEventListener.class);

	@Autowired
	private EventLogger logger;

	public LoggerEventListener() {
		log.debug("{} created", LoggerEventListener.class.getCanonicalName());
	}

	@Override
	public void handleEvent(ApplicationEvent event) throws Exception {
		log.debug("Event logged");
		handleEventInternal(event);
	}


	private void handleEventInternal(ApplicationEvent e){
		if(e instanceof EmployeeSwipeEvent){
			handleEmployeeSwipeEvent((EmployeeSwipeEvent) e);
		}
		else if(e instanceof EmployeeCreatedEvent){
			handleEmployeeCreatedEvent((EmployeeCreatedEvent) e);
		}
		else if(e instanceof EmployeeUpdatedEvent){
			handleEmployeeUpdatedEvent((EmployeeUpdatedEvent) e);
		}
		else if(e instanceof CorrectionRequestCreatedEvent){
			handleCorrectionRequestCreatedEvent((CorrectionRequestCreatedEvent) e);
		}
		else if(e instanceof CorrectionRequestResolvedEvent){
			handleCorrectionRequestResolvedEvent((CorrectionRequestResolvedEvent) e);
		}
		else{
			handleGenericEvent(e);
		}
	}

	private void handleCorrectionRequestResolvedEvent(CorrectionRequestResolvedEvent evt){
		Integer id = evt.getRequest().getId();
		Employee resolver = evt.getRequest().getCompleter();
		Employee submitter = evt.getRequest().getSubmitter();

		switch(evt.getRequest().getStatus()){
		case APPROVED:
			logger.logEvent("%s approved correction request %d for %s",
					buildFullName(resolver), id, buildFullName(submitter));
		case DENIED:
			if(resolver.getId() == submitter.getId()){
				logger.logEvent("%s cancelled correction request %d", buildFullName(submitter), id);
			}
			else{
				logger.logEvent("%s denied correction request %d for %s",
						buildFullName(resolver), id, buildFullName(submitter));
			}

		case PENDING:
		default:
		}
	}

	private void handleCorrectionRequestCreatedEvent(CorrectionRequestCreatedEvent evt){
		logger.logEvent("%s submitted a correction request with id %d",
				buildFullName(evt.getCreatedRequest().getSubmitter()), evt.getCreatedRequest().getId());
	}
	private void handleEmployeeUpdatedEvent(EmployeeUpdatedEvent evt){
		logger.logEvent("%s updated the information for %s", buildFullName(evt.getEditor()), buildFullName(evt.getUpdateEmployee()));
	}
	private void handleEmployeeCreatedEvent(EmployeeCreatedEvent evt){
		logger.logEvent("A new employee named %s was created by %s", 
				buildFullName(evt.getEmployee()),
				buildFullName(evt.getCreator()));
	}

	private void handleEmployeeSwipeEvent(EmployeeSwipeEvent evt){
		Employee e = evt.getEmployee();
		log.debug("Employee {} swiped {}", evt.getEmployee().getFirstName(), evt.isNowSwipedIn() ? "in" : "out");
		logger.logEvent("%s %s swiped %s", e.getFirstName(), e.getLastName(), evt.isNowSwipedIn() ? "in" : "out");
	}

	private void handleGenericEvent(ApplicationEvent e){
		log.debug("Unclassified event of type {}: {}", e.getClass().getCanonicalName(), e.toString());
	}


	private String buildFullName(Employee e){
		return e == null ? "null" : String.format("%s %s (id %d)", e.getFirstName(), e.getLastName(), e.getId());
	}
}
