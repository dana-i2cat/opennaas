/**
 * 
 */
package org.opennaas.web.services;

import org.apache.log4j.Logger;
import org.opennaas.web.services.rest.RestServiceException;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Jordi
 */
public abstract class GenericRestService {

	private static final Logger						LOGGER					= Logger.getLogger(GenericRestService.class);
	protected static final Integer					HTTP_STATUS_CODE_MAX_OK	= 299;
	protected static final Integer					HTTP_STATUS_CODE_MIN_OK	= 200;

	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	/**
	 * @param path
	 * @return the url rest to call
	 */
	protected String getURL(String path) {
		String url = messageSource.getMessage(Constants.WS_REST_URL, null, null) + path;
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
		if (response.getStatus() > HTTP_STATUS_CODE_MAX_OK
				|| response.getStatus() < HTTP_STATUS_CODE_MIN_OK) {
			throw new RestServiceException(response);
		}
		return true;
	}
}
