package edu.helpdesk.signin.web.rest;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.helpdesk.signin.web.util.Description;

public class HelpTextGenerator {
	private static final Logger log = LoggerFactory.getLogger(HelpTextGenerator.class);

	/** Name of the file to write */
	private static final String FILE_NAME = "src/main/webapp/apireference.json";
	
	/** HTTP method annotations */
	private static final Set<Class<?>> HTTP_METHODS = new HashSet<Class<?>>(){
		private static final long serialVersionUID = 2118430403768489843L;
		{
			this.add(GET.class);
			this.add(POST.class);
			this.add(PUT.class);
			this.add(DELETE.class);
		}
	};
	
	/** Classes to generate documentation for */
	private static final Class<?>[] DOCUMENTED_CLASSES = {
			AdminPageResource.class,
			SigninPageResource.class
	};

	/** Helper class for generating JSON */
	private static class HttpMethod{
		public String path = "";
		public String method = "";
		public String description = "";
		public String paramClass = "";
		public List<String> queryParams = new ArrayList<>();

		public String toJsonForm(){
			StringBuilder out = new StringBuilder();
			out.append("{");
			boolean includeComma = false;

			if(path.trim().length() > 0){
				out.append(String.format("\"path\": \"%s\"", path));
				includeComma = true;
			}

			if(method.trim().length() > 0){
				if(includeComma){
					out.append(", ");
				}
				out.append(String.format("\"method\": \"%s\"", method));
			}
			if(description.trim().length() > 0){
				if(includeComma){
					out.append(", ");
				}
				out.append(String.format("\"description\": \"%s\"", description));
			}

			if(paramClass.trim().length() > 0){
				if(includeComma){
					out.append(", ");
				}
				out.append(String.format("\"paramClass\": \"%s\"", paramClass));
			}

			if(queryParams.size() > 0){
				if(includeComma){
					out.append(", ");
				}
				out.append(String.format("\"queryParams\": %s", listToJson()));
			}
			out.append("}");
			return out.toString();
		}

		private String listToJson(){
			StringBuilder out = new StringBuilder();
			out.append("[");
			for(int i = 0; i < queryParams.size(); i++){
				out.append('"');
				out.append(queryParams.get(i));
				out.append('"');
				if(i < queryParams.size() - 1){
					out.append(", ");
				}
			}
			out.append("]");
			return out.toString();
		}
	}

	@Test
	public void test() {
		StringBuilder JSON = new StringBuilder();
		
		JSON.append("{\"apiEndpoints\": [\n");
		for(int i = 0; i < DOCUMENTED_CLASSES.length; i++){
			Class<?> c = DOCUMENTED_CLASSES[i];
			String json = buildJsonObject(getRootPath(c), generateMethods(c));
			JSON.append(json);
			if(i < DOCUMENTED_CLASSES.length - 1){
				JSON.append(", \n");
			}
		}
		JSON.append("\n]}");
		writeFile(JSON.toString(), FILE_NAME);
	}
	
	/**
	 * Write the JSON data to disk.
	 * @param dataStr
	 */
	private void writeFile(String dataStr, String filename){
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(new File(filename));
			byte[] data = dataStr.getBytes("UTF-8");
			log.debug("Writing {} bytes of data to '{}'", data.length, filename);
			out.write(data);
		} catch (Exception e) {
			log.warn("Exception writing file", e);
			fail("Exception while writing documentation file");
		}finally{
			if(out != null){
				try{
					out.flush();
					out.close();
				}catch(Exception e){
					log.debug("Exception closing file output stream", e);
				}
			}
		}
	}

	private String buildJsonObject(String rootPath, List<HttpMethod> methods){
		StringBuilder out = new StringBuilder();
		out.append("{");
		out.append(String.format("\"path\": \"%s\"", rootPath));
		out.append(", \n");
		out.append("\"subpaths\": [\n");

		for(int i = 0; i < methods.size(); i++){
			out.append(String.format("\t%s", methods.get(i).toJsonForm()));
			if(i < methods.size() - 1){
				out.append(", \n");
			}
		}

		out.append("\n]}");
		return out.toString();
	}


	private String getRootPath(Class<?> underTest){
		String rootPath = "";
		for(Annotation a : underTest.getAnnotations()){
			if(a.annotationType().equals(Path.class)){
				rootPath = ((Path)a).value();
			}
		}
		return rootPath;
	}

	private List<HttpMethod> generateMethods(Class<?> underTest){
		List<HttpMethod> methods = new ArrayList<>();

		for(Method method : underTest.getDeclaredMethods()){
			HttpMethod holder = new HttpMethod();
			boolean isGet = false;
			boolean isHttpMethod = false;

			for(Annotation a : method.getAnnotations()){

				if(HTTP_METHODS.contains(a.annotationType())){
					isHttpMethod = true;
					holder.method = a.annotationType().getSimpleName();
					if(a.annotationType().equals(GET.class)){
						isGet = true;
					}
				}
				else if(a.annotationType().equals(Path.class)){
					holder.path = ((Path)a).value();
				}
				else if(a.annotationType().equals(Description.class)){
					holder.description = ((Description)a).value();
				}
			}

			if(isHttpMethod == false){
				continue;
			}


			for(Annotation[] as : method.getParameterAnnotations()){
				for(Annotation a : as){
					if(a.annotationType().equals(QueryParam.class)){
						holder.queryParams.add(((QueryParam)a).value());
					}
				}
			}

			if(isGet == false){
				for(Class<?> c : method.getParameterTypes()){
					if(c.equals(HttpServletRequest.class))
						continue;
					holder.paramClass = c.getCanonicalName();
				}
			}
			methods.add(holder);
		}

		return methods;
	}

}
