package edu.helpdesk.signin.util;

import edu.helpdesk.signin.model.EmployeeType;

/**
 * Exception that represents a failed authentication
 * @author galen
 *
 */
public class AuthenticationFailedException extends Exception {
	private static final long serialVersionUID = -7000330482870306793L;

	public AuthenticationFailedException() {
	}
	
	public AuthenticationFailedException(EmployeeType required, EmployeeType actual) {
		this(String.format("Required employee type %s, got employee type %s", required, actual));
	}

	public AuthenticationFailedException(String message) {
		super(message);
	}

	public AuthenticationFailedException(Throwable cause) {
		super(cause);
	}

	public AuthenticationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
