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
import org.opennaas.core.resources.descriptor.vcpe.VCPENetworkDescriptor;
import org.opennaas.core.resources.descriptor.vcpe.helper.VCPENetworkDescriptorHelper;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class DescriptorTest {

	@Test
	public void marshallDescriptorTest() throws JAXBException, SerializationException {

		StringWriter writer = new StringWriter();
		VCPENetworkModel model = VCPENetworkModelHelper.generateSampleModel();
		VCPENetworkDescriptor descriptor = VCPENetworkDescriptorHelper.generateSampleDescriptor(
				"vcpeNet1", model.toXml());

		marshallVCPENetDescriptor(writer, descriptor);

		VCPENetworkDescriptor loaded = unmarshallVCPENetDescriptor(new StringReader(writer.toString()));

		Assert.assertEquals(loaded.getInformation(), descriptor.getInformation());
		Assert.assertEquals(loaded.getvCPEModel(), descriptor.getvCPEModel());

		VCPENetworkModel loadedModel = (VCPENetworkModel) ObjectSerializer.fromXml(loaded.getvCPEModel(), VCPENetworkModel.class);

		Assert.assertEquals(model, loadedModel);

		System.out.println(writer.toString());
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
