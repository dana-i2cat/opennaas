package org.opennaas.extensions.vcpe.test.mp;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.vcpe.manager.templates.mp.MPTemplateModelBuilder;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class MPModelTest {

	@Test
	public void marshalUnmarsalTest() throws JAXBException, SerializationException {

		VCPENetworkModel model = MPTemplateModelBuilder.generateModel();

		String xml = model.toXml();

		VCPENetworkModel loaded = (VCPENetworkModel) ObjectSerializer.fromXml(xml, VCPENetworkModel.class);
		String xml2 = loaded.toXml();

		Assert.assertEquals(model, loaded);
		Assert.assertEquals(xml, xml2);
	}

}
