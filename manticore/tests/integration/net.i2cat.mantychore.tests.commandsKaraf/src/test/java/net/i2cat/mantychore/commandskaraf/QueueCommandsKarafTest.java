package net.i2cat.mantychore.commandskaraf;

import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.*;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.i2cat.nexus.tests.KarafCommandHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.felix.service.command.CommandProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceRepository;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Customizer;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.ops4j.pax.exam.spi.reactors.EagerSingleStagedReactorFactory;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.Constants;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(EagerSingleStagedReactorFactory.class)
public class QueueCommandsKarafTest
{
	static Log					log	=
		LogFactory.getLog(ResourceCommandsKarafTest.class);

	@Inject
	@Filter("(type=router)")
	private IResourceRepository	repository;

	@Inject
	private CommandProcessor	commandprocessor;

	@Inject
	private IProtocolManager	protocolManager;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.repository)")
    private BlueprintContainer	routerRepositoryService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.capability.ip)")
    private BlueprintContainer	ipService;

    @Inject
    @Filter("(osgi.blueprint.container.symbolicname=net.i2cat.mantychore.queuemanager)")
    private BlueprintContainer	queueService;

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

	public void createProtocolForResource(String resourceId) throws ProtocolException {
		protocolManager.getProtocolSessionManagerWithContext(resourceId, newSessionContextNetconf());

	}

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
        return probe;
    }

	@Configuration
	public static Option[] configuration() {
		return options(karafDistributionConfiguration()
					   .frameworkUrl(maven()
									 .groupId("net.i2cat.mantychore")
									 .artifactId("assembly")
									 .type("zip")
									 .classifier("bin")
									 .versionAsInProject())
					   .karafVersion("2.2.2")
					   .name("mantychore")
					   .unpackDirectory(new File("target/paxexam")),
					   editConfigurationFilePut("etc/org.apache.karaf.features.cfg",
												"featuresBoot",
												"opennaas-router,nexus-tests-helper"),
					   configureConsole()
					   .ignoreLocalConsole()
					   .ignoreRemoteShell(),
					   keepRuntimeFolder());
	}

	@Test
	public void SetAndGetInterfacesCommandTest() {
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("ipv4");
		capabilities.add("queue");

		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("resource1", "router", capabilities);
		String resourceFriendlyID = resourceDescriptor.getInformation().getType() + ":" + resourceDescriptor.getInformation().getName();

		try {
			IResource resource = repository.createResource(resourceDescriptor);
			createProtocolForResource(resource.getResourceIdentifier().getId());
			repository.startResource(resource.getResourceDescriptor().getId());

			ArrayList<String> response = KarafCommandHelper.executeCommand(
					"ipv4:setIP  " + resourceFriendlyID + " fe-0/1/2.0 192.168.1.1 255.255.255.0", commandprocessor);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("queue:listActions  " + resourceFriendlyID, commandprocessor);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			response = KarafCommandHelper.executeCommand("queue:execute  " + resourceFriendlyID, commandprocessor);
			// assert command output does not contain ERROR tag
			Assert.assertTrue(response.get(1).isEmpty());

			repository.stopResource(resource.getResourceIdentifier().getId());
			repository.removeResource(resource.getResourceIdentifier().getId());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}
}