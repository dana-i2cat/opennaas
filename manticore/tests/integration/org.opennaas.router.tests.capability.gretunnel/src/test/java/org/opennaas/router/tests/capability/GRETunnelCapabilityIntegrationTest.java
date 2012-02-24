package org.opennaas.router.tests.capability;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.ResourceIdentifier;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.command.Response.Status;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.MockResource;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.queue.QueueConstants;
import org.opennaas.core.resources.queue.QueueResponse;
import org.opennaas.router.tests.capability.mock.MockBootstrapper;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public class GRETunnelCapabilityIntegrationTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log				= LogFactory
												.getLog(GRETunnelCapabilityIntegrationTest.class);
	static MockResource	mockResource;

	static ICapability	gretunnelCapability;

	private ICapability	queueCapability;

	@Inject
	BundleContext		bundleContext	= null;

	@Configuration
	public static Option[] configure() {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				, vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);
		return options;
	}

	@Before
	public void initBundles() {

		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		initResource();
		initCapability();
	}

	public void initResource() {

		mockResource = new MockResource();
		mockResource.setModel((IModel) new ComputerSystem());
		mockResource.setBootstrapper(new MockBootstrapper());

		List<String> capabilities = new ArrayList<String>();

		capabilities.add("gretunnel");
		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		IResourceIdentifier resourceIdentifier = new ResourceIdentifier(resourceDescriptor.getInformation().getType(), resourceDescriptor.getId());
		mockResource.setResourceIdentifier(resourceIdentifier);
	}

	public void initCapability() {

		try {

			log.info("INFO: Before Test, getting queue...");
			ICapabilityFactory queueManagerFactory = getOsgiService(ICapabilityFactory.class, "capability=queue", 5000);
			Assert.assertNotNull(queueManagerFactory);

			queueCapability = queueManagerFactory.create(mockResource);
			queueCapability.initialize();

			IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
			protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

			ICapabilityFactory gretunnelFactory = getOsgiService(ICapabilityFactory.class, "capability=gretunnel", 10000);
			// Test elements not null
			log.info("Checking gretunnel factory");
			Assert.assertNotNull(gretunnelFactory);
			log.info("Checking capability descriptor");
			Assert.assertNotNull(mockResource.getResourceDescriptor().getCapabilityDescriptor("gretunnel"));
			log.info("Creating gretunnel capability");
			gretunnelCapability = gretunnelFactory.create(mockResource);
			Assert.assertNotNull(gretunnelCapability);
			gretunnelCapability.initialize();

			mockResource.addCapability(queueCapability);
			mockResource.addCapability(gretunnelCapability);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void TestGRETunnelAction() {
		log.info("TEST GRE TUNNEL ACTION");
		try {

			Response resp = (Response) gretunnelCapability.sendMessage(ActionConstants.CREATETUNNEL, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) gretunnelCapability.sendMessage(ActionConstants.GETTUNNELCONFIG, newParamsGRETunnelService());
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) gretunnelCapability.sendMessage(ActionConstants.DELETETUNNEL, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) gretunnelCapability.sendMessage(ActionConstants.SHOWTUNNELS, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			resp = (Response) gretunnelCapability.sendMessage(ActionConstants.GETCONFIG, null);
			Assert.assertTrue(resp.getStatus() == Status.OK);
			Assert.assertTrue(resp.getErrors().size() == 0);

			List<IAction> queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 5);

			QueueResponse queueResponse = (QueueResponse) queueCapability.sendMessage(QueueConstants.EXECUTE, null);
			Assert.assertTrue(queueResponse.getResponses().size() == 5);

			Assert.assertTrue(queueResponse.getPrepareResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getConfirmResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRefreshResponse().getStatus() == ActionResponse.STATUS.OK);
			Assert.assertTrue(queueResponse.getRestoreResponse().getStatus() == ActionResponse.STATUS.PENDING);

			Assert.assertTrue(queueResponse.isOk());

			queue = (List<IAction>) queueCapability.sendMessage(QueueConstants.GETQUEUE, null);
			Assert.assertTrue(queue.size() == 0);
		} catch (CapabilityException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		log.debug("FFFF test setup uri: " + uri + "Length: " + uri.length());
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

	}

	private Object newParamsGRETunnelService() {
		GRETunnelService greService = new GRETunnelService();
		greService.setName("gre-0/1/0");
		return greService;
	}
}