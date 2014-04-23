package org.opennaas.extensions.opendaylight.vtn.protocol.client;

import java.util.HashMap;
import java.util.Map;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.jaxrs.client.ProxyClassLoader;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.serializers.json.CustomJSONProvider;

/**
 * @author Josep Batallé (i2CAT)
 *
 */
public class OpenDaylightClientFactory {

    private final String odlUserName = "admin";
    private final String odlPassword = "adminpass";

    public IOpenDaylightvtnAPIClient createClient(ProtocolSessionContext sessionContext) {
        String uri = (String) sessionContext.getSessionParameters().get(ProtocolSessionContext.PROTOCOL_URI);
        // TODO use switch id to instantiate the client

        // create CXF client
        ProxyClassLoader classLoader = new ProxyClassLoader();
        classLoader.addLoader(IOpenDaylightvtnAPIClient.class.getClassLoader());
        classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());

        Map user = new HashMap<String, String>();
        user.put("username", odlUserName);
        user.put("password", odlPassword);
        
        JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
        bean.setAddress(uri);
        bean.setHeaders(user);
//        bean.setUsername(odlUserName);
//        bean.setPassword(odlPassword);
        bean.setProvider(new CustomJSONProvider());
        bean.setResourceClass(IOpenDaylightvtnAPIClient.class);
        bean.setClassLoader(classLoader);

        IOpenDaylightvtnAPIClient cxfClient = (IOpenDaylightvtnAPIClient) bean.create();

        // create mixed client using CXF and custom Java clients
        IOpenDaylightvtnAPIClient client = new OpenDaylightvtnAPIClient(cxfClient, sessionContext);

        return client;
    }

    public IOpenDaylightvtnAPIClient destroyClient() {
        return null;
    }
}
