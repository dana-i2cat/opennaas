package org.opennaas.extensions.ofertie.ncl.notification.api;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
import org.apache.cxf.jaxrs.client.WebClient;

/**
 * 
 * @author Julio Carlos Barrera
 *
 */
public class NCLNotificationAPIClientFactory {

	public static INCLNotificationAPI createClient(String url) {

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();

		ProxyClassLoader classLoader = new ProxyClassLoader();

		bean.setAddress(url);
		bean.setResourceClass(INCLNotificationAPI.class);
		bean.setClassLoader(classLoader);

		INCLNotificationAPI cxfClient = (INCLNotificationAPI) bean.create();

		// By enabling async HTTP conduit, as side-effect, support for HTTP DELETE methods with body is available.
		// https://issues.apache.org/jira/browse/CXF-5337
		WebClient.getConfig(cxfClient).getRequestContext().put("use.async.http.conduit", true);

		return cxfClient;
	}

}
