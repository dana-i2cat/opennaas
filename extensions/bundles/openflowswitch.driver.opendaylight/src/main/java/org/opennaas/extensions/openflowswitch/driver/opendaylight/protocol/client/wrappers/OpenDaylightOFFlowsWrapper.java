package org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.wrappers;

import java.util.ArrayList;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 * Object wrapper of ArrayList {@link OpenDaylightOFFlow} to allow custom JSON deserialization
 * 
 * @author Julio Carlos Barrera
 */
public class OpenDaylightOFFlowsWrapper extends ArrayList<OpenDaylightOFFlow> {

	private static final long	serialVersionUID	= -3635412232071232709L;

}
