package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ProxyClassLoader;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.FloodlightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.serializers.json.CustomJSONProvider;

/**
 * Floodlight Ports Statistics API client Factory using {@link IFloodlightPortsStatisticsClient}
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class FloodlightPortsStatisticsClientFactory {

	public IFloodlightPortsStatisticsClient createClient(ProtocolSessionContext sessionContext) {

		String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
		String switchId = (String) sessionContext.getSessionParameters().get(FloodlightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
		// TODO use switch id to instantiate the client

		// create CXF client
		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(IFloodlightPortsStatisticsClient.class.getClassLoader());
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress(uri);
		bean.setProvider(new CustomJSONProvider());
		bean.setResourceClass(IFloodlightPortsStatisticsClient.class);
		bean.setClassLoader(classLoader);

		IFloodlightPortsStatisticsClient client = (IFloodlightPortsStatisticsClient) bean.create();

		return client;
	}

	public IFloodlightPortsStatisticsClient destroyClient() {
		return null;
	}

}
