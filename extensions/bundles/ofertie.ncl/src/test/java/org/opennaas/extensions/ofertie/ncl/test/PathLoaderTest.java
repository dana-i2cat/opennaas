package org.opennaas.extensions.ofertie.ncl.test;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing.PathLoader;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NetworkConnection;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;

public class PathLoaderTest {

	private final static String	PATH_FILE_URL				= "/routes/sampleXMLRoutes.xml";

	private final static String	ROUTE_0_ID					= "0";
	private final static String	ROUTE_1_ID					= "1";
	private final static String	ROUTE_2_ID					= "2";

	private static final String	DEVICE_H1_ID				= "h1";
	private static final String	DEVICE_H2_ID				= "h2";
	private static final String	DEVICE_S3_ID				= "s3";
	private static final String	DEVICE_S4_ID				= "s4";
	private static final String	DEVICE_S5_ID				= "s5";
	private static final String	DEVICE_S6_ID				= "s6";
	private static final String	DEVICE_S7_ID				= "s7";
	private static final String	DEVICE_S8_ID				= "s8";

	private static final String	NET_CONNECTION_H1_0_S3_1	= "h1-0->s3-1";
	private static final String	NET_CONNECTION_H1_1_S5_1	= "h1-1->s5-1";
	private static final String	NET_CONNECTION_H1_2_S6_1	= "h1-2->s6-1";
	private static final String	NET_CONNECTION_S3_1_S3_2	= "s3-1->s3-2";
	private static final String	NET_CONNECTION_S3_2_S4_1	= "s3-2->s4-1";
	private static final String	NET_CONNECTION_S4_1_S4_2	= "s4-1->s4-2";
	private static final String	NET_CONNECTION_S4_2_h2_0	= "s4-2->h2-0";
	private static final String	NET_CONNECTION_S5_1_S5_2	= "s5-1->s5-2";
	private static final String	NET_CONNECTION_S5_2_H2_1	= "s5-2->h2-1";
	private static final String	NET_CONNECTION_S6_1_S6_2	= "s6-1->s6-2";
	private static final String	NET_CONNECTION_S6_2_S7_1	= "s6-2->s7-1";
	private static final String	NET_CONNECTION_S7_1_S7_2	= "s7-1->s7-2";
	private static final String	NET_CONNECTION_S7_2_S8_1	= "s7-2->s8-1";
	private static final String	NET_CONNECTION_S8_1_S8_2	= "s8-1->s8-2";
	private static final String	NET_CONNECTION_S8_2_H2_2	= "s8-2->h2-2";

	private static final String	PORT_ID_0					= "0";
	private static final String	PORT_ID_1					= "1";
	private static final String	PORT_ID_2					= "2";

	@Test
	public void PathLoadingTest() throws IOException, SerializationException {

		String xmlPaths = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));

		Map<String, Route> routes = PathLoader.getRoutesFromXml(xmlPaths);

		Assert.assertEquals("The XML contain 3 routes.", 3, routes.size());

		// Check routes
		Route firstRoute = routes.get(ROUTE_0_ID);
		Assert.assertNotNull("XML contain a route with id \"0\"", firstRoute);

		Route secondRoute = routes.get(ROUTE_1_ID);
		Assert.assertNotNull("XML contain a route with id \"1\"", secondRoute);

		Route thirdRoute = routes.get(ROUTE_2_ID);
		Assert.assertNotNull("XML contain a route with id \"2\"", thirdRoute);

		checkFirstRoute(firstRoute);
		checkSecondRoute(secondRoute);
		checkThirdRoute(thirdRoute);
	}

	private void checkFirstRoute(Route firstRoute) {

		List<NetworkConnection> firstRouteConnections = firstRoute.getNetworkConnections();

		Assert.assertEquals("First route should contain 5 network connections.", 5, firstRouteConnections.size());

		NetworkConnection netConnection = firstRouteConnections.get(0);
		Assert.assertEquals("First network connection from first route should be h1->s3", netConnection.getName(), NET_CONNECTION_H1_0_S3_1);
		Assert.assertEquals("Source of first network connection from first route should be device h1", netConnection.getSource().getDeviceId(),
				DEVICE_H1_ID);
		Assert.assertEquals("Source of first network connection from first route should be port 0", netConnection.getSource().getPortNumber(),
				PORT_ID_0);
		Assert.assertEquals("Destination of first network connection from first route should be device s3", netConnection.getDestination()
				.getDeviceId(), DEVICE_S3_ID);
		Assert.assertEquals("Destination of first network connection from first route should be port 0", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = firstRouteConnections.get(1);
		Assert.assertEquals("Second network connection from first route should be s3-1->s3-2", netConnection.getName(), NET_CONNECTION_S3_1_S3_2);
		Assert.assertEquals("Source of second network connection from first route should be device s3", netConnection.getSource().getDeviceId(),
				DEVICE_S3_ID);
		Assert.assertEquals("Source of second network connection from first route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of second network connection from first route should be device s3", netConnection.getDestination()
				.getDeviceId(), DEVICE_S3_ID);
		Assert.assertEquals("Destination of second network connection from first route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = firstRouteConnections.get(2);
		Assert.assertEquals("Third network connection from first route should be s3-2->s4-1", netConnection.getName(), NET_CONNECTION_S3_2_S4_1);
		Assert.assertEquals("Source of third network connection from first route should be device s3", netConnection.getSource().getDeviceId(),
				DEVICE_S3_ID);
		Assert.assertEquals("Source of third network connection from first route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of third network connection from first route should be device s4", netConnection.getDestination()
				.getDeviceId(), DEVICE_S4_ID);
		Assert.assertEquals("Destination of third network connection from first route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = firstRouteConnections.get(3);
		Assert.assertEquals("Fourth network connection from first route should be s4-1->s4-2", netConnection.getName(), NET_CONNECTION_S4_1_S4_2);
		Assert.assertEquals("Source of fourth network connection from first route should be device s4", netConnection.getSource().getDeviceId(),
				DEVICE_S4_ID);
		Assert.assertEquals("Source of fourth network connection from first route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of fourth network connection from first route should be device s4", netConnection.getDestination()
				.getDeviceId(), DEVICE_S4_ID);
		Assert.assertEquals("Destination of fourth network connection from first route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = firstRouteConnections.get(4);
		Assert.assertEquals("Fifth network connection from first route should be s4-2->h2-0", netConnection.getName(), NET_CONNECTION_S4_2_h2_0);
		Assert.assertEquals("Source of fifth network connection from first route should be device s4", netConnection.getSource().getDeviceId(),
				DEVICE_S4_ID);
		Assert.assertEquals("Source of fifth network connection from first route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of fifth network connection from first route should be device h2", netConnection.getDestination()
				.getDeviceId(), DEVICE_H2_ID);
		Assert.assertEquals("Destination of fifth network connection from first route should be port 0", netConnection.getDestination()
				.getPortNumber(), PORT_ID_0);

	}

	private void checkSecondRoute(Route secondRoute) {

		List<NetworkConnection> secondRouteConnections = secondRoute.getNetworkConnections();

		Assert.assertEquals("First route should contain 3 network connections.", 3, secondRouteConnections.size());

		NetworkConnection netConnection = secondRouteConnections.get(0);
		Assert.assertEquals("First network connection from second route should be h1->s5-1", netConnection.getName(), NET_CONNECTION_H1_1_S5_1);
		Assert.assertEquals("Source of first network connection from second route should be device h1", netConnection.getSource().getDeviceId(),
				DEVICE_H1_ID);
		Assert.assertEquals("Source of first network connection from second route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of first network connection from second route should be device s5", netConnection.getDestination()
				.getDeviceId(), DEVICE_S5_ID);
		Assert.assertEquals("Destination of first network connection from second route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = secondRouteConnections.get(1);
		Assert.assertEquals("Second network connection from second route should be s5-1->s5-2", netConnection.getName(), NET_CONNECTION_S5_1_S5_2);
		Assert.assertEquals("Source of second network connection from second route should be device s5", netConnection.getSource().getDeviceId(),
				DEVICE_S5_ID);
		Assert.assertEquals("Source of second network connection from second route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of second network connection from second route should be device s5", netConnection.getDestination()
				.getDeviceId(), DEVICE_S5_ID);
		Assert.assertEquals("Destination of second network connection from second route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = secondRouteConnections.get(2);
		Assert.assertEquals("Third network connection from first route should be s5-2->h2-1", netConnection.getName(), NET_CONNECTION_S5_2_H2_1);
		Assert.assertEquals("Source of third network connection from first route should be device s5", netConnection.getSource().getDeviceId(),
				DEVICE_S5_ID);
		Assert.assertEquals("Source of third network connection from first route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of third network connection from first route should be device h2", netConnection.getDestination()
				.getDeviceId(), DEVICE_H2_ID);
		Assert.assertEquals("Destination of third network connection from first route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

	}

	private void checkThirdRoute(Route thirdRoute) {
		List<NetworkConnection> thirdRouteConnections = thirdRoute.getNetworkConnections();

		Assert.assertEquals("Third route should contain 7 network connections.", 7, thirdRouteConnections.size());

		NetworkConnection netConnection = thirdRouteConnections.get(0);
		Assert.assertEquals("First network connection from third route should be h1-2->s6-1", netConnection.getName(), NET_CONNECTION_H1_2_S6_1);
		Assert.assertEquals("Source of first network connection from third route should be device h1", netConnection.getSource().getDeviceId(),
				DEVICE_H1_ID);
		Assert.assertEquals("Source of first network connection from third route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of first network connection from third route should be device s6", netConnection.getDestination()
				.getDeviceId(), DEVICE_S6_ID);
		Assert.assertEquals("Destination of first network connection from third route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = thirdRouteConnections.get(1);
		Assert.assertEquals("Second network connection from third route should be s6-1->s6-2", netConnection.getName(), NET_CONNECTION_S6_1_S6_2);
		Assert.assertEquals("Source of second network connection from third route should be device s6", netConnection.getSource().getDeviceId(),
				DEVICE_S6_ID);
		Assert.assertEquals("Source of second network connection from third route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of second network connection from third route should be device s6", netConnection.getDestination()
				.getDeviceId(), DEVICE_S6_ID);
		Assert.assertEquals("Destination of second network connection from third route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = thirdRouteConnections.get(2);
		Assert.assertEquals("Third network connection from third route should be s6-2->s7-1", netConnection.getName(), NET_CONNECTION_S6_2_S7_1);
		Assert.assertEquals("Source of third network connection from third route should be device s6", netConnection.getSource().getDeviceId(),
				DEVICE_S6_ID);
		Assert.assertEquals("Source of third network connection from third route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of third network connection from third route should be device s7", netConnection.getDestination()
				.getDeviceId(), DEVICE_S7_ID);
		Assert.assertEquals("Destination of third network connection from third route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = thirdRouteConnections.get(3);
		Assert.assertEquals("Fourth network connection from third route should be s7-1->s7-2", netConnection.getName(), NET_CONNECTION_S7_1_S7_2);
		Assert.assertEquals("Source of fourth network connection from third route should be device s7", netConnection.getSource().getDeviceId(),
				DEVICE_S7_ID);
		Assert.assertEquals("Source of fourth network connection from third route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of fourth network connection from third route should be device s7", netConnection.getDestination()
				.getDeviceId(), DEVICE_S7_ID);
		Assert.assertEquals("Destination of fourth network connection from third route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = thirdRouteConnections.get(4);
		Assert.assertEquals("Fifth network connection from third route should be s7-2->s8-1", netConnection.getName(), NET_CONNECTION_S7_2_S8_1);
		Assert.assertEquals("Source of fifth network connection from third route should be device s7", netConnection.getSource().getDeviceId(),
				DEVICE_S7_ID);
		Assert.assertEquals("Source of fifth network connection from third route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of fifth network connection from third route should be device s8", netConnection.getDestination()
				.getDeviceId(), DEVICE_S8_ID);
		Assert.assertEquals("Destination of fifth network connection from third route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_1);

		netConnection = thirdRouteConnections.get(5);
		Assert.assertEquals("Sixth network connection from third route should be s8-1->s8-2", netConnection.getName(), NET_CONNECTION_S8_1_S8_2);
		Assert.assertEquals("Source of sixth network connection from third route should be device s8", netConnection.getSource().getDeviceId(),
				DEVICE_S8_ID);
		Assert.assertEquals("Source of sixth network connection from third route should be port 1", netConnection.getSource().getPortNumber(),
				PORT_ID_1);
		Assert.assertEquals("Destination of sixth network connection from third route should be device s8", netConnection.getDestination()
				.getDeviceId(), DEVICE_S8_ID);
		Assert.assertEquals("Destination of sixth network connection from third route should be port 1", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

		netConnection = thirdRouteConnections.get(6);
		Assert.assertEquals("Last network connection from third route should be s8-2->h2-2", netConnection.getName(), NET_CONNECTION_S8_2_H2_2);
		Assert.assertEquals("Source of last network connection from third route should be device s8", netConnection.getSource().getDeviceId(),
				DEVICE_S8_ID);
		Assert.assertEquals("Source of last network connection from third route should be port 2", netConnection.getSource().getPortNumber(),
				PORT_ID_2);
		Assert.assertEquals("Destination of last network connection from third route should be device H2", netConnection.getDestination()
				.getDeviceId(), DEVICE_H2_ID);
		Assert.assertEquals("Destination of last network connection from third route should be port 2", netConnection.getDestination()
				.getPortNumber(), PORT_ID_2);

	}

}
