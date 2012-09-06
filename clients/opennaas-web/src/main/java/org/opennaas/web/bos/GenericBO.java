/**
 * 
 */
package org.opennaas.web.bos;

import org.apache.log4j.Logger;
import org.opennaas.web.utils.Constants;
import org.opennaas.web.ws.OpennaasRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * @author Jordi
 */
public class GenericBO {

	private static final Logger						LOGGER	= Logger.getLogger(GenericBO.class);

	@Autowired
	private ReloadableResourceBundleMessageSource	messageSource;

	@Autowired
	protected OpennaasRest							opennaasRest;

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
