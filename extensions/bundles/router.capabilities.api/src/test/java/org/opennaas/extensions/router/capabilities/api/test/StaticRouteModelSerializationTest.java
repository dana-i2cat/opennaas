package org.opennaas.extensions.router.capabilities.api.test;

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
	private boolean	DISCARD_1		= false;

	private String	NET_ID_2		= "192.168.2.0/24";
	private String	NEXT_HOP_2		= "10.10.10.12";
	private boolean	DISCARD_2		= true;

	private String	SR_XML_PATH		= "/staticRoute.xml";
	private String	SRC_XML_PATH	= "/staticRouteCollection.xml";

	@Test
	public void staticRouteSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		StaticRoute staticRoute = generateSampleRoute(NET_ID_1, NEXT_HOP_1, DISCARD_1);

		String xml = ObjectSerializer.toXml(staticRoute);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(SR_XML_PATH));

		Assert.assertTrue("Serialized StaticRoute is not equals to the expected one.", XmlHelper.compareXMLStrings(expectedXml, xml));

	}

	@Test
	public void staticRouteDeserializationTest() throws IOException, SerializationException {

		StaticRoute expectedStaticRoute = generateSampleRoute(NET_ID_1, NEXT_HOP_1, DISCARD_1);

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

		StaticRoute sr1 = generateSampleRoute(NET_ID_1, NEXT_HOP_1, DISCARD_1);
		StaticRoute sr2 = generateSampleRoute(NET_ID_2, NEXT_HOP_2, DISCARD_2);
		staticRoutes.add(sr1);
		staticRoutes.add(sr2);

		StaticRouteCollection srCollection = new StaticRouteCollection();
		srCollection.setStaticRoutes(staticRoutes);

		return srCollection;
	}

	private StaticRoute generateSampleRoute(String netId, String nextHopId, boolean discard) {
		StaticRoute staticRoute = new StaticRoute();

		staticRoute.setDiscard(discard);
		staticRoute.setNetIdIpAdress(netId);
		staticRoute.setNextHopIpAddress(nextHopId);

		return staticRoute;
	}
}
