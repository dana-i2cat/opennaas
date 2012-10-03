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
import org.opennaas.extensions.vcpe.descriptor.VCPENetworkDescriptor;
import org.opennaas.extensions.vcpe.descriptor.helper.VCPENetworkDescriptorHelper;

public class DescriptorTest {

	@Test
	public void marshallDescriptorTest() throws JAXBException {

		StringWriter writer = new StringWriter();
		VCPENetworkDescriptor descriptor = VCPENetworkDescriptorHelper.generateSampleDescriptor(
				"vcpeNet1", VCPENetworkDescriptorHelper.generateSampleRequest());

		marshallVCPENetDescriptor(writer, descriptor);

		VCPENetworkDescriptor loaded = unmarshallVCPENetDescriptor(new StringReader(writer.toString()));

		Assert.assertEquals(loaded.getInformation(), descriptor.getInformation());
		Assert.assertEquals(loaded.getRequest(), descriptor.getRequest());
	}

	private static Writer marshallVCPENetDescriptor(Writer writer, VCPENetworkDescriptor descriptor) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(VCPENetworkDescriptor.class);

		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(descriptor, writer);

		return writer;
	}

	private static VCPENetworkDescriptor unmarshallVCPENetDescriptor(Reader reader) throws JAXBException {

		JAXBContext context = JAXBContext.newInstance(VCPENetworkDescriptor.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (VCPENetworkDescriptor) unmarshaller.unmarshal(reader);
	}

}
