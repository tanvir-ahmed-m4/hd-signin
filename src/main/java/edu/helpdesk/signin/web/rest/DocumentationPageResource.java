package edu.helpdesk.signin.web.rest;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.helpdesk.signin.web.util.Description;
import edu.helpdesk.signin.web.util.WebTask;
import edu.helpdesk.signin.web.util.WebTaskExecutor;

@Component
@Path("/docs")
public class DocumentationPageResource {
	private static final Logger log = LoggerFactory.getLogger(DocumentationPageResource.class);

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
		SigninPageResource.class,
		DocumentationPageResource.class
	};

	/** Helper class for generating JSON */
	private static class HttpMethod implements Comparable<HttpMethod>{
		public String path = "";
		public String method = "";
		public String description = "";
		public String paramClass = "";
		public List<String> queryParams = new ArrayList<>();


		public JSONObject toJson() throws JSONException{
			JSONObject out = new JSONObject();
			if(path.trim().length() > 0){
				out.put("path", path);
			}

			if(method.trim().length() > 0){
				out.put("method", method);
			}
			if(description.trim().length() > 0){
				out.put("description", description);
			}

			if(paramClass.trim().length() > 0){
				out.put("paramClass", paramClass);
			}

			if(queryParams.size() > 0){
				out.put("queryParams", queryParams);
			}

			return out;
		}

		@Override
		public int compareTo(HttpMethod o) {
			return this.path.compareTo(o.path);
		}
	}

	
	private JSONObject docs = null;
	
	public DocumentationPageResource() {}


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/apireference")
	@Description("Get documentation for the REST methods that can be used")
	public Response getDoc() throws JSONException{
		return WebTaskExecutor.doWebTaskSafe(new WebTask() {
			@Override
			public Response doTask() throws Exception {
				ensureNotNull();
				return Response.ok(docs).build();
			}
		});
	}
	
	private void ensureNotNull(){
		if(this.docs == null){
			long start = System.currentTimeMillis();
			log.debug("Building documentation for API request methods...");
			buildObjectInternal();
			long diff = System.currentTimeMillis() - start;
			log.debug("Done building documentation for API request methods ({} milliseconds)", diff);
		}
	}
	
	private void buildObjectInternal(){
		try{
			List<JSONObject> objects = new ArrayList<>();

			for(int i = 0; i < DOCUMENTED_CLASSES.length; i++){
				Class<?> c = DOCUMENTED_CLASSES[i];
				objects.add(buildJsonObject(getRootPath(c), generateMethods(c)));
			}
			
			JSONObject object = new JSONObject();
			object.put("apiEndpoints", objects);
			this.docs = object;
		}catch(JSONException e){
			log.warn("Failed to build JSON documentation for API request methods", e);
		}
	}

	private JSONObject buildJsonObject(String rootPath, List<HttpMethod> methods) throws JSONException{
		JSONObject out = new JSONObject();
		
		out.put("path", rootPath);

		List<JSONObject> list = new ArrayList<>();

		for(int i = 0; i < methods.size(); i++){
			list.add(methods.get(i).toJson());
		}

		out.put("subpaths", list);
		
		return out;
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
		Collections.sort(methods);
		return methods;
	}

}
