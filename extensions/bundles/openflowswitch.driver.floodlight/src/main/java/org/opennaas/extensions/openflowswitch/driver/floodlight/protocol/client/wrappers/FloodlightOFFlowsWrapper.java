package org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.client.wrappers;

import java.util.ArrayList;

import org.opennaas.extensions.openflowswitch.model.FloodlightOFFlow;

/**
 * Object wrapper of ArrayList {@link FloodlightOFFlow} to allow custom JSON deserialization
 * 
 * @author Julio Carlos Barrera
 */
public class FloodlightOFFlowsWrapper extends ArrayList<FloodlightOFFlow> {

	private static final long	serialVersionUID	= -3635412232071232709L;

}
