package org.opennaas.itests.vcpe;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.extensions.vcpe.manager.templates.sp.SingleProviderTemplate;
import org.opennaas.extensions.vcpe.manager.templates.sp.SPTemplateConstants;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.extensions.vcpe.model.helper.VCPENetworkModelHelper;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class SPVCPETemplateTest {

	@Inject
	private IResourceManager	rm;

	@Inject
	@Filter("(type=vcpenet)")
	private IResourceRepository	resourceRepo;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.vcpe)")
	private BlueprintContainer	vcpeBundleBlueprintContainer;

	SingleProviderTemplate		template;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-vcpe"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void initTemplate() {
		template = new SingleProviderTemplate();
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

		Router router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 3);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.CORE_PHY_LO_INTERFACE));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.CORE_PHY_INTERFACE_MASTER));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.CORE_PHY_INTERFACE_BKP));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CPE1_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 4);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.INTER1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.DOWN1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.UP1_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.LO1_PHY_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CPE2_PHY_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertTrue(router.getInterfaces().size() >= 4);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.INTER2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.DOWN2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.UP2_PHY_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.LO2_PHY_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		Domain domain = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.AUTOBAHN);
		Assert.assertNotNull(domain);
		Assert.assertTrue(domain.getInterfaces().size() >= 6);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(),
				SPTemplateConstants.INTER1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.DOWN1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(),
				SPTemplateConstants.INTER2_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.DOWN2_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(),
				SPTemplateConstants.CLIENT1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(),
				SPTemplateConstants.CLIENT1_PHY_INTERFACE_AUTOBAHN));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(domain.getInterfaces()));

	}

	private void checkModelHasAllLogicalElements(VCPENetworkModel model) {

		LogicalRouter router = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE1_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.INTER1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.DOWN1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.UP1_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.LO1_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		router = (LogicalRouter) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.VCPE2_ROUTER);
		Assert.assertNotNull(router);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.INTER2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.DOWN2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.UP2_INTERFACE_LOCAL));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(router.getInterfaces(), SPTemplateConstants.LO2_INTERFACE));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(router.getInterfaces()));

		Domain domain = (Domain) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.AUTOBAHN);
		Assert.assertNotNull(domain);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.CLIENT1_INTERFACE_AUTOBAHN));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(domain.getInterfaces(), SPTemplateConstants.CLIENT2_INTERFACE_AUTOBAHN));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(domain.getInterfaces()));

		Router core = (Router) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CORE_PHY_ROUTER);
		Assert.assertNotNull(core);
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), SPTemplateConstants.CORE_LO_INTERFACE));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), SPTemplateConstants.UP1_INTERFACE_PEER));
		Assert.assertNotNull(VCPENetworkModelHelper.getElementByTemplateName(core.getInterfaces(), SPTemplateConstants.UP2_INTERFACE_PEER));
		Assert.assertTrue(VCPENetworkModelHelper.getInterfaces(model.getElements()).containsAll(core.getInterfaces()));

		// test all links are in place
		List<Link> links = VCPENetworkModelHelper.getLinks(model.getElements());
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_LINK)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_LINK)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_LINK_LOCAL)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK_AUTOBAHN)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_LINK_LOCAL)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_LOCAL)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_AUTOBAHN)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_LOCAL)));
		Assert.assertTrue(links.contains((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_AUTOBAHN)));

		Link link = (Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK);
		Assert.assertNotNull(link);
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_LINK_LOCAL)));
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK_AUTOBAHN)));
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_LINK_LOCAL)));

		link = (Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK);
		Assert.assertNotNull(link);
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_LOCAL)));
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_AUTOBAHN)));

		link = (Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK);
		Assert.assertNotNull(link);
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_LOCAL)));
		Assert.assertTrue(link.getImplementedBy().contains(
				(Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_AUTOBAHN)));

		// test link interfaces
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_LINK_LOCAL)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_LINK_LOCAL)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK_AUTOBAHN)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER_LINK_AUTOBAHN)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_LINK_LOCAL)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.INTER2_LINK_LOCAL)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_LOCAL)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_LOCAL)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_AUTOBAHN)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT1_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN1_LINK_AUTOBAHN)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_LOCAL)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_LOCAL)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_AUTOBAHN)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.CLIENT2_INTERFACE_AUTOBAHN),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.DOWN2_LINK_AUTOBAHN)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_LINK)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_INTERFACE_PEER),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP1_LINK)).getSink());

		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_INTERFACE_LOCAL),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_LINK)).getSource());
		Assert.assertEquals(VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_INTERFACE_PEER),
				((Link) VCPENetworkModelHelper.getElementByTemplateName(model, SPTemplateConstants.UP2_LINK)).getSink());

		// TODO test VRRP and BGP are set

	}

}
