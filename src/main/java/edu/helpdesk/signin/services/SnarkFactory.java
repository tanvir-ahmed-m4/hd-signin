package edu.helpdesk.signin.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import edu.helpdesk.signin.model.dto.Employee;

public class SnarkFactory {
	private static SnarkFactory INSTANCE = null;
	private static final Logger log = LoggerFactory.getLogger(SnarkFactory.class);

	public static SnarkFactory get(){
		if(INSTANCE == null){
			INSTANCE = new SnarkFactory();
		}
		return INSTANCE;
	}

	private final Random r = new Random();

	private static enum Type{
		SHORT, REG, LONG
	};

	@Value("${snark.location}")
	private String snarkLocation;


	private static final String REGULAR = "REGULAR";
	private static final String SHORT = "SHORT";
	private static final String LONG = "LONG";

	private final List<String> shortTime = new ArrayList<>();
	private final List<String> regularTime = new ArrayList<>();
	private final List<String> longTime = new ArrayList<>();


	public SnarkFactory() {
		if(INSTANCE == null){
			INSTANCE = this;
		}
	}

	@PostConstruct
	private void init(){
		try{
			readFile(this.getClass().getResourceAsStream("/" + snarkLocation));
		}catch(FileNotFoundException e){
			log.warn("Failed to find file {} (CWD: {})", snarkLocation, new File("").getAbsolutePath());
		}catch(Exception e){
			log.warn("Exception reading in snark file", e);
		}
	}

	private void readFile(InputStream stream) throws Exception{
		InputStreamReader isr = new InputStreamReader(stream);
		BufferedReader reader = new BufferedReader(isr);

		String line;

		List<String> dest = null;
		int count = 0;
		while((line = reader.readLine()) != null){
			line = line.trim();

			if(line.startsWith("#")){
				continue;
			}
			if(line.equals(LONG)){
				dest = this.longTime;
				continue;
			}
			if(line.equals(REGULAR)){
				dest = this.regularTime;
				continue;
			}
			if(line.equals(SHORT)){
				dest = this.shortTime;
				continue;
			}
			if(line.length() > 0){
				if(dest != null){
					dest.add(line);
					count++;
				}
				else{
					log.warn("Snark file is poorly formatted, line '{}' does not belong to a section", line);
				}
			}
		}
		log.info("Read in {} snark messages", count);
		reader.close();
	}


	public String getSnark(int time, Employee e){
		try{
			Type t = getType(time);
			List<String> list;

			switch(t){
			case LONG: list = longTime; break;
			case REG: list = regularTime; break;
			case SHORT: list = shortTime; break;
			default: return "How'd you do that?";
			}

			return formatMessage(list.get(r.nextInt(list.size())), time, e);
		}catch(Exception ex){
			log.warn("Exception generating snark", ex);
			return "You broke something!";
		}
	}

	private String formatMessage(String msg, int time, Employee e){
		return msg;
	}


	private Type getType(int time){
		double hours = time / 1000.0 / 60 / 60;
		if(hours < 1.5){
			return Type.SHORT;
		}
		if(hours < 4.2){
			return Type.REG;
		}
		return Type.LONG;

	}

}
