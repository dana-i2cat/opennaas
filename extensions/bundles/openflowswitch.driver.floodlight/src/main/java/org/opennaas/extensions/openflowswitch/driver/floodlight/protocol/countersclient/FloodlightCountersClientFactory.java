package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ProxyClassLoader;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.countersclient.serializers.json.CustomJSONProvider;

/**
 * Floodlight Counters API client Factory using {@link IFloodlightCountersClient}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class FloodlightCountersClientFactory {

	public IFloodlightCountersClient createClient(ProtocolSessionContext sessionContext) {

		String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		String switchId = (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
		// TODO use switch id to instantiate the client

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(IFloodlightCountersClient.class.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(uri);
		bean.setProvider(new CustomJSONProvider());
		bean.setResourceClass(IFloodlightCountersClient.class);
		bean.setClassLoader(classLoader);

		IFloodlightCountersClient client = (IFloodlightCountersClient) bean.create();

		return client;
	}

	public IFloodlightCountersClient destroyClient() {
		return null;
	}

}
