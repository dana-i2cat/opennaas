package org.opennaas.extensions.router.capability.ip;

/*
 * #%L
 * OpenNaaS :: Router :: IP Capability
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capability.ip.api.IPAddresses;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class IPSerializationTest {

	private static final String	PATH_FILE_URL	= "/ipAddressesSample.xml";

	private IPAddresses			ipAddresses;

	@Before
	public void initIPAddresses() {
		ipAddresses = generateSampleIPAddress();
	}

	@Test
	public void IPAddressSerializationDeserializationTest() throws SerializationException {
		genericSerializationDeserializationCompatilibiliyTest(ipAddresses);
	}

	@Test
	public void IPAddressSerializationTest() throws Exception {
		String xml = ObjectSerializer.toXml(ipAddresses);
		String expectedXml = IOUtils.toString(this.getClass().getResourceAsStream(PATH_FILE_URL));
		Assert.assertTrue(XmlHelper.compareXMLStrings(xml, expectedXml));
	}

	private void genericSerializationDeserializationCompatilibiliyTest(Object original) throws SerializationException {
		String xml = ObjectSerializer.toXml(original);
		Object generated = ObjectSerializer.fromXml(xml, original.getClass());
		String xml2 = ObjectSerializer.toXml(generated);
		Assert.assertEquals(original, generated);
		Assert.assertEquals(xml, xml2);
	}

	private IPAddresses generateSampleIPAddress() {

		List<String> addresses = new ArrayList<String>(10);

		String ip_prefix = "192.168.1.";
		String mask = "255.255.255.0";
		for (int i = 100; i < 110; i++) {
			addresses.add(ip_prefix + String.valueOf(i) + "/" + mask);
		}

		IPAddresses ipAddresses = new IPAddresses();
		ipAddresses.setIpAddresses(addresses);
		return ipAddresses;
	}

}
