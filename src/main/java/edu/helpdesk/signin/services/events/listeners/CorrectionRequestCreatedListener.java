package edu.helpdesk.signin.services.events.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestCreatedEvent;

public class CorrectionRequestCreatedListener implements ApplicationEventListener<CorrectionRequestCreatedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestCreatedListener.class);
	
	public CorrectionRequestCreatedListener() {
		log.debug("{} created", this.getClass().getSimpleName());
	}
	
	@Override
	public void handleEvent(CorrectionRequestCreatedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getCreatedRequest();
		String to = event.getCreatedRequest().getSubmitter().getNetId();
		
		String msg = String.format("%s, your correction request has been received, "
				+ "and will change your work session from %s->%s to %s->%s if approved",
				cr.getSubmitter().getFirstName(), 
				cr.getOriginalSigninTime(),
				cr.getOriginalSignoutTime(),
				cr.getNewSigninTime(),
				cr.getNewSignoutTime());
		log.info("Email that should be sent to {}@rice.edu: \"{}\"", to, msg);
		// TODO actually send email
	}

}
