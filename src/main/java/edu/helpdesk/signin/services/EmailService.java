package edu.helpdesk.signin.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private boolean sendingEnabled = true;



	public EmailService() {
		// TODO Auto-generated constructor stub
	}

	public HtmlEmail createEmail(List<String> to, String subject, String htmlBody){
		return createEmail(to, new ArrayList<String>(0), subject, htmlBody);
	}

	public HtmlEmail createEmail(List<String> to, List<String> cc, String subject, String htmlBody){
		return createEmail(to, cc, new ArrayList<String>(0), subject, htmlBody);
	}

	public HtmlEmail createEmail(List<String> to, List<String> cc, List<String> bcc, String subject, String htmlBody){
		HtmlEmail out = new HtmlEmail();
		try{
			out.setFrom("no-reply@rice.edu", "Help Desk Sign-In System");
			out.setHostName("localhost");

			out.setSubject(subject);
			out.setHtmlMsg(htmlBody);
			if(to != null && to.size() > 0)
				out.addTo(to.toArray(new String[]{}));
			if(cc != null && cc.size() > 0)
				out.addCc(cc.toArray(new String[]{}));
			if(bcc != null && bcc.size() > 0)
				out.addBcc(bcc.toArray(new String[]{}));
		}catch(EmailException e){
			log.warn("Exception creating email", e);
			return null;
		}
		return out;
	}


	public boolean send(Email email){
		if(email == null){
			throw new NullPointerException("Email cannot be null");
		}
		if(sendingEnabled == false){
			log.info("Not sending email because sending email is disabled");
			return false;
		}
		try {
			log.debug("Sending email to {}...", email.getToAddresses());
			//email.setDebug(true);
			email.send();
			log.debug("Email sent");
			return true;
		} catch (EmailException e) {
			log.warn("Error sending email", e);
			return false;
		}
	}


	public boolean isSendingEnabled() {
		return sendingEnabled;
	}

	public void setSendingEnabled(boolean sendingEnabled) {
		log.info("Sending emails is now {}", sendingEnabled ? "enabled" : "disabled");
		this.sendingEnabled = sendingEnabled;
	}


}
