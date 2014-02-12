package org.opennaas.extensions.router.capabilities.api.test;

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

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.router.capabilities.api.helper.IPApi2ModelTranslator;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
public class IPApi2ModelTranslatorTest {

	private IPProtocolEndpoint	ipv4PEP;

	@Before
	public void initIPAddresses() {
		ipv4PEP = generateSampleIPv4PEP();
	}

	@Test
	public void ipv4TranslationTest() throws SerializationException {
		List<String> addresses = IPApi2ModelTranslator.ipPEP2IPAddresses(ipv4PEP);
		Assert.assertNotNull(addresses);
		Assert.assertFalse(addresses.isEmpty());
		Assert.assertEquals(1, addresses.size());

		String firstAddress = addresses.get(0);
		String ip = firstAddress.split("/")[0];
		String mask = firstAddress.split("/")[1];

		Assert.assertEquals(ipv4PEP.getIPv4Address(), ip);
		Assert.assertEquals(IPUtilsHelper.parseLongToShortIpv4NetMask(ipv4PEP.getSubnetMask()), mask);
	}

	private IPProtocolEndpoint generateSampleIPv4PEP() {

		String ip = "192.168.1.110";
		String mask = "255.255.255.0";

		IPProtocolEndpoint pep = new IPProtocolEndpoint();
		pep.setProtocolIFType(ProtocolIFType.IPV4);
		pep.setIPv4Address(ip);
		pep.setSubnetMask(mask);

		return pep;
	}

}
