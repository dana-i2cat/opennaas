package org.opennaas.extensions.router.capabilities.api.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.BridgeDomainApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.vlanbridge.BridgeDomains;

public class VLANBridgeApiHelperTest {

	private final static String	BD_NAME_100	= "vlan.100";
	private final static String	BD_NAME_200	= "vlan.200";

	@Test
	public void buildApiBridgeDomainsTest() {

		List<org.opennaas.extensions.router.model.BridgeDomain> modelBridgeDomains = generateSampleModelBridgeDomains();

		BridgeDomains apiBridgeDomains = BridgeDomainApiHelper.buildApiBridgeDomains(modelBridgeDomains);

		Assert.assertNotNull("Parsed Bridge Domains should not be null", apiBridgeDomains);
		Assert.assertNotNull("Parsed Bridge Domains should not be null", apiBridgeDomains.getDomainNames());
		Assert.assertEquals("BridgeDomains list should contain two elements.", 2, apiBridgeDomains.getDomainNames().size());

		Assert.assertTrue("BridgeDomains list should contain domain name " + BD_NAME_100, apiBridgeDomains.getDomainNames().contains(BD_NAME_100));
		Assert.assertTrue("BridgeDomains list should contain domain name " + BD_NAME_200, apiBridgeDomains.getDomainNames().contains(BD_NAME_200));

	}

	private List<org.opennaas.extensions.router.model.BridgeDomain> generateSampleModelBridgeDomains() {

		List<org.opennaas.extensions.router.model.BridgeDomain> modelBridgeDomains = new ArrayList<org.opennaas.extensions.router.model.BridgeDomain>();

		modelBridgeDomains.add(generateSampleModelBridgeDomain(BD_NAME_100));
		modelBridgeDomains.add(generateSampleModelBridgeDomain(BD_NAME_200));

		return modelBridgeDomains;
	}

	private org.opennaas.extensions.router.model.BridgeDomain generateSampleModelBridgeDomain(String domainName) {

		org.opennaas.extensions.router.model.BridgeDomain modelBrDomain = new org.opennaas.extensions.router.model.BridgeDomain();
		modelBrDomain.setElementName(domainName);

		return modelBrDomain;
	}
}
