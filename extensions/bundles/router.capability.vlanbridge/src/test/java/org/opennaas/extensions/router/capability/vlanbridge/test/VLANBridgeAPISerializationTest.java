package org.opennaas.extensions.router.capability.vlanbridge.test;

/*
 * #%L
 * OpenNaaS :: Router :: VLAN bridge Capability
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

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.router.capability.vlanbridge.api.model.BridgeDomain;
import org.opennaas.extensions.router.capability.vlanbridge.api.model.BridgeDomains;
import org.opennaas.extensions.router.capability.vlanbridge.api.model.InterfaceVLANOptions;

public class VLANBridgeAPISerializationTest {

	BridgeDomains			domains;

	BridgeDomain			domain;

	InterfaceVLANOptions	vlanOptions;

	@Before
	public void generateSamples() {

		List<String> domainNames = Arrays.asList("office", "development", "depA", "depB");
		domains = new BridgeDomains();
		domains.setDomainNames(domainNames);

		domain = new BridgeDomain();
		domain.setDomainName("office");
		domain.setVlanid(100);
		domain.setDescription("Office shared network");
		domain.setInterfacesNames(Arrays.asList("ge-0/0/0.0", "ge-0/0/1.0"));

		vlanOptions = new InterfaceVLANOptions();
		vlanOptions.getVlanOptions().put("port-mode", "trunk");
		vlanOptions.getVlanOptions().put("native-vlan-id", "10");
	}

	@Test
	public void bridgeDomainsSerializationTest() throws SerializationException {

		String xml = ObjectSerializer.toXml(domains);
		BridgeDomains generated = (BridgeDomains) ObjectSerializer.fromXml(xml, BridgeDomains.class);
		Assert.assertEquals(domains, generated);
		System.out.println(xml);
	}

	@Test
	public void bridgeDomainSerializationTest() throws SerializationException {

		String xml = ObjectSerializer.toXml(domain);
		BridgeDomain generated = (BridgeDomain) ObjectSerializer.fromXml(xml, BridgeDomain.class);
		Assert.assertEquals(domain, generated);
		System.out.println(xml);

	}

	@Test
	public void interfaceVLANOptionsSerializationTest() throws SerializationException {

		String xml = ObjectSerializer.toXml(vlanOptions);
		InterfaceVLANOptions generated = (InterfaceVLANOptions) ObjectSerializer.fromXml(xml, InterfaceVLANOptions.class);
		Assert.assertEquals(vlanOptions, generated);
		System.out.println(xml);
	}

}
