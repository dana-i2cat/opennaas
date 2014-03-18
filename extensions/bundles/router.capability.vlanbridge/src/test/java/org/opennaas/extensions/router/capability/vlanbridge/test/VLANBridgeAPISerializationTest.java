package org.opennaas.extensions.router.capability.vlanbridge.test;

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
