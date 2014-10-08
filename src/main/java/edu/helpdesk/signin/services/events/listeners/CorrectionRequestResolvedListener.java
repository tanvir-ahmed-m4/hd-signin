package edu.helpdesk.signin.services.events.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestResolvedEvent;

public class CorrectionRequestResolvedListener implements ApplicationEventListener<CorrectionRequestResolvedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestResolvedListener.class);
	
	public CorrectionRequestResolvedListener() {
		log.debug("{} created", this.getClass().getSimpleName());
	}
	
	@Override
	public void handleEvent(CorrectionRequestResolvedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getRequest();
		String to = event.getRequest().getSubmitter().getNetId();
		
		String msg = String.format("%s, your correction request has been approved by %s, "
				+ "and your work session has changed from %s->%s to %s->%s ",
				cr.getSubmitter().getFirstName(), 
				cr.getCompleter().getFirstName(),
				cr.getOriginalSigninTime(),
				cr.getOriginalSignoutTime(),
				cr.getNewSigninTime(),
				cr.getNewSignoutTime());
		log.info("Email that should be sent to {}@rice.edu: \"{}\"", to, msg);
		// TODO actually send email
	}

}
