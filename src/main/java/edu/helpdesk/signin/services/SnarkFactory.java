package edu.helpdesk.signin.services;

import edu.helpdesk.signin.model.dto.Employee;

public class SnarkFactory {
	private static SnarkFactory INSTANCE = null;
	
	public static SnarkFactory get(){
		if(INSTANCE == null){
			INSTANCE = new SnarkFactory();
		}
		return INSTANCE;
	}
	
	public SnarkFactory() {
		// TODO Auto-generated constructor stub
	}
	
	
	public String getSnark(int time, Employee e){
		return "If only I had a brain";
	}

}
