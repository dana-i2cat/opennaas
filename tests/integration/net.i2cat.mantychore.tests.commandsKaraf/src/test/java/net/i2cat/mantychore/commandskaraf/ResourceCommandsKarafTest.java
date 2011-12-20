package net.i2cat.mantychore.commandskaraf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.IntegrationTestsHelper;
import net.i2cat.nexus.tests.KarafCommandHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.swissbox.tinybundles.core.TinyBundles;
import org.ops4j.pax.swissbox.tinybundles.dp.Constants;

@RunWith(JUnit4TestRunner.class)
public class ResourceCommandsKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log					log	= LogFactory.getLog(ResourceCommandsKarafTest.class);

	IResourceManager			resourceManager;

	private CommandProcessor	commandprocessor;

	public String capture() throws IOException {
		StringWriter sw = new StringWriter();
		BufferedReader rdr = new BufferedReader(
				new InputStreamReader(System.in));
		String s = rdr.readLine();
		while (s != null) {
			sw.write(s);
			s = rdr.readLine();
		}
		return sw.toString();
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}") || uri.isEmpty()) {
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

	public void createProtocolForResource(String resourceId)
			throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(
				IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId,
				newSessionContextNetconf());

	}

	public void initBundles() {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		resourceManager = getOsgiService(IResourceManager.class, 50000);
		commandprocessor = getOsgiService(CommandProcessor.class);

		clearRepo();

		log.info("INFO: Initialized!");

	}

	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(IntegrationTestsHelper.FELIX_CONTAINER),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	@Configuration
	public Option[] additionalConfiguration() throws Exception {
		return combine(configuration(), new Customizer() {
			@Override
			public InputStream customizeTestProbe(InputStream testProbe)
					throws Exception {
				return TinyBundles
						.modifyBundle(testProbe)
						.set(Constants.DYNAMICIMPORT_PACKAGE,
								"*,org.apache.felix.service.*;status=provisional")
						.build();
			}
		});
	}

	@Test
	public void InfoCommandTest() {
		initBundles();
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
				.newResourceDescriptor("junosm20", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation()
				.getType()
				+ ":"
				+ resourceDescriptor.getInformation().getName();

		try {
			IResource resource = resourceManager
					.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			resourceManager.startResource(resource.getResourceIdentifier());

			List<String> response = KarafCommandHelper.executeCommand(
					"resource:info " + resourceFriendlyID, commandprocessor);

			if (!response.get(1).isEmpty()) {
				Assert.fail(response.get(1));
			}
			Assert.assertTrue(response.get(1).isEmpty());

			Assert.assertTrue(response.get(0).contains("Resource ID: junosm20"));
			Assert.assertTrue(response.get(0).contains("Type: ipv4"));
			Assert.assertTrue(response.get(0).contains("Type: queue"));

			resourceManager.stopResource(resource.getResourceIdentifier());
			resourceManager.removeResource(resource.getResourceIdentifier());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	public void clearRepo() {

		log.info("Clearing resource repo");

		IResource[] toRemove = new IResource[resourceManager.listResources()
				.size()];
		toRemove = resourceManager.listResources().toArray(toRemove);

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				try {
					resourceManager.stopResource(resource
							.getResourceIdentifier());
				} catch (ResourceException e) {
					log.error("Failed to remove resource "
							+ resource.getResourceIdentifier().getId()
							+ " from repository.");
				}
			}
			try {
				resourceManager
						.removeResource(resource.getResourceIdentifier());
			} catch (ResourceException e) {
				log.error("Failed to remove resource "
						+ resource.getResourceIdentifier().getId()
						+ " from repository.");
			}

		}

		log.info("Resource repo cleared!");
	}

}