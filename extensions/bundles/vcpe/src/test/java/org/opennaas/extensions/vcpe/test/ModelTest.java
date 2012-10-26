package org.opennaas.extensions.vcpe.test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class ModelTest {

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
