package net.i2cat.mantychore.commandskaraf;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceRepository;
import net.i2cat.nexus.resources.descriptor.CapabilityDescriptor;
import net.i2cat.nexus.resources.descriptor.ResourceDescriptor;
import net.i2cat.nexus.resources.protocol.IProtocolManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

@RunWith(JUnit4TestRunner.class)
public class QueueCommandsKarafTest extends AbstractIntegrationTest {
	// import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.vmOption;

	static Log			log	= LogFactory
									.getLog(ResourceCommandsKarafTest.class);

	IResourceRepository	repository;

	public String capture() throws IOException {
		StringWriter sw = new StringWriter();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(System.in));
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

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		IProtocolManager protocolManager = getOsgiService(IProtocolManager.class, 5000);
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

	public void initBundles() {
		log.info("Waiting to load all bundles");
		/* Wait for the activation of all the bundles */
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		log.info("Loaded all bundles");
		/* init capability */

		log.info("This is running inside Equinox. With all configuration set up like you specified. ");

		repository = getOsgiService(IResourceRepository.class, 50000);

		log.info("INFO: Initialized!");

	}

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getMantychoreTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	public Object executeCommand(String command) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = getOsgiService(CommandProcessor.class);
		CommandSession cs = cp.createSession(System.in, System.out, System.err);
		Object commandOutput = null;
		try {
			commandOutput = cs.execute(command);
			return commandOutput;
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		}

		cs.close();
		return commandOutput;
	}

	@Test
	public void SetAndGetInterfacesCommandTest() {
		initBundles();
		ResourceDescriptor resourceDescriptor = RepositoryHelper.newResourceDescriptor("router", "resource1");
		List<CapabilityDescriptor> capabilityDescriptors = new ArrayList<CapabilityDescriptor>();
		capabilityDescriptors.add(RepositoryHelper.newIPCapabilityDescriptor());
		capabilityDescriptors.add(RepositoryHelper.newQueueCapabilityDescriptor());
		resourceDescriptor.setCapabilityDescriptors(capabilityDescriptors);

		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			repository.startResource(resource.getResourceDescriptor().getId());
			// resource.start();
			createProtocolForResource(resource.getResourceIdentifier().getId());

			log.debug("executeCommand(ip:setIPv4 " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0)");
			Integer response = (Integer) executeCommand("ip:setIPv4  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0");
			log.debug(response);
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the setInterfaces command");

			log.debug("executeCommand(queue:listActions " + resourceFriendlyID);
			response = (Integer) executeCommand("queue:listActions  " + resourceFriendlyID);
			log.debug(response);
			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the setInterfaces command");

			log.debug("executeCommand(queue:execute " + resourceFriendlyID);
			response = (Integer) executeCommand("queue:execute  " + resourceFriendlyID);
			log.debug(response);

			// Assert.assertNotNull(response);
			if (response != null)
				Assert.fail("Error in the setInterfaces command");

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}
}