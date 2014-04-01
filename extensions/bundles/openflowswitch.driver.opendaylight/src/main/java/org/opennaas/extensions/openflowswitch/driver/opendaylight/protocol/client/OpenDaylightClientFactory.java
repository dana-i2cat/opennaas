package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client;

import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ProxyClassLoader;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.OpenDaylightProtocolSession;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.serializers.json.CustomJSONProvider;

/**
 * @author Isart Canyameres Gimenez (i2cat Foundation)
 * @author Adrian Rosello (i2CAT)
 *
 */
public class OpenDaylightClientFactory {

    private final String odlUserName = "admin";
    private final String odlPassword = "admin";

    public IOpenDaylightStaticFlowPusherClient createClient(ProtocolSessionContext sessionContext) {
        String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
        String switchId = (String) sessionContext.getSessionParameters().get(OpenDaylightProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
		// TODO use switch id to instantiate the client

        // create CXF client
        ProxyClassLoader classLoader = new ProxyClassLoader();
        classLoader.addLoader(IOpenDaylightStaticFlowPusherClient.class.getClassLoader());
        classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(uri);
        bean.setUsername(odlUserName);
        bean.setPassword(odlPassword);
        bean.setProvider(new CustomJSONProvider());
        bean.setResourceClass(IOpenDaylightStaticFlowPusherClient.class);
        bean.setClassLoader(classLoader);

        IOpenDaylightStaticFlowPusherClient cxfClient = (IOpenDaylightStaticFlowPusherClient) bean.create();

        // create mixed client using CXF and custom Java clients
        IOpenDaylightStaticFlowPusherClient client = new OpenDaylightStaticFlowPusherClient(cxfClient, sessionContext);

        return client;
    }

    public IOpenDaylightStaticFlowPusherClient destroyClient() {
        return null;
    }
}
