package org.opennaas.extensions.router.capability.ospf.api.model.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFAreaWrapper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFProtocolEndpointWrapper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFServiceWrapper;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.xml.sax.SAXException;

public class SerializationTest {

	private static final String	PATH_FILE_URL	= "/ospfService.xml";

	@Test
	public void OSPFServiceSerializationTest() throws SerializationException, IOException, SAXException, TransformerException,
			ParserConfigurationException {

		OSPFProtocolEndpointWrapper pE1 = generateProtocolEndpointWrapper("fe-0/3/3.1", EnabledState.ENABLED);
		OSPFProtocolEndpointWrapper pE2 = generateProtocolEndpointWrapper("fe-0/3/3.2", EnabledState.DISABLED);
		Collection<OSPFProtocolEndpointWrapper> endpoints = new ArrayList<OSPFProtocolEndpointWrapper>();
		endpoints.add(pE1);
		endpoints.add(pE2);

		OSPFAreaWrapper area = generateAreaWrapper("0.0.0.0", endpoints);
		Collection<OSPFAreaWrapper> ospfAreas = new ArrayList<OSPFAreaWrapper>();
		ospfAreas.add(area);

		OSPFServiceWrapper ospfService = new OSPFServiceWrapper();

		ospfService.setOspfArea(ospfAreas);

		String xml = ObjectSerializer.toXml(ospfService);

		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));

		Assert.assertTrue(XmlHelper.compareXMLStrings(xml, expectedXml));

	}

	@Test
	public void OSPFServiceDeserializationTest() throws IOException, SerializationException {
		String xml = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));
		OSPFServiceWrapper serviceWrapper = (OSPFServiceWrapper) ObjectSerializer.fromXml(xml, OSPFServiceWrapper.class);

		Assert.assertNotNull(serviceWrapper);
		Collection<OSPFAreaWrapper> ospfAreas = serviceWrapper.getOspfAreas();
		Assert.assertNotNull(ospfAreas);
		Assert.assertEquals(1, ospfAreas.size());

		OSPFAreaWrapper ospfArea = ospfAreas.iterator().next();
		Assert.assertEquals("0.0.0.0", ospfArea.getName());
		Assert.assertNotNull(ospfArea.getOspfProtocolEndpoints());

		Collection<OSPFProtocolEndpointWrapper> protocolEndpoints = ospfArea.getOspfProtocolEndpoints();
		Assert.assertEquals(2, protocolEndpoints.size());

		Iterator<OSPFProtocolEndpointWrapper> iterator = protocolEndpoints.iterator();
		OSPFProtocolEndpointWrapper pE = iterator.next();

		OSPFProtocolEndpointWrapper pE2 = iterator.next();
		Assert.assertTrue((pE.getName().equals("fe-0/3/3.1") && pE.getEnabledState().equals(EnabledState.ENABLED) || (pE2.getName().equals(
				"fe-0/3/3.2") && pE2.getName().equals(EnabledState.DISABLED))));
	}

	private OSPFAreaWrapper generateAreaWrapper(String name, Collection<OSPFProtocolEndpointWrapper> endpoints) {

		OSPFAreaWrapper area = new OSPFAreaWrapper();
		area.setName(name);
		area.setOspfProtocolEndpoints(endpoints);

		return area;
	}

	private OSPFProtocolEndpointWrapper generateProtocolEndpointWrapper(String name, EnabledState enabled) {

		OSPFProtocolEndpointWrapper pE = new OSPFProtocolEndpointWrapper();

		pE.setName(name);
		pE.setState(enabled);

		return pE;

	}

}
