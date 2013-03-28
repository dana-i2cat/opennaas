package org.opennaas.extensions.vcpe.test.mp;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.vcpe.manager.templates.mp.MPTemplateDefaultValuesLoader;
import org.opennaas.extensions.vcpe.manager.templates.mp.MPTemplateModelBuilder;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

public class MPModelTest {

	private static final String	SUGGESTOR_CONFIG_PATH	= "/org.opennaas.extensions.vcpe.manager.templates.mp.suggestor.defaults.cfg";

	@Test
	public void marshalUnmarsalTest() throws JAXBException, SerializationException {

		VCPENetworkModel model = MPTemplateModelBuilder.generateModel();

		String xml = model.toXml();

		VCPENetworkModel loaded = (VCPENetworkModel) ObjectSerializer.fromXml(xml, VCPENetworkModel.class);
		String xml2 = loaded.toXml();

		Assert.assertEquals(model, loaded);
		Assert.assertEquals(xml, xml2);
	}

	@Test
	public void physicalSuggestionDefaults() throws IOException {

		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(SUGGESTOR_CONFIG_PATH));

		MPTemplateDefaultValuesLoader loader = new MPTemplateDefaultValuesLoader();
		loader.initialize(props);

		VCPENetworkModel suggestion = loader.loadDefaultPhysicalModel();

	}

	@Test
	public void logicalSuggestionDefaults() throws IOException {

		Properties props = new Properties();
		props.load(this.getClass().getResourceAsStream(SUGGESTOR_CONFIG_PATH));

		MPTemplateDefaultValuesLoader loader = new MPTemplateDefaultValuesLoader();
		loader.initialize(props);

		VCPENetworkModel suggestion = loader.loadDefaultLogicalModel();

	}

}
