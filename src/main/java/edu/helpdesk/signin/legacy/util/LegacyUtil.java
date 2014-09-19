package edu.helpdesk.signin.legacy.util;

import edu.helpdesk.signin.model.EmployeeType;

public class LegacyUtil {
	private static final LegacyUtil INSTANCE = new LegacyUtil();
	
	public static LegacyUtil get(){
		return INSTANCE;
	}
	
	
	public LegacyUtil() {
		// TODO Auto-generated constructor stub
	}
	
	
	public EmployeeType fromInt(int legacyPermissionLevel){
		switch(legacyPermissionLevel){
		case 3: return EmployeeType.SUPERVISOR;
		case 2: return EmployeeType.SCC_LEAD;
		case 1: return EmployeeType.SCC;
		default: return EmployeeType.NONE;
		}
	}

}
