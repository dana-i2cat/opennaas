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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRouteCollection;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class StaticRouteModelSerializationTest {

	private String	NET_ID_1		= "192.168.1.0/24";
	private String	NEXT_HOP_1		= "10.10.10.11";

	private String	NET_ID_2		= "192.168.2.0/24";

	private String	SR_XML_PATH		= "/staticRoute.xml";
	private String	SRC_XML_PATH	= "/staticRouteCollection.xml";

	@Test
	public void staticRouteSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		StaticRoute staticRoute = generateSampleRoute(NET_ID_1, NEXT_HOP_1);

		String xml = ObjectSerializer.toXml(staticRoute);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SR_XML_PATH));

		Assert.assertTrue("Serialized StaticRoute is not equals to the expected one.", XmlHelper.compareXMLStrings(expectedXml, xml));

	}

	@Test
	public void staticRouteDeserializationTest() throws IOException, SerializationException {

		StaticRoute expectedStaticRoute = generateSampleRoute(NET_ID_1, NEXT_HOP_1);

		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SR_XML_PATH));
		StaticRoute deserializedObject = (StaticRoute) ObjectSerializer.fromXml(xml, StaticRoute.class);

		Assert.assertEquals("Deserialized object is not equals to the expected static route.", expectedStaticRoute, deserializedObject);
	}

	@Test
	public void staticRouteCollectionSerializationTest() throws IOException, SerializationException, SAXException, TransformerException,
			ParserConfigurationException {

		StaticRouteCollection srCollection = generateSRCollection();

		String xml = ObjectSerializer.toXml(srCollection);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SRC_XML_PATH));

		Assert.assertTrue("Serialized StaticRouteCollection is not equals to the expected one.", XmlHelper.compareXMLStrings(expectedXml, xml));

	}

	@Test
	public void staticRouteCollectionDeserializationTest() throws IOException, SerializationException {

		StaticRouteCollection srCollection = generateSRCollection();
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(SRC_XML_PATH));

		StaticRouteCollection deserializedObject = (StaticRouteCollection) ObjectSerializer.fromXml(xml, StaticRouteCollection.class);

		Assert.assertEquals("Deserialized object is not equals to the expected static route collection.", srCollection, deserializedObject);

	}

	private StaticRouteCollection generateSRCollection() {
		Collection<StaticRoute> staticRoutes = new ArrayList<StaticRoute>();

		StaticRoute sr1 = generateSampleRoute(NET_ID_1, NEXT_HOP_1);
		StaticRoute sr2 = generateSampleRoute(NET_ID_2);
		staticRoutes.add(sr1);
		staticRoutes.add(sr2);

		StaticRouteCollection srCollection = new StaticRouteCollection();
		srCollection.setStaticRoutes(staticRoutes);

		return srCollection;
	}

	/**
	 * Sample Static route with only netId, which implies that the discard option gets activated.
	 * 
	 * @param netId
	 * @return
	 */
	private StaticRoute generateSampleRoute(String netId) {
		StaticRoute staticRoute = new StaticRoute();

		staticRoute.setDiscard(true);
		staticRoute.setNetIdIpAdress(netId);

		return staticRoute;

	}

	/**
	 * Sample Static route with netId and nextHopAddress, which implies that the discard option gets deactivated.
	 * 
	 * @param netId
	 * @return
	 */
	private StaticRoute generateSampleRoute(String netId, String nextHopId) {
		StaticRoute staticRoute = new StaticRoute();

		staticRoute.setDiscard(false);
		staticRoute.setNetIdIpAdress(netId);
		staticRoute.setNextHopIpAddress(nextHopId);

		return staticRoute;
	}
}
