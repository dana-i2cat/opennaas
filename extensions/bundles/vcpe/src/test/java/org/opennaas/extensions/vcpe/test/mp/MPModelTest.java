package org.opennaas.extensions.vcpe.test.mp;

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
