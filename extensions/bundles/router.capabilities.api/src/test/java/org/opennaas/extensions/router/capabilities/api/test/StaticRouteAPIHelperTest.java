package org.opennaas.extensions.router.capabilities.api.test;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.router.capabilities.api.helper.StaticRouteApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRouteCollection;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NextHopIPRoute;
import org.opennaas.extensions.router.model.NextHopRoute;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class StaticRouteAPIHelperTest {

	private String	DST_NET_ADDRESS_1	= "192.168.1.0";
	private String	DST_NET_ADDRESS_2	= "192.168.2.0";
	private String	DST_NET_MASK		= "24";

	private String	NEXT_HOP_ADDRESS_1	= "10.10.10.11";

	private String	SRC_XML_PATH		= "/staticRouteCollection.xml";

	@Test
	public void buildStaticRouteCollectionTest() throws IOException, SerializationException {

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SRC_XML_PATH));
		StaticRouteCollection srCollection = (StaticRouteCollection) ObjectSerializer.fromXml(xml, StaticRouteCollection.class);

		Collection<NextHopRoute> nextHopRoutes = generateSampleNextHopRoutes();

		StaticRouteCollection builtSrCollection = StaticRouteApiHelper.buildStaticRouteCollection(nextHopRoutes);

		Assert.assertEquals("Built StaticRouteCollection is not equals as the expected one.", srCollection, builtSrCollection);

	}

	private Collection<NextHopRoute> generateSampleNextHopRoutes() {

		Collection<NextHopRoute> nhrCollection = new ArrayList<NextHopRoute>();

		NextHopIPRoute nhr1 = generateSampleNextHopIPRoute(DST_NET_ADDRESS_1, DST_NET_MASK, NEXT_HOP_ADDRESS_1);
		NextHopIPRoute nhr2 = generateSampleNextHopIPRoute(DST_NET_ADDRESS_2, DST_NET_MASK);

		nhrCollection.add(nhr1);
		nhrCollection.add(nhr2);

		return nhrCollection;
	}

	/**
	 * Generates a {@link NextHopIPRoute} instance from destination ip and mask, without an {@link IPProtocolEndpoint} associated to it. IPv4 only! It
	 * implies that the "discard" option is active.
	 * 
	 * @param dstAdress
	 * @param dstMask
	 * @param nextHopAddress
	 * @return
	 */
	private NextHopIPRoute generateSampleNextHopIPRoute(String dstAdress, String dstMask) {

		NextHopIPRoute nhRoute = new NextHopIPRoute();

		nhRoute.setDestinationAddress(dstAdress);
		nhRoute.setIsStatic(true);
		nhRoute.setDestinationMask(IPUtilsHelper.parseShortToLongIpv4NetMask(dstMask));

		return nhRoute;
	}

	/**
	 * Generates a {@link NextHopIPRoute} instance from destination ip and mask, with an {@link IPProtocolEndpoint} contaning the nextHopAddress IPv4
	 * only!
	 * 
	 * @param dstAdress
	 * @param dstMask
	 * @param nextHopAddress
	 * @return
	 */
	private NextHopIPRoute generateSampleNextHopIPRoute(String dstAdress, String dstMask, String nextHopAddress) {

		NextHopIPRoute nhRoute = new NextHopIPRoute();

		nhRoute.setDestinationAddress(dstAdress);
		nhRoute.setDestinationMask(IPUtilsHelper.parseShortToLongIpv4NetMask(dstMask));
		nhRoute.setIsStatic(true);

		IPProtocolEndpoint ipEndpoint = new IPProtocolEndpoint();
		ipEndpoint.setProtocolIFType(ProtocolIFType.IPV4);
		ipEndpoint.setIPv4Address(nextHopAddress);
		ipEndpoint.setSubnetMask("255.255.255.255");
		nhRoute.setProtocolEndpoint(ipEndpoint);

		return nhRoute;
	}
}
