/**
 * 
 */
package org.opennaas.web.ws;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * @author Jordi
 */
public class OpennaasRest {

	private static final Logger	LOGGER	= Logger.getLogger(OpennaasRest.class);

	/**
	 * Call a get method using the param url.
	 * 
	 * @param url
	 * @return a ClientResponse
	 */
	public ClientResponse get(String url) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a get method with url: " + url);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a get method using the param url and returning and object of type ret
	 * 
	 * @param url
	 * @param ret
	 * @return an object of type ret
	 */
	public Object get(String url, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a get method with url: " + url
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ret);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a post method using the param url.
	 * 
	 * @param url
	 * @return a ClientResponse
	 */
	public ClientResponse post(String url) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with url: " + url);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a post method using the param url and returning and object of type ret
	 * 
	 * @param url
	 * @return an object of type ret
	 */
	public Object post(String url, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a post method with url: " + url
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ret);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a post method using the param url <br>
	 * The bean is the object to send in the post
	 * 
	 * @param url
	 * @param bean
	 * @return a ClientResponse
	 */
	public ClientResponse post(String url, Object bean) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with url: " + url);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, bean);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

	/**
	 * Call a post method using the param url and returning and object of type ret<br>
	 * The bean is the object to send in the post
	 * 
	 * @param url
	 * @param bean
	 * @return a ClientResponse
	 */
	public Object post(String url, Object bean, Class<?> ret) {
		Object response = null;
		try {
			LOGGER.info("Execute a post method with url: " + url
					+ " and return class: " + ret.getClass().getCanonicalName());
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ret, bean);
			LOGGER.info("Response class is: " + response.getClass().getCanonicalName());
		} catch (ClientHandlerException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
		return response;
	}

}
