package org.opennaas.extensions.opendaylight.vtn.protocol.client.mockup;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.OpenDaylightClientFactory;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.IOpenDaylightvtnAPIClient;

/**
 *
 * @author Adrian Rosello (i2CAT)
 *
 */
public class OpenDaylightMockClientFactory extends OpenDaylightClientFactory {

    @Override
    public IOpenDaylightvtnAPIClient createClient(ProtocolSessionContext sessionContext) {

        return new OpenDaylightMockClient();

    }

}
