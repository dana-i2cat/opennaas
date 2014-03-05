package org.opennaas.extensions.genericnetwork.test.capability.pathfinding;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionInput;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionLogic;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionMap;
import org.opennaas.extensions.genericnetwork.driver.internal.actionsets.actions.pathfinding.model.RouteSelectionMapLoader;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class PathFinderComponentsTest {

	private final static String	MAPPING_URL				= "src/test/resources/routeMapping.xml";
	private final static String	MAPPING_WITH_PORTS_URL	= "src/test/resources/routeMappingWithPorts.xml";

	private final static String	SRC_IP					= "192.168.10.10";
	private final static String	DST_IP					= "192.168.10.11";
	private final static String	TOS						= "4";
	private final static String	SRC_PORT				= "portA";
	private final static String	DST_PORT				= "portC";

	private final static String	UNVALID_SRC_IP			= "10.10.10.11";

	@Test
	public void RouteSelectionMapLoaderTest() throws FileNotFoundException, SerializationException {

		RouteSelectionMap map = RouteSelectionMapLoader.getRouteSelectionMapFromXmlFile(MAPPING_URL);

		Assert.assertNotNull("RouteSelectionMap should not be null.", map);
		Assert.assertEquals("RouteSelectionMap should contain 4 mappings. ", 4, map.getRouteMapping().keySet().size());
	}

	@Test
	public void RouteSelectionLogicTest() throws SerializationException, IOException {

		RouteSelectionLogic selectionLogic = new RouteSelectionLogic();
		RouteSelectionInput input = new RouteSelectionInput(SRC_IP, DST_IP, TOS);

		selectionLogic.setMappingUrl(MAPPING_URL);
		List<String> routes = selectionLogic.getPotentialRoutes(input);

		Assert.assertNotNull("Potential routes should not be null.", routes);
		Assert.assertEquals("Potential routes should consist only of one route.", 1, routes.size());
		Assert.assertEquals("Potential routes should consist of route 3.", "3", routes.get(0));

	}

	@Test
	public void RouteSelectionLogicWithUnvalidTestTest() throws SerializationException, IOException {

		RouteSelectionLogic selectionLogic = new RouteSelectionLogic();
		RouteSelectionInput input = new RouteSelectionInput(UNVALID_SRC_IP, DST_IP, TOS);

		selectionLogic.setMappingUrl(MAPPING_URL);
		List<String> routes = selectionLogic.getPotentialRoutes(input);

		Assert.assertNotNull("Potential routes should not be null.", routes);
		Assert.assertTrue("Potential routes should not contain any route.", routes.isEmpty());

	}

	@Test(expected = IOException.class)
	public void RouteSelectionLogicWithoutFileTest() throws SerializationException, IOException {

		RouteSelectionLogic selectionLogic = new RouteSelectionLogic();
		RouteSelectionInput input = new RouteSelectionInput(UNVALID_SRC_IP, DST_IP, TOS);

		List<String> routes = selectionLogic.getPotentialRoutes(input);

	}

	@Test
	public void RouteSelectionLogicWithLinkPorts() throws SerializationException, IOException {
		RouteSelectionLogic selectionLogic = new RouteSelectionLogic();
		RouteSelectionInput input = new RouteSelectionInput(SRC_IP, DST_IP, TOS, SRC_PORT, DST_PORT);

		selectionLogic.setMappingUrl(MAPPING_WITH_PORTS_URL);
		List<String> routes = selectionLogic.getPotentialRoutes(input);

		Assert.assertNotNull("Potential routes should not be null.", routes);
		Assert.assertEquals("Potential routes should consist only of one route.", 1, routes.size());
		Assert.assertEquals("Potential routes should consist of route 3.", "3", routes.get(0));
	}

	@Test
	public void RouteSelectionLogicWithLinkPortsOnlyInFile() throws SerializationException, IOException {
		RouteSelectionLogic selectionLogic = new RouteSelectionLogic();
		RouteSelectionInput input = new RouteSelectionInput(SRC_IP, DST_IP, TOS);

		selectionLogic.setMappingUrl(MAPPING_WITH_PORTS_URL);
		List<String> routes = selectionLogic.getPotentialRoutes(input);

		Assert.assertNotNull("Potential routes should not be null.", routes);
		Assert.assertEquals("Potential routes should consist only of one route.", 1, routes.size());
		Assert.assertEquals("Potential routes should consist of route 3.", "3", routes.get(0));
	}

}
