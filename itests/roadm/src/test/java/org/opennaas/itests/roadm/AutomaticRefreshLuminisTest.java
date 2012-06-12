package org.opennaas.itests.roadm;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.mock.MockResource;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class AutomaticRefreshLuminisTest
{
	private final static Log	log				= LogFactory.getLog(AutomaticRefreshLuminisTest.class);

	private List<String>		startupActionNames;
	private AbstractCapability	connectionsCapability;
	private IResource			mockResource	= new MockResource();

	@Inject
	@Filter("(capability=connections)")
	private ICapabilityFactory	connectionFactory;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.roadm.repository)")
	private BlueprintContainer	routerService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-luminis", "opennaas-roadm-proteus"),
				includeTestHelper(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * tests ConnectionsCapability.getActionSet().getStartupRefreshAction() != null
	 */
	@Test
	public void getStartUpRefreshActionTest() throws Exception {

		mockResource.setResourceDescriptor(ResourceHelper.newResourceDescriptorProteus("roadm"));

		// Test elements not null
		log.info("Checking connections factory");
		Assert.assertNotNull(connectionFactory);
		log.info("Checking capability descriptor");
		Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("connections"));
		log.info("Creating connection capability");
		connectionsCapability = (AbstractCapability) connectionFactory.create(mockResource);
		Assert.assertNotNull(connectionsCapability);
		connectionsCapability.initialize();

		Assert.assertFalse(connectionsCapability.getActionSet().getRefreshActionName().isEmpty());
		startupActionNames = connectionsCapability.getActionSet().getRefreshActionName();
	}

	// TODO test startupActionName action is executed during resourceRespository.startResource() (during bootstrap)

}
