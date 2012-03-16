package mantychore;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.nexus.tests.KarafCommandHelper;
import net.i2cat.nexus.tests.ResourceHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
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
public class CreateLogicalRouterTest
{
	static Log log = LogFactory.getLog(CreateLogicalRouterTest.class);

	private String				resourceFriendlyID;
	private String				LRFriendlyID	= "pepito";
	private IResource			resource;

	private Boolean				isMock;

	@Inject
	private CommandProcessor commandprocessor;

	@Inject
	private IResourceManager resourceManager;

	@Inject
	private BundleContext bundleContext;

	@Inject
	private	IProtocolManager protocolManager;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.chassis)")
    private BlueprintContainer chasisService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer queueService;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
	}

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
					   includeTestHelper(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	public Boolean createProtocolForResource(String resourceId) throws ProtocolException {
		ProtocolSessionContext context = ResourceHelper.newSessionContextNetconf();

		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId, context);

		return context.getSessionParameters().get(context.PROTOCOL_URI).toString().contains("mock");
	}

	@Before
	public void initResource() throws Exception {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("chassis");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("junosm20", "router", capabilities);
		resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		resource = resourceManager.createResource(resourceDescriptor);
		isMock = createProtocolForResource(resource.getResourceIdentifier().getId());
		resourceManager.startResource(resource.getResourceIdentifier());
	}

	@After
	public void clearRepo() {
		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources().size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				try {
					resourceManager.stopResource(resource.getResourceIdentifier());
				} catch (ResourceException e) {
					log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
				}
			}
			try {
				resourceManager.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource " + resource.getResourceIdentifier().getId() + " from repository.");
			}

		}

		log.info("Resource repo cleared!");
	}

	@Test
	public void createLogicalRouterOnRealRouterTest() throws Exception {

		List<String> response;
		List<String> response1;

		if (isMock) {
			LRFriendlyID = "cpe1";
		} else {
			LRFriendlyID = "pepito";
		}

		// creating LogicalRouter
		response =
			KarafCommandHelper.executeCommand("chassis:createLogicalRouter " + resourceFriendlyID + " " + LRFriendlyID,
											  commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());
		response =
			KarafCommandHelper.executeCommand("queue:execute " + resourceFriendlyID,
											  commandprocessor);
		Assert.assertTrue(response.get(1).isEmpty());

		response1 = KarafCommandHelper.executeCommand("resource:list ",
													  commandprocessor);
		Assert.assertTrue(response1.get(1).isEmpty());
		if (!isMock) {
			Assert.assertTrue(ExistanceHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));
			Assert.assertTrue(response1.get(0).contains(LRFriendlyID));
		}
	}

	@Test
	public void listLogicalRoutersOnResourceTest() throws Exception {

		List<String> response;
		if (isMock) {
			LRFriendlyID = "cpe1";
		} else {
			LRFriendlyID = "pepito";
		}
		// chassis:listLogicalRouters
		response =
			KarafCommandHelper.executeCommand("chassis:listLogicalRouters " + resourceFriendlyID,
											  commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		Assert.assertTrue(response.get(0).contains(LRFriendlyID));

		if (!isMock) {
			Assert.assertTrue(ExistanceHelper.checkExistLogicalRouter((ComputerSystem) resource.getModel(), LRFriendlyID));
		}
	}

	@Test
	public void discoveryOnBootstrapLogicalRoutersTest() throws Exception {
		if (isMock) {
			LRFriendlyID = "cpe1";
		} else {
			LRFriendlyID = "pepito";
		}

		// resource:list
		List<String> response =
			KarafCommandHelper.executeCommand("resource:list ", commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// check that the logical router is on the list
		Assert.assertTrue(response.get(0).contains(LRFriendlyID));

		response =
			KarafCommandHelper.executeCommand("resource:info " + "router:" + LRFriendlyID,
											  commandprocessor);
		// assert command output no contains ERROR tag
		Assert.assertTrue(response.get(1).isEmpty());

		// check resource initialized
		if (!isMock) {
			Assert.assertTrue(response.get(0).contains("INITIALIZED"));
		}
	}
}