package edu.helpdesk.signin.web.util;

import javax.ws.rs.core.Response;

public interface WebTask {
	Response doTask() throws Exception;
}
