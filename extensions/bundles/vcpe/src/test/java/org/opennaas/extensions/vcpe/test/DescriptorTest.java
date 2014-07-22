package org.opennaas.extensions.vcpe.test;

/*
 * #%L
 * OpenNaaS :: vCPENetwork
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
