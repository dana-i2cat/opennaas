package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.mockup;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.OpenDaylightClientFactory;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class OpenDaylightMockClientFactory extends OpenDaylightClientFactory {

	@Override
	public IOpenDaylightStaticFlowPusherClient createClient(ProtocolSessionContext sessionContext) {

		return new OpenDaylightMockClient();

	}

}

