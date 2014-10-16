package org.opennaas.extensions.ofertie.ncl.notification.api;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.WebClient;
import org.opennaas.extensions.ofertie.ncl.notification.INCLNotifierClient;

/**
 * 
 * @author Julio Carlos Barrera
 *
 */
public class NCLNotificationAPIClientFactory {

	public static INCLNotificationAPI createClient(String url) {

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(url);
		bean.setResourceClass(INCLNotifierClient.class);

		INCLNotificationAPI cxfClient = (INCLNotificationAPI) bean.create();

		// By enabling async HTTP conduit, as side-effect, support for HTTP DELETE methods with body is available.
		// https://issues.apache.org/jira/browse/CXF-5337
		WebClient.getConfig(cxfClient).getRequestContext().put("use.async.http.conduit", true);

		return cxfClient;
	}

}
