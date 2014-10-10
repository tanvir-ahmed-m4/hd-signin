package edu.helpdesk.signin.services;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private static EmailService INSTANCE = null;
	
	public static EmailService get(){
		return INSTANCE;
	}
	
	@Value("${email.from.name}")
	private String emailFromName;
	
	@Value("${email.from.address}")
	private String emailFromAddress;
	
	@Value("${email.hostname}")
	private String emailHostname;
	
	@Value("${email.debugenabled}")
	private boolean debugEnabled;
	
	@Value("${email.enabled}")
	private boolean sendingEnabled;
	
	
	public EmailService() {
		if(INSTANCE != null){
			throw new IllegalStateException("Can't instantiate an EmailService more than once; use the EmailService.get() static method to get an isntance of the class");
		}
		INSTANCE = this;
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
			out.setFrom(emailFromAddress, emailFromName);
			out.setHostName(emailHostname);
			out.addHeader("X-Files", "The Truth Is Out There");
			
			// The email content
			out.setSubject(subject);
			out.setHtmlMsg(htmlBody);
			
			// add the recipients
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
			email.setDebug(debugEnabled);
			String id = email.send();
			log.debug("Email sent (id {})", id);
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
