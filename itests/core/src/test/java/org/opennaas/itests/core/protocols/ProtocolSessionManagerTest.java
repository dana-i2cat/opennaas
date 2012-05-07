package org.opennaas.itests.core.protocols;

import static org.junit.Assert.*;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;
import static org.opennaas.extensions.itests.helpers.OpennaasExamOptions.*;
import static org.ops4j.pax.exam.CoreOptions.*;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class ProtocolSessionManagerTest {

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)")
	private BlueprintContainer		routerRepositoryService;

	// @Inject
	// @Filter("(osgi.blueprint.container.symbolicname=org.opennaas.core.protocols-sessionmanager)")
	// private BlueprintContainer protocolManagerService;

	// @Inject
	// @Filter("(osgi.blueprint.container.symbolicname=org.opennaas.core.resources)")
	// private BlueprintContainer resourceManagerService;

	@Inject
	@Filter("(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.capability.chassis)")
	private BlueprintContainer		chasisService;

	private ResourceDescriptor		resourceDescriptor;
	private ProtocolSessionContext	sessionContext;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-router"),
				noConsole(),
				keepRuntimeFolder());
	}

	@Before
	public void init() {

		List<String> capabilities = new ArrayList<String>();
		capabilities.add("chassis");
		capabilities.add("queue");
		resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("TestResource", "router", capabilities);

		sessionContext = newSessionContextNetconf();
	}

	@Test
	public void testSessionManagerLifecycleIsSyncWithResourceLifecycle() throws Exception {

		IResource resource = resourceManager.createResource(resourceDescriptor);
		String resourceId = resource.getResourceIdentifier().getId();

		assertTrue("New resource should be registered in ProtocolManager",
				protocolManager.getAllResourceIds().contains(resourceId));

		// FIXME this will create a protocolSessionManager if it does not exists
		// use a true getter method (not modifying state) to test!
		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(resourceId);
		assertEquals(resourceId, sessionManager.getResourceID());
		assertTrue("No context registered yet", sessionManager.getRegisteredContexts().isEmpty());
		assertTrue("No sessions are created", sessionManager.getAllProtocolSessionIds().isEmpty());

		protocolManager.getProtocolSessionManager(resourceId).registerContext(sessionContext);
		assertTrue("Context is registered", sessionManager.getRegisteredContexts().contains(sessionContext));
		assertTrue("No sessions are created", sessionManager.getAllProtocolSessionIds().isEmpty());

		resourceManager.startResource(resource.getResourceIdentifier());

		assertEquals(1, sessionManager.getAllProtocolSessionIds().size());
		for (String sessionId : sessionManager.getAllProtocolSessionIds()) {
			IProtocolSession session = sessionManager.getSessionById(sessionId, false);
			assertEquals(sessionContext, session.getSessionContext());
			assertEquals(IProtocolSession.Status.CONNECTED, session.getStatus());
		}

		resourceManager.stopResource(resource.getResourceIdentifier());
		assertFalse("Context should be still registered ", sessionManager.getRegisteredContexts().isEmpty());
		assertTrue("No active sessions", sessionManager.getAllProtocolSessionIds().isEmpty());

		resourceManager.removeResource(resource.getResourceIdentifier());
		assertTrue("Contexts should be unregistered", sessionManager.getRegisteredContexts().isEmpty());
		assertTrue("Sessions should be destroyed", sessionManager.getAllProtocolSessionIds().isEmpty());

		assertFalse("Removed resource should not be registered in ProtocolManager",
				protocolManager.getAllResourceIds().contains(resourceId));
	}

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
		// ADDED
		return protocolSessionContext;
	}

}
