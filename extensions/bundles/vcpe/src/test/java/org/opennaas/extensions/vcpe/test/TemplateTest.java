package org.opennaas.extensions.vcpe.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.extensions.vcpe.manager.templates.vcpe.Template;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.VCPETemplate;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;

public class TemplateTest {

	Template	template;

	@Before
	public void initTemplate() {
		template = new Template();
	}

	@Test
	public void testPhysicalSuggestionHasAllPhyElements() {

		VCPENetworkModel phy = template.getPhysicalInfrastructureSuggestion();

		Assert.assertEquals(3, VCPENetworkModelHelper.getRouters(phy.getElements()).size());
		Assert.assertEquals(1, VCPENetworkModelHelper.getDomains(phy.getElements()).size());
		Assert.assertEquals(17, VCPENetworkModelHelper.getInterfaces(phy.getElements()).size());
		Assert.assertEquals(0, VCPENetworkModelHelper.getLinks(phy.getElements()).size());

		checkModelHasAllPhysicalElements(phy);

	}

	@Test
	public void testAllPhysicalSuggestionElementsHaveName() {

		VCPENetworkModel phy = template.getPhysicalInfrastructureSuggestion();

		for (VCPENetworkElement elem : phy.getElements()) {
			Assert.assertNotNull(elem.getName());
		}

		for (Interface iface : VCPENetworkModelHelper.getInterfaces(phy.getElements())) {
			Assert.assertNotNull(iface.getPhysicalInterfaceName());
			Assert.assertEquals(iface.getPhysicalInterfaceName(), iface.getName());
		}
	}

	@Test
	public void testLogicalSuggestionHasAllElements() {

		VCPENetworkModel phy = template.getPhysicalInfrastructureSuggestion();
		VCPENetworkModel suggestion = template.getLogicalInfrastructureSuggestion(phy);

		checkModelHasAllPhysicalElements(suggestion);
		checkModelHasAllLogicalElements(suggestion);
	}

	@Test
	public void testBuildModelFromSuggestionHasAllElements() {

		VCPENetworkModel phy = template.getPhysicalInfrastructureSuggestion();
		VCPENetworkModel suggestion = template.getLogicalInfrastructureSuggestion(phy);
		VCPENetworkModel build = template.buildModel(suggestion);

		checkModelHasAllPhysicalElements(build);
		checkModelHasAllLogicalElements(build);

	}

	// TODO add a test for names of logical suggestion

	private void checkModelHasAllPhysicalElements(VCPENetworkModel model) {

		Router router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CORE_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 3);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.CORE_PHY_LO_INTERFACE));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.CORE_PHY_INTERFACE_MASTER));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.CORE_PHY_INTERFACE_BKP));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE1_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 4);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.INTER1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.DOWN1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.UP1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.LO1_PHY_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CPE2_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 4);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.INTER2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.DOWN2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.UP2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.LO2_PHY_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		Domain domain = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);
		Assert.assertNotNull(domain);
		Assert.assertTrue(domain.getInterfaces().size() >= 6);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.INTER1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.DOWN1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.INTER2_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.DOWN2_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.CLIENT1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(domain.getInterfaces()));

	}

	private void checkModelHasAllLogicalElements(VCPENetworkModel model) {

		LogicalRouter router = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE1_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.INTER1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.DOWN1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.UP1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.LO1_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.VCPE2_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.INTER2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.DOWN2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.UP2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), VCPETemplate.LO2_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		Domain domain = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.AUTOBAHN);
		Assert.assertNotNull(domain);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.INTER1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.DOWN1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.INTER2_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.DOWN2_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.CLIENT1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), VCPETemplate.CLIENT2_INTERFACE_AUTOBAHN));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(domain.getInterfaces()));

		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, VCPETemplate.CORE_PHY_ROUTER);
		Assert.assertNotNull(core);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), VCPETemplate.CORE_LO_INTERFACE));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), VCPETemplate.UP1_INTERFACE_PEER));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), VCPETemplate.UP2_INTERFACE_PEER));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(core.getInterfaces()));

		// TODO test all links are in place

		// TODO test VRRP and BGP are set

	}

}
