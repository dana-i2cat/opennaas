package org.opennaas.extensions.contentprovisioning.mediaencoder.client;

/*
 * #%L
 * OpenNaaS :: Content Provisioning
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
