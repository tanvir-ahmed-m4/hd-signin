package edu.helpdesk.signin.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.net.finger.FingerClient;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SigninUtil {
	private static final SigninUtil INSTACE = new SigninUtil();
	
	public static SigninUtil get(){
		return INSTACE;
	}
	
	
	public SigninUtil() {
		// TODO Auto-generated constructor stub
	}
	
	
	public Map<String, String> finger(String netid) throws Exception{
		final Set<String> KEYS = new HashSet<String>(){
			private static final long serialVersionUID = -801483559828938609L;
			{
				add("name");
				add("class");
				add("matric term");
				add("college");
				add("major");
				add("address");
				add("mailto");
			}
		};

		FingerClient finger = new FingerClient();
		try{
			Preconditions.checkArgument(!Strings.isNullOrEmpty(netid), "netid cannot be null or empty");
			finger.connect("rice.edu");
			String infoRaw = finger.query(false, netid);
			String[] info = infoRaw.split("\n");
			Map<String, String> out = new HashMap<>();

			for(String line : info){
				String[] split = line.split(":", 2);
				if(split.length == 2){
					String key = split[0].trim().toLowerCase();
					String val = split[1].trim();
					if(KEYS.contains(key)){
						out.put(key, val);
					}
				}
			}

			return out;
		}finally{
			try{
				finger.disconnect();
			}catch(Exception ignore){}
		}
	}

}
