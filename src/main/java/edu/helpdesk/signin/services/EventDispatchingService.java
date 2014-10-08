package edu.helpdesk.signin.services;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.AbstractApplicationContext;

import edu.helpdesk.signin.services.events.ApplicationEvent;
import edu.helpdesk.signin.services.events.ApplicationEventListener;

/**
 * A service for dispatching events to event listeners for the program.
 * In order to register a class to receive event notifications, create a bean
 * that implements {@link ApplicationEventListener} with the appropriate
 * event type to be notified of, and this class will automatically pick it up
 * and dispatch appropriate events to it.
 * @author galen
 *
 */
public class EventDispatchingService implements ApplicationContextAware{
	private static final Logger log = LoggerFactory.getLogger(EventDispatchingService.class);
	private static EventDispatchingService INSTANCE = null;
	
	private ApplicationContext ctx = null;

	private int listenerCount = 0;
	private int eventTypeCount = 0;

	private final Map<Class<?>, List<ApplicationEventListener<? extends ApplicationEvent>>> listeners;

	/**
	 * Creates a new EventDispatchingService.<br />
	 * This should only be called by Spring, and will throw a {@link IllegalStateException}
	 * if a user program attempts to call it.
	 * @throws IllegalStateException
	 */
	public EventDispatchingService() throws IllegalStateException {
		if(INSTANCE != null){
			throw new IllegalStateException("Cannot create more than one instance of " + this.getClass().getCanonicalName());
		}
		INSTANCE = this;
		this.listeners = new HashMap<Class<?>, List<ApplicationEventListener<? extends ApplicationEvent>>>();
	}

	/**
	 * Set the application context.
	 * This can only be called once, by spring.
	 * @param applicationContext
	 * @throws IllegalStateException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws IllegalStateException{
		if(this.ctx != null){
			throw new IllegalStateException("Cannot set application context more than once");
		}
		this.ctx = applicationContext;
	}

	public <E extends ApplicationEvent> void doDispatchEvent(E e){
		try{
			logEventInternal(e);
		}catch(Exception ex){
			log.warn("Exception dispatching event", ex);
		}
	}


	@SuppressWarnings("unchecked")
	private <E extends ApplicationEvent> void logEventInternal(E e){
		List<ApplicationEventListener<E>> ls = new ArrayList<ApplicationEventListener<E>>();


		for(Map.Entry<Class<?>, List<ApplicationEventListener<? extends ApplicationEvent>>> entry : listeners.entrySet()){
			if(entry.getKey().isAssignableFrom(e.getClass())){
				List<ApplicationEventListener<? extends ApplicationEvent>> raw = entry.getValue();
				for(ApplicationEventListener<? extends ApplicationEvent> r : raw){
					ls.add((ApplicationEventListener<E>) r);
				}
			}
		}

		for(ApplicationEventListener<E> l : ls){
			try {
				l.handleEvent(e);
			} catch (Exception e1) {
				log.warn("Exception while handling event", e1);
			}
		}
	}


	@PostConstruct
	private void initializeThings(){
		try{
			initializeListeners();
		}catch(Exception e){
			log.warn("Exception initializing event listeners", e);
		}
	}

	private void initializeListeners(){

		// get all the listener beans
		List<ApplicationEventListener<? extends ApplicationEvent>> beans = getListeners(ctx);

		// iterate over the beans and find out what kind of events they listene for
		for(ApplicationEventListener<? extends ApplicationEvent> e : beans){

			// the type of event this listener listens for
			Class<?> key = null;

			// iterate over the interfaces that the class implements, and see
			// what kinds of events they listen for
			root:{
				for(Type ot : e.getClass().getGenericInterfaces()){
					ParameterizedType pt = (ParameterizedType) ot;
					for(Type t : pt.getActualTypeArguments()){
						Class<?> c = (Class<?>) t;
						if(ApplicationEvent.class.isAssignableFrom(c)){
							key = c;
							break root;
						}
					}
				}
			}

			if(key == null){
				log.warn("Class {} implements {}, but did not paramaterize it "
						+ "with a valid application event type", 
						e.getClass().getCanonicalName(), 
						ApplicationEventListener.class.getCanonicalName());
			}
			else{
				
				// make sure that the listeners map has an entry for the event type
				if(!listeners.containsKey(key)){
					listeners.put(key, new ArrayList<ApplicationEventListener<? extends ApplicationEvent>>());
					eventTypeCount++;
				}

				listeners.get(key).add(e);
				listenerCount++;
			}
		}

		log.info("Loaded {} listener{} for {} different types of event{}", 
				listenerCount, isPlural(listenerCount), 
				eventTypeCount, isPlural(eventTypeCount));
	}

	/**
	 * Get all of the beans that implement the {@link ApplicationEventListener}
	 * interface for the given context.
	 * @param ctx
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<ApplicationEventListener<? extends ApplicationEvent>> getListeners(ApplicationContext ctx){
		List<Object> beans = getInstantiatedSigletons(ctx);

		List<ApplicationEventListener<? extends ApplicationEvent>> out = new ArrayList<>();
		for(int i = 0; i < beans.size(); i++){
			Object e = beans.get(i);
			if(e instanceof ApplicationEventListener){
				try{
					out.add((ApplicationEventListener<? extends ApplicationEvent>) e);
				}catch(Exception ex){
					log.warn("Exception", e);
				}
			}
		}
		return out;
	}

	/**
	 * Get all the beans for an application context
	 * @param ctx
	 * @return
	 */
	private List<Object> getInstantiatedSigletons(ApplicationContext ctx) {
		List<Object> singletons = new ArrayList<Object>();
		String[] all = ctx.getBeanDefinitionNames();

		if(!(ctx instanceof AbstractApplicationContext)){
			log.warn("Context is not an {}, even though it should be. Failed to load beans", AbstractApplicationContext.class.getCanonicalName());
			throw new IllegalArgumentException("Application context is not an AbstractApplicationContext, even though it should be");
		}
		
		ConfigurableListableBeanFactory clbf = ((AbstractApplicationContext) ctx).getBeanFactory();
		for (String name : all) {
			Object s = clbf.getSingleton(name);
			if (s != null)
				singletons.add(s);
		}
		return singletons;
	}

	private String isPlural(int size){
		return size == 1 ? "" : "s";
	}
}
