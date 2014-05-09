package org.opennaas.extensions.contentprovisioning.mediaencoder.client;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.opennaas.extensions.contentprovisioning.mediaencoder.api.IMediaEncoder;

/**
 * Elemental Live REST client factory
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class MediaEncoderClientFactory {

	/**
	 * Retrieves a REST client based on API defined in {@link IMediaEncoder} *
	 * 
	 * @param baseUri
	 *            base URI of remote server
	 * @return {@link IMediaEncoder} instance to be used as client
	 */
	public static IMediaEncoder getClient(String baseUri) {
		return JAXRSClientFactory.create(baseUri, IMediaEncoder.class);
	}

}
