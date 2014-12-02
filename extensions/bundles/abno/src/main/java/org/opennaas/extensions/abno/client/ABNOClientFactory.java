package org.opennaas.extensions.abno.client;

import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

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
		bean.setAddress(uri);
		bean.setResourceClass(IABNOClient.class);
		bean.setClassLoader(classLoader);

		return (IABNOClient) bean.create();
	}
}
