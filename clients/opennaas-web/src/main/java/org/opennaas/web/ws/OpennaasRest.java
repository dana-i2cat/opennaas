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
	 * Execute a post method through the url generated with the parameters resourceType, resourceName, capability and method.
	 * 
	 * @param resourceType
	 * @param resourceName
	 * @param capability
	 * @param method
	 * @param bean
	 * @return the response
	 */
	public ClientResponse executePost(String resourceType, String resourceName, String capability, String method, Object bean) {
		return executePost(getURL(resourceType, resourceName, capability, method), bean);
	}

	/**
	 * Execute a post method through the url generated with the parameters resourceType, resourceName, capability, method and query string.
	 * 
	 * @param resourceType
	 * @param resourceName
	 * @param capability
	 * @param method
	 * @param queryString
	 * @return the response
	 */
	public ClientResponse executeGet(String resourceType, String resourceName, String capability, String method, String queryString) {
		return executeGet(getURL(resourceType, resourceName, capability, method, queryString));
	}

	/**
	 * Execute a post method through the param url.
	 * 
	 * @param url
	 * @param bean
	 * @return the response
	 */
	public ClientResponse executePost(String url, Object bean) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with the url: " + url);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, bean);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * Execute a get method through the param url.
	 * 
	 * @param url
	 * @return the response
	 */
	public ClientResponse executeGet(String url) {
		ClientResponse response = null;
		try {
			LOGGER.info("Execute a post method with the url: " + url);
			Client client = Client.create();
			WebResource webResource = client.resource(url);
			response = webResource.type(MediaType.APPLICATION_XML).get(ClientResponse.class);
			LOGGER.info("Response code: " + response.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return response;
	}

	/**
	 * @param resourceType
	 * @param resourceName
	 * @param capability
	 * @param method
	 * @return
	 */
	private String getURL(String resourceType, String resourceName, String capability, String method) {
		String url = messageSource.getMessage(Constants.WS_REST_URL, null, null);
		url += resourceType + "/" + resourceName + "/" + capability + "/" + method;
		return url;
	}

	/**
	 * @param resourceType
	 * @param resourceName
	 * @param capability
	 * @param method
	 * @param queryString
	 * @return
	 */
	private String getURL(String resourceType, String resourceName, String capability, String method, String queryString) {
		String url = messageSource.getMessage(Constants.WS_REST_URL, null, null);
		url += resourceType + "/" + resourceName + "/" + capability + "/" + method + queryString;
		return url;
	}
}
