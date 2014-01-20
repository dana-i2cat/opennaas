package org.opennaas.extensions.ofnetwork.capability.monitoring;

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

import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IActionSetDefinition;
import org.opennaas.extensions.ofnetwork.model.NetworkStatistics;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class MonitoringNetworkActionSet implements IActionSetDefinition {

	/**
	 * An action that retrieves the statistics of all port of the network switches. It receives no parameters, and it returns a
	 * {@link NetworkStatistics} object in the {@link ActionResponse#getResult()} method.
	 */
	public static final String	GET_NETWORK_STATISTICS	= "getNetworkStatistics";
}
