package org.opennaas.extensions.vcpe.test;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class NetworkModelHelperTest {

	VCPENetworkModel	vcpeModel;
	Router				router;

	static final String	routerName	= "vCPE1";

	public void setUp() throws IOException {

		vcpeModel = VCPENetworkModelHelper.generateSampleModel();
		router = VCPENetworkModelHelper.getRouterByName(vcpeModel.getElements(), routerName);
	}

	/**
	 * Test creates a sample model, remove one of the routers, and checks its interfaces and links has been removed from model.
	 * 
	 * @throws IOException
	 */
	@Test
	public void removeAllRouterInformationFromModelTest() throws IOException {

		setUp();

		// checking pre-state

		List<VCPENetworkElement> vcpeElements = vcpeModel.getElements();
		Assert.assertEquals(4, VCPENetworkModelHelper.getRouters(vcpeElements).size());
		Router router = VCPENetworkModelHelper.getRouterByName(vcpeElements, routerName);

		int modelInterfaces = VCPENetworkModelHelper.getInterfaces(vcpeElements).size();
		List<Interface> routerInterfaces = router.getInterfaces();
		Assert.assertEquals(3, routerInterfaces.size());

		int modelLinks = VCPENetworkModelHelper.getLinks(vcpeElements).size();
		List<Link> routerLinks = VCPENetworkModelHelper.getAllRouterLinksFromModel(vcpeModel, router);
		Assert.assertEquals(5, routerLinks.size());

		VCPENetworkModelHelper.removeAllRouterInformationFromModel(vcpeModel, routerName);

		// checking post-state
		vcpeElements = vcpeModel.getElements();

		Assert.assertEquals(3, VCPENetworkModelHelper.getRouters(vcpeElements).size());

		Assert.assertEquals(modelLinks - 5, VCPENetworkModelHelper.getLinks(vcpeElements).size());
		for (Interface iface : routerInterfaces)
			Assert.assertFalse(vcpeElements.contains(iface));

		Assert.assertEquals(modelInterfaces - 3, VCPENetworkModelHelper.getInterfaces(vcpeElements).size());
		for (Link link : routerLinks)
			Assert.assertFalse(vcpeElements.contains(link));

	}
}
