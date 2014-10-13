package org.opennaas.extensions.router.capabilities.api.helper;

/*
 * #%L
 * OpenNaaS :: Router :: Capabilities :: API
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
import java.util.Collection;

import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRouteCollection;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NextHopIPRoute;
import org.opennaas.extensions.router.model.NextHopRoute;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public abstract class StaticRouteApiHelper {

	private static final int	DEFAULT_PREFERENCE_VALUE	= -1;

	/**
	 * Creates a {@link StaticRouteCollection} instance from a {@link Collection} of {@link NextHopRoute}.
	 * 
	 * @param nextHopRoutes
	 * @return
	 */
	public static StaticRouteCollection buildStaticRouteCollection(Collection<NextHopRoute> nextHopRoutes) {

		StaticRouteCollection srCollection = new StaticRouteCollection();

		Collection<StaticRoute> staticRoutes = new ArrayList<StaticRoute>();

		for (NextHopRoute nextHopRoute : nextHopRoutes)
			if (nextHopRoute.isIsStatic() && nextHopRoute instanceof NextHopIPRoute) {

				StaticRoute sr = buildStaticRoute((NextHopIPRoute) nextHopRoute);
				staticRoutes.add(sr);
			}

		srCollection.setStaticRoutes(staticRoutes);

		return srCollection;

	}

	/**
	 * Creates a {@link StaticRoute} instance parsing the information of a {@link NextHopIPRoute} one. If the {@link NextHopIPRoute} object is
	 * associated with a {@link ProtocolEndpoint}, the {@link StaticRoute#isDiscard() method would return false, true otherwise.}
	 * 
	 * @param nextHopRoute
	 * @return
	 */
	public static StaticRoute buildStaticRoute(NextHopIPRoute nextHopRoute) {

		StaticRoute sr = new StaticRoute();

		String destAddr = nextHopRoute.getDestinationAddress();
		String destMask = nextHopRoute.getDestinationMask();
		String destNet = IPUtilsHelper.ipAddressAndMaskToComposedIPAddress(destAddr, destMask);
		sr.setNetIdIpAdress(destNet);

		if (nextHopRoute.getRouteMetric() != DEFAULT_PREFERENCE_VALUE)
			sr.setPreference(nextHopRoute.getRouteMetric());

		sr.setDiscard(nextHopRoute.getProtocolEndpoint() == null);

		if (nextHopRoute.getProtocolEndpoint() != null) {
			// Static routes have associated IPProtocolEndpoints
			IPProtocolEndpoint ipE = (IPProtocolEndpoint) nextHopRoute.getProtocolEndpoint();
			if (ipE.getProtocolIFType().equals(ProtocolIFType.IPV4))
				sr.setNextHopIpAddress(ipE.getIPv4Address());
			else
				sr.setNextHopIpAddress(ipE.getIPv6Address());

		}

		return sr;
	}
}
