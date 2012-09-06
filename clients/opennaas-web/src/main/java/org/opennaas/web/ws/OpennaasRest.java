/**
 * 
 */
package org.opennaas.web.ws;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class OpennaasRest {

	private static final Logger						LOGGER	= Logger.getLogger(OpennaasRest.class);

	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * @param messageSource
	 */
	public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * Call a get method using the param path.
	 * 
	 * @param path
	 * @return a ClientResponse
	 */
	public ClientResponse get(String path) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a get method with path: " + path);
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Call a get method using the param path and returning and object of type ret
	 * 
	 * @param path
	 * @param ret
	 * @return an object of type ret
	 */
	public Object get(String path, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a get method with path: " + path
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.TEXT_PLAIN).get(ret);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Call a post method using the param path.
	 * 
	 * @param path
	 * @return a ClientResponse
	 */
	public ClientResponse post(String path) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with path: " + path);
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Call a post method using the param path and returning and object of type ret
	 * 
	 * @param path
	 * @return an object of type ret
	 */
	public Object post(String path, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a post method with path: " + path
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.APPLICATION_XML).post(ret);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Call a post method using the param path <br>
	 * The bean is the object to send in the post
	 * 
	 * @param path
	 * @param bean
	 * @return a ClientResponse
	 */
	public ClientResponse post(String path, Object bean) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with path: " + path);
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, bean);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Call a post method using the param path and returning and object of type ret<br>
	 * The bean is the object to send in the post
	 * 
	 * @param url
	 * @param bean
	 * @return a ClientResponse
	 */
	public Object post(String path, Object bean, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a post method with path: " + path
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(getURL(path));
			response = webResource.type(MediaType.APPLICATION_XML).post(ret, bean);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * @param path
	 * @return the url rest to call
	 */
	private String getURL(String path) {
		String url = messageSource.getMessage(Constants.WS_REST_URL, null, null) + path;
		LOGGER.info("Web service url: " + url);
		return url;
	}
}
