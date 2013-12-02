package org.opennaas.extensions.vcpe.test;

import java.io.IOException;
import java.util.ArrayList;
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

	/**
	 * Changed to be checked in router1:
	 * 
	 * - interface fe-0/3/2.1 renamed to fe-0/3/2.2 - interface ge-0/2/0.1 not updated - interface lt-0/1/2.1 changed ip address
	 * 
	 * @throws IOException
	 */
	@Test
	public void updateRouterInformationTest() throws IOException {

		setUp();

		// CHECKING PRE-STATE
		Assert.assertTrue(vcpeModel.getElements().contains(router));

		List<Interface> oldModelIfaces = VCPENetworkModelHelper.getInterfaces(vcpeModel.getElements());
		Assert.assertEquals(26, oldModelIfaces.size());

		List<Interface> routerIfaces = router.getInterfaces();
		Assert.assertEquals(3, routerIfaces.size());

		List<Link> routerLinks = VCPENetworkModelHelper.getAllRouterLinksFromModel(vcpeModel, router);
		Assert.assertEquals(5, routerLinks.size());

		List<Link> oldModelLinks = VCPENetworkModelHelper.getLinks(vcpeModel.getElements());

		Interface oldIface1 = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "fe-0/3/2.1");
		Assert.assertNotNull(oldIface1);

		Interface oldIface2 = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "ge-0/2/0.1");
		Assert.assertNotNull(oldIface2);
		String oldIface2IP = oldIface2.getIpAddress();

		Interface oldIface3 = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "lt-0/1/2.1");
		Assert.assertNotNull(oldIface3);
		String oldIface3IP = oldIface3.getIpAddress();

		Router editedRouter = VCPENetworkModelHelper.generateSampleRouter();
		List<Link> newLinks = prepareNewLinks(editedRouter);

		// CHECKING POST-STATE

		VCPENetworkModelHelper.updateRouterInformation(vcpeModel, editedRouter, newLinks);

		// router in model
		router = VCPENetworkModelHelper.getRouterByName(vcpeModel.getElements(), editedRouter.getName());
		// Assert.assertEquals(routerName, router.getName());

		// number of interfaces in Model
		List<Interface> newModelIfaces = VCPENetworkModelHelper.getInterfaces(vcpeModel.getElements());
		Assert.assertEquals(oldModelIfaces.size(), newModelIfaces.size());

		// router ifaces
		List<Interface> ifaces = router.getInterfaces();
		Assert.assertEquals(3, ifaces.size());

		Assert.assertFalse(vcpeModel.getElements().contains(oldIface1));
		Assert.assertFalse(router.getInterfaces().contains(oldIface1));
		Assert.assertNull(VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "fe-0/3/2.1"));

		Assert.assertTrue(vcpeModel.getElements().contains(oldIface2));
		Assert.assertTrue(router.getInterfaces().contains(oldIface2));
		Assert.assertEquals(oldIface2IP, oldIface2.getIpAddress());

		Assert.assertFalse(vcpeModel.getElements().contains(oldIface3));
		Assert.assertFalse(router.getInterfaces().contains(oldIface3));
		Interface newIface3 = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "lt-0/1/2.1");
		Assert.assertNotNull(newIface3);
		Assert.assertEquals("192.168.0.9/30", newIface3.getIpAddress());
		Assert.assertFalse(oldIface3IP.equals(newIface3.getIpAddress()));

		// router links
		List<Link> newRouterLinks = VCPENetworkModelHelper.getAllRouterLinksFromModel(vcpeModel, router);
		Assert.assertEquals(newLinks.size(), newRouterLinks.size());
		Assert.assertTrue(newRouterLinks.containsAll(newLinks));

		List<Link> newModelLinks = VCPENetworkModelHelper.getLinks(vcpeModel.getElements());
		Assert.assertEquals(oldModelLinks.size(), newModelLinks.size());
		Assert.assertFalse(oldModelLinks.equals(newModelLinks));

	}

	private List<Link> prepareNewLinks(Router editedRouter) {

		List<Link> links = new ArrayList<Link>();

		Interface iface1 = editedRouter.getInterfaces().get(0);
		Interface iface1other = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "lt-0/1/2.3");
		Link link1 = new Link();
		link1.setSource(iface1);
		link1.setSink(iface1other);

		Interface iface2 = editedRouter.getInterfaces().get(1);
		Interface iface2other = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "autobahnID:000001.2");
		Link link2 = new Link();
		link2.setSource(iface2);
		link2.setSink(iface2other);

		Interface iface3 = editedRouter.getInterfaces().get(2);
		Interface iface3other = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "lt-0/1/2.3");
		Link link3 = new Link();
		link3.setSource(iface3);
		link3.setSink(iface3other);

		Interface inter2 = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "fe-0/3/3.1");
		Link link4 = new Link();
		link4.setSource(iface1);
		link4.setSink(inter2);

		Interface clientOther = (Interface) VCPENetworkModelHelper.getElementByName(vcpeModel.getElements(), "autobahnID:000003.1");
		Link link5 = new Link();
		link5.setSource(iface2);
		link5.setSink(clientOther);

		links.add(link1);
		links.add(link2);
		links.add(link3);
		links.add(link4);
		links.add(link5);

		return links;
	}
}
