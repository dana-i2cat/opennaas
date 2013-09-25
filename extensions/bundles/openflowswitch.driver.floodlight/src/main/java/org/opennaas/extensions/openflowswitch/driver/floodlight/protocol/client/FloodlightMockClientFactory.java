package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client;

import org.opennaas.core.resources.protocol.ProtocolSessionContext;

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
