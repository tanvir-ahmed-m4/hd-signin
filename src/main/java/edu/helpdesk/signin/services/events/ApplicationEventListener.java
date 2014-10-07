package edu.helpdesk.signin.services.events;



public interface ApplicationEventListener<E extends ApplicationEvent> {
	void handleEvent(E event) throws Exception;
}
