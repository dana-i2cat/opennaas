package org.opennaas.extensions.vcpe.test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.vcpe.manager.PhysicalInfrastructureLoader;
import org.opennaas.extensions.vcpe.manager.model.VCPEPhysicalInfrastructure;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class ModelTest {

	private static final String	PROPERTIES_PATH	= "/org.opennaas.extensions.vcpe.manager.phyinfrastructure.cfg";

	@Test
	public void marshallTest() throws JAXBException {

		VCPENetworkModel model = VCPENetworkModelHelper.generateSampleModel();

		StringWriter writer = (StringWriter) marshall(model, new StringWriter());
		VCPENetworkModel loaded = unmarshall(new StringReader(writer.toString()));

		Assert.assertEquals(model, loaded);
	}

	@Test
	public void marshallUsingToXmlTest() throws JAXBException, SerializationException {

		VCPENetworkModel model = VCPENetworkModelHelper.generateSampleModel();

		String xml = model.toXml();

		VCPENetworkModel loaded = (VCPENetworkModel) ObjectSerializer.fromXml(xml, VCPENetworkModel.class);
		String xml2 = loaded.toXml();
		// System.out.println(xml2);

		Assert.assertEquals(model, loaded);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void physicalInfrastrtuctureLoaderModelTest() throws IOException {

		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(PROPERTIES_PATH));

		PhysicalInfrastructureLoader loader = new PhysicalInfrastructureLoader();
		VCPEPhysicalInfrastructure phy = loader.loadPhysicalInfrastuctureFromProperties(props);

		Assert.assertNotNull(phy);

		Assert.assertNotNull(phy.getPhyRouterCore());
		Assert.assertNotNull(phy.getPhyRouterMaster());
		Assert.assertNotNull(phy.getPhyRouterBackup());
		Assert.assertNotNull(phy.getPhyBoD());
		Assert.assertNotNull(phy.getPhyLinks());
		Assert.assertFalse(phy.getPhyLinks().isEmpty());

		Assert.assertFalse(phy.getAllElements().isEmpty());
		Assert.assertTrue(phy.getAllElements()
				.contains(phy.getPhyRouterCore()));
		Assert.assertTrue(phy.getAllElements()
				.contains(phy.getPhyRouterMaster()));
		Assert.assertTrue(phy.getAllElements()
				.contains(phy.getPhyRouterBackup()));
		Assert.assertTrue(phy.getAllElements()
				.contains(phy.getPhyBoD()));
		Assert.assertTrue(phy.getAllElements()
				.containsAll(phy.getPhyLinks()));

	}

	private Writer marshall(VCPENetworkModel model, Writer writer) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(VCPENetworkModel.class); // using jaxb.index file
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(model, writer);
		return writer;
	}

	private VCPENetworkModel unmarshall(Reader reader) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(VCPENetworkModel.class); // using jaxb.index file
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (VCPENetworkModel) unmarshaller.unmarshal(reader);
	}

}
