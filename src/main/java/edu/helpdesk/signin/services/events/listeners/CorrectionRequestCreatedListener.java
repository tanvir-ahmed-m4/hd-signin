package edu.helpdesk.signin.services.events.listeners;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.services.EmailService;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestCreatedEvent;

public class CorrectionRequestCreatedListener implements ApplicationEventListener<CorrectionRequestCreatedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestCreatedListener.class);
	
	@Autowired
	private EmailService emailService;
	
	public CorrectionRequestCreatedListener() {
		log.debug("{} created", this.getClass().getSimpleName());
	}
	
	@Override
	public void handleEvent(CorrectionRequestCreatedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getCreatedRequest();
		String to = event.getCreatedRequest().getSubmitter().getNetId();
		String subject = String.format("%s, your correction request has been received", cr.getSubmitter().getFirstName());
		String msg = String.format("%s, your correction request has been received, "
				+ "and will change your work session from %s->%s to %s->%s if approved",
				cr.getSubmitter().getFirstName(), 
				cr.getOriginalSigninTime(),
				cr.getOriginalSignoutTime(),
				cr.getNewSigninTime(),
				cr.getNewSignoutTime());
		emailService.send(emailService.createEmail(Arrays.asList(to + "@rice.edu"), subject, msg));
	}

}
