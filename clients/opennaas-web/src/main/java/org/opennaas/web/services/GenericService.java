/**
 * 
 */
package org.opennaas.web.services;

import org.apache.log4j.Logger;
import org.opennaas.web.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author Jordi
 */
public abstract class GenericService {

	private static final Logger						LOGGER	= Logger.getLogger(GenericService.class);
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
}
