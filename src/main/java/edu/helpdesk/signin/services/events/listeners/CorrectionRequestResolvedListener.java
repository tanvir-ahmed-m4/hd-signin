package edu.helpdesk.signin.services.events.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.CorrectionRequestStatus;
import edu.helpdesk.signin.services.EmailService;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestResolvedEvent;

public class CorrectionRequestResolvedListener implements ApplicationEventListener<CorrectionRequestResolvedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestResolvedListener.class);
	
	private static final String requestorFirstName = "${requestor.firstname}";
	private static final String correctionStatus = "${correction.status}";
	private static final String completerName = "${completer.name}";
	private static final String sessionStart = "${session.start}";
	private static final String sessionEnd = "${session.end}";
	
	@Autowired
	private EmailService emailService;

	@Value("${email.template.crr}")
	private String templatePath;
	
	public CorrectionRequestResolvedListener() {
		log.debug("{} created", this.getClass().getSimpleName());
	}

	@Override
	public void handleEvent(CorrectionRequestResolvedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getRequest();
		
		// don't notify people that they resolved their own requests
		if(cr.getSubmitter().getId() == cr.getCompleter().getId()){
			return;
		}
		
		final String subject = String.format("%s has resolved a time sheet correction request", cr.getCompleter().getFirstName());
		final String emailBody = populateTemplate(loadTemplate(), cr);

		List<String> to = Arrays.asList(String.format("%s@rice.edu", cr.getSubmitter().getNetId()));
		List<String> cc = Arrays.asList(String.format("%s@rice.edu", cr.getCompleter().getNetId()));
		
		// send the emails
		emailService.send(emailService.createEmail(to, cc, subject, emailBody));
	}

	/**
	 * Load the email template
	 * @return
	 */
	private String loadTemplate(){
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(this.templatePath)));
		StringBuilder out = new StringBuilder();
		String temp;

		try {
			while((temp = reader.readLine()) != null){
				out.append(temp).append("<br />\n");
			}
		} catch (IOException e) {
			log.warn("Error reading in template", e);
		}

		try {
			reader.close();
		} catch (IOException e) {
			//ignore
		}
		return out.toString();
	}

	/**
	 * Populate the email template with all of the data
	 * @param template
	 * @param cr
	 * @return
	 */
	private String populateTemplate(String template, CorrectionRequest cr){
		
		Map<String, String> keys = new HashMap<String, String>();
		String newStatus = "resolved in an unknown way";
		if(CorrectionRequestStatus.APPROVED.equals(cr.getStatus())){
			newStatus = "approved";
		}
		else if(CorrectionRequestStatus.DENIED.equals(cr.getStatus())){
			newStatus = "denied";
		}
		
		Date in = CorrectionRequestStatus.APPROVED.equals(cr.getStatus()) ? cr.getNewSigninTime() : cr.getOriginalSigninTime();
		Date out = CorrectionRequestStatus.APPROVED.equals(cr.getStatus()) ? cr.getNewSignoutTime() : cr.getOriginalSignoutTime();
		
		try{
			keys.put(requestorFirstName, cr.getSubmitter().getFirstName());
			keys.put(correctionStatus, newStatus);
			keys.put(completerName, String.valueOf(cr.getCompleter().getFirstName() + ' ' + cr.getCompleter().getLastName()));
			keys.put(sessionStart, String.valueOf(in));
			keys.put(sessionEnd, String.valueOf(out));
		}catch(Exception e){
			log.warn("Error populating email template fields", e);
		}
		return emailService.replaceSubstitutions(template, keys);
	}

}
