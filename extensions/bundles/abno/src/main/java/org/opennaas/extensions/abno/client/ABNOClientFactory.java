package org.opennaas.extensions.abno.client;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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

import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.abno.client.json.ABNOJSONProvider;

/**
 * {@link IABNOClient} factory
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ABNOClientFactory {

	public IABNOClient createClient(ProtocolSessionContext sessionContext) {

		String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(IABNOClient.class.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setProvider(new ABNOJSONProvider());
		bean.setAddress(uri);
		bean.setResourceClass(IABNOClient.class);
		bean.setClassLoader(classLoader);

		return (IABNOClient) bean.create();
	}
}
