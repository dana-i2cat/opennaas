package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.mockup;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.FloodlightClientFactory;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.IFloodlightStaticFlowPusherClient;

/**
 * 
 * @author Adrian Rosello (i2CAT)
 * 
 */
public class FloodlightMockClientFactory extends FloodlightClientFactory {

	@Override
	public IFloodlightStaticFlowPusherClient createClient(ProtocolSessionContext sessionContext) {

		return new FloodlightMockClient();

	}

}
