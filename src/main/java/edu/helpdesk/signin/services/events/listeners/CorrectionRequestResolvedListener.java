package edu.helpdesk.signin.services.events.listeners;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.services.EmailService;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestResolvedEvent;

public class CorrectionRequestResolvedListener implements ApplicationEventListener<CorrectionRequestResolvedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestResolvedListener.class);
	
	@Autowired
	private EmailService emailService;
	
	public CorrectionRequestResolvedListener() {
		log.debug("{} created", this.getClass().getSimpleName());
	}
	
	@Override
	public void handleEvent(CorrectionRequestResolvedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getRequest();
		String to = event.getRequest().getSubmitter().getNetId();
		
		String newStatus = "resolved in an unknown way";
		if(CorrectionRequestStatus.APPROVED.equals(cr.getStatus())){
			newStatus = "approved";
		}
		else if(CorrectionRequestStatus.DENIED.equals(cr.getStatus())){
			newStatus = "denied";
		}
		
		String subject = String.format("%s, your correction request has been %s", cr.getSubmitter().getFirstName(), newStatus);
		
		String msg = String.format("%s, your correction request has been %s by %s, "
				+ "and your work session has changed from %s->%s to %s->%s ",
				cr.getSubmitter().getFirstName(),
				newStatus,
				cr.getCompleter().getFirstName(),
				cr.getOriginalSigninTime(),
				cr.getOriginalSignoutTime(),
				cr.getNewSigninTime(),
				cr.getNewSignoutTime());
		
		if(cr.getSubmitter().getId() != cr.getCompleter().getId())
			emailService.send(emailService.createEmail(Arrays.asList(to + "@rice.edu"), subject, msg));
	}

}
