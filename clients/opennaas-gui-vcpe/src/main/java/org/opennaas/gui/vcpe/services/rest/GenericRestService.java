/**
 * 
 */
package org.opennaas.gui.vcpe.services.rest;

import java.util.Locale;

import javax.ws.rs.core.Response.Status.Family;

import org.apache.log4j.Logger;
import org.opennaas.gui.vcpe.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Jordi
 */
public abstract class GenericRestService {

	private static final Logger						LOGGER	= Logger.getLogger(GenericRestService.class);

	@Autowired
	private ReloadableResourceBundleMessageSource	configSource;

	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * @param path
	 * @return the url rest to call
	 */
	protected String getURL(String path) {
		String url = configSource.getMessage(Constants.WS_REST_URL, null, Locale.getDefault()) + path;
		LOGGER.info("Web service url: " + url);
		return url;
	}

	/**
	 * Check if response code is between 200 and 299
	 * 
	 * @param code
	 * @return true if response code is between 200 and 299
	 * @throws RestServiceException
	 */
	protected Boolean checkResponse(ClientResponse response) throws RestServiceException {
		LOGGER.info("Response: " + response);
		Family family = ClientResponse.Status.fromStatusCode(response.getStatus()).getFamily();
		if (family.equals(Family.SERVER_ERROR)) {
			String message = response.getEntity(String.class);
			throw new RestServiceException((message != null && !message.equals("")) ?
					message : messageSource.getMessage("message.error.notdetailmessage", null, null));
		} else if (!family.equals(Family.SUCCESSFUL)) {
			throw new RestServiceException(response.toString());
		}
		return true;
	}
}
