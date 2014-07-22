package org.opennaas.extensions.router.capability.linkaggregation;

/*
 * #%L
 * OpenNaaS :: Router :: Link Aggregation Capability
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
public class LinkAggregationActionSet implements IActionSetDefinition {

	/**
	 * Configures a new aggregated interface specified by given AggregatedLogicalPort.
	 * 
	 * @param AggregatedLogicalPort
	 *            to be created
	 */
	public static final String	CREATE_AGGREGATED_INTERFACE	= "createAggregatedInterfaceAction";

	/**
	 * Removes an already configured aggregated interface, identified by given aggregatedInterfaceId.
	 * 
	 * @param aggregatedInterfaceId
	 *            which is the elementName of the AggregatedLogicalPort to remove
	 */
	public static final String	REMOVE_AGGREGATED_INTERFACE	= "removeAggregatedInterfaceAction";

}
