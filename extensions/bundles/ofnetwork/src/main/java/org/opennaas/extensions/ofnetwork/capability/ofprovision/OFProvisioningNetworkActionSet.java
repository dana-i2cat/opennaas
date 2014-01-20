package org.opennaas.extensions.ofnetwork.capability.ofprovision;

/*
 * #%L
 * OpenNaaS :: OF Network
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.opennaas.core.resources.action.IActionSetDefinition;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class OFProvisioningNetworkActionSet implements IActionSetDefinition {

	/**
	 * An Action that allocates the flow in the network. It receives an SDNNetworkOFFlow with a Route as a parameter. It returns the flowId in its
	 * ActionResponse.getResult()
	 */
	public static final String	ALLOCATEFLOW		= "allocateFlow";
	/**
	 * An Action that deallocates the flow in the network. It receives an flowId of an allocated SDNNetworkOFFlow as a parameter. It returns nothing
	 * (null) in its ActionResponse.getResult()
	 */
	public static final String	DEALLOCATEFLOW		= "deallocateFlow";
	/**
	 * An Action that retrieves allocated flows in the network. It receives no parameters It returns a collection of SDNNetworkOFFlow in its
	 * ActionResponse.getResult()
	 */
	public static final String	GETALLOCATEDFLOWS	= "getAllocatedFlows";

}
