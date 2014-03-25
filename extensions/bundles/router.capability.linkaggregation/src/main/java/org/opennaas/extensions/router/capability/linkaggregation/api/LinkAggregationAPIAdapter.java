package org.opennaas.extensions.router.capability.linkaggregation.api;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.opennaas.extensions.router.model.AggregatedLogicalPort;
import org.opennaas.extensions.router.model.AggregatedOptions;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class LinkAggregationAPIAdapter {

	public static final String	MINIMUM_LINKS_OPT		= "minimum-links";
	public static final String	LINK_SPEED_OPT			= "link-speed";
	public static final String	LACP_OPT				= "lacp";
	public static final String	LACP_OPT_ACTIVE_VAL		= "active";
	public static final String	LACP_OPT_PASSIVE_VAL	= "passive";
	public static final String	LACP_OPT_DEFAULT_VAL	= LACP_OPT_PASSIVE_VAL;

	/**
	 * 
	 * @param aggregator
	 * @return AggregatedInterface representation for given aggregator
	 */
	public static AggregatedInterface model2Api(AggregatedLogicalPort aggregator) {

		// AggregatedLogicalPort --> AggregatedInterface
		AggregatedInterface aggregatedInterface = new AggregatedInterface();
		aggregatedInterface.setId(aggregator.getElementName());
		List<String> ifaces = new ArrayList<String>(aggregator.getInterfaces().size());
		ifaces.addAll(aggregator.getInterfaces());
		aggregatedInterface.setInterfacesNames(ifaces);

		Map<String, String> aggregationOptions = new HashMap<String, String>();
		if (!StringUtils.isEmpty(aggregator.getAggregatedOptions().getMinimumLinks()))
			aggregationOptions.put(MINIMUM_LINKS_OPT, aggregator.getAggregatedOptions().getMinimumLinks());
		if (!StringUtils.isEmpty(aggregator.getAggregatedOptions().getLinkSpeed()))
			aggregationOptions.put(LINK_SPEED_OPT, aggregator.getAggregatedOptions().getLinkSpeed());
		if (aggregator.getAggregatedOptions().isLacpActive()) {
			aggregationOptions.put(LACP_OPT, LACP_OPT_ACTIVE_VAL);
		} else {
			aggregationOptions.put(LACP_OPT, LACP_OPT_PASSIVE_VAL);
		}
		aggregatedInterface.setAggregationOptions(aggregationOptions);

		return aggregatedInterface;
	}

	/**
	 * 
	 * @param aggregatedInterface
	 * @return AggregatedLogicalPort representation for given aggregatedInterface
	 */
	public static AggregatedLogicalPort api2Model(AggregatedInterface aggregatedInterface) {

		// AggregatedInterface --> AggregatedLogicalPort
		AggregatedLogicalPort aggregator = new AggregatedLogicalPort();
		aggregator.setElementName(aggregatedInterface.getId());

		List<String> ifaces = new ArrayList<String>(aggregatedInterface.getInterfacesNames().size());
		ifaces.addAll(aggregatedInterface.getInterfacesNames());
		aggregator.setInterfaces(ifaces);

		AggregatedOptions aggrOpt = new AggregatedOptions();

		if (aggregatedInterface.getAggregationOptions().containsKey(MINIMUM_LINKS_OPT)) {
			aggrOpt.setMinimumLinks(aggregatedInterface.getAggregationOptions().get(MINIMUM_LINKS_OPT));
		}
		if (aggregatedInterface.getAggregationOptions().containsKey(LINK_SPEED_OPT)) {
			aggrOpt.setLinkSpeed(aggregatedInterface.getAggregationOptions().get(LINK_SPEED_OPT));
		}
		if (aggregatedInterface.getAggregationOptions().containsKey(LACP_OPT)) {
			if (!StringUtils.isEmpty(aggregatedInterface.getAggregationOptions().get(LACP_OPT)) &&
					aggregatedInterface.getAggregationOptions().get(LACP_OPT).equals(LACP_OPT_ACTIVE_VAL))
				aggrOpt.setLacpActive(true);
		}
		// lacpActive default to false, as any non initialized boolean

		aggregator.setAggregatedOptions(aggrOpt);

		return aggregator;
	}

}
