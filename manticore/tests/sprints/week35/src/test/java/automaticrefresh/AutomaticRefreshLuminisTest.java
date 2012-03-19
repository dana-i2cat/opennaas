package automaticrefresh;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;

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
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.luminis.ROADM.repository)")
    private BlueprintContainer routerService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-luminis"),
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
