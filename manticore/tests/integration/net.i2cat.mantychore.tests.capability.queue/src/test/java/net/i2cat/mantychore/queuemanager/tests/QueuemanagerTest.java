package net.i2cat.mantychore.queuemanager.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.queuemanager.IQueueManagerService;

import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockAction;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

import static net.i2cat.nexus.tests.OpennaasExamOptions.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.swissbox.framework.ServiceLookup.getService;

@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
@RunWith(JUnit4TestRunner.class)
public class QueuemanagerTest
{
	private final static Log		log				= LogFactory.getLog(QueuemanagerTest.class);

	private final String			resourceID		= "junosResource";
	private MockResource			mockResource;
	private ICapability				queueCapability;
	private IQueueManagerService	queueManagerService;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer		routerService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer		queueService;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
					   includeFeatures("opennaas-router"),
					   includeSwissboxFramework(),
					   noConsole(),
					   keepRuntimeFolder());
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		return protocolSessionContext;
	}

	@Before
	public void before() throws ProtocolException, CapabilityException {
		/* initialize model */
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Before test, getting queue...");
		queueCapability = queueManagerFactory.create(mockResource);
		queueManagerService = getService(bundleContext, IQueueManagerService.class, 20000,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")");
	}

	@After
	public void after() {
		log.info("INFO: After test, cleaning queue...");
		queueManagerService.empty();
	}

	@Test
	public void removeAction() {
		log.info("INFO: Remove actions");
		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);
		queueManagerService.empty();
		Assert.assertTrue(queueManagerService.getActions().size() == 0);
		log.info("INFO: OK!");
	}

	@Test
	public void executeAction() throws ProtocolException, CapabilityException {
		log.info("INFO: Execute actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);
		queueManagerService.execute();
		Assert.assertTrue(queueManagerService.getActions().size() == 0);
		log.info("INFO: OK!");
	}

	@Test
	public void listActions() {
		log.info("INFO: List actions");

		IAction action = new MockAction();
		action.setActionID("mockAction");

		queueManagerService.queueAction(action);
		Assert.assertTrue(queueManagerService.getActions().size() == 1);

		for (IAction act : queueManagerService.getActions()) {
			log.info("INFO: action id=" + act.getActionID());
		}

		log.info("INFO: OK!");
	}

}
