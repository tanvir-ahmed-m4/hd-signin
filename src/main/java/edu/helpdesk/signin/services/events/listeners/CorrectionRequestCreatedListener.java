package edu.helpdesk.signin.services.events.listeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import edu.helpdesk.signin.dao.EmployeeDao;
import edu.helpdesk.signin.model.CorrectionRequest;
import edu.helpdesk.signin.model.EmployeeType;
import edu.helpdesk.signin.model.dto.Employee;
import edu.helpdesk.signin.services.EmailService;
import edu.helpdesk.signin.services.events.ApplicationEventListener;
import edu.helpdesk.signin.services.events.CorrectionRequestCreatedEvent;

public class CorrectionRequestCreatedListener implements ApplicationEventListener<CorrectionRequestCreatedEvent> {
	private final Logger log = LoggerFactory.getLogger(CorrectionRequestCreatedListener.class);

	private static final String newSessionStart = "${newsession.start}";
	private static final String newSessionEnd = "${newsession.end}";

	private static final String oldSessionStart = "${oldsession.start}";
	private static final String oldSessionEnd = "${oldsession.end}";

	private static final String employeeFirstName = "${employe.firstname}";

	@Autowired
	private EmailService emailService;

	@Autowired
	private EmployeeDao employeeDao;
	
	@Value("${email.template.crc}")
	private String templatePath;

	public CorrectionRequestCreatedListener() {}

	@Override
	public void handleEvent(CorrectionRequestCreatedEvent event)
			throws Exception {
		final CorrectionRequest cr = event.getCreatedRequest();
		
		// don't send an email if the user is a supervisor or above, because
		// these people can correct their own time sheets
		if(cr.getSubmitter().getEmployeeType().isAboveOrEqualTo(EmployeeType.SUPERVISOR)){
			return;
		}
		
		final String subject = String.format("%s has submitted a timesheet correction request", cr.getSubmitter().getFirstName());
		final String emailBody = populateTemplate(loadTemplate(), cr);
		
		final List<String> to = getAdminEmails();
		final List<String> cc = Arrays.asList(String.format("%s@rice.edu", cr.getSubmitter().getNetId()));
		
		// send the emails
		emailService.send(emailService.createEmail(to, cc, subject, emailBody));
	}

	/**
	 * Get the emails of all the people who can approve a correction request
	 * @return
	 */
	private List<String> getAdminEmails(){
		List<Employee> admins = employeeDao.getAllEmployeesAtOrAboveLevel(EmployeeType.SUPERVISOR, false);
		List<String> adminEmails = new ArrayList<>();
		
		for(Employee e : admins){
			adminEmails.add(String.format("%s@rice.edu", e.getNetId()));
		}
		
		return adminEmails;
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
				out.append(temp).append("\n");
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
		try{
			keys.put(employeeFirstName, cr.getSubmitter().getFirstName());
			keys.put(newSessionEnd, String.valueOf(cr.getNewSignoutTime()));
			keys.put(newSessionStart, String.valueOf(cr.getNewSigninTime()));
			keys.put(oldSessionEnd, String.valueOf(cr.getOriginalSignoutTime()));
			keys.put(oldSessionStart, String.valueOf(cr.getOriginalSigninTime()));
		}catch(Exception e){
			log.warn("Error populating email template fields", e);
		}
		return emailService.replaceSubstitutions(template, keys);
	}

}
