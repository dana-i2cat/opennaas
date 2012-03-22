package net.i2cat.nexus.tests;

import java.util.List;
import javax.inject.Inject;

import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.ProbeBuilder;

import org.osgi.framework.Constants;

import org.apache.felix.service.command.CommandProcessor;

public class AbstractKarafCommandTest
{
	@Inject
	protected CommandProcessor commandProcessor;

	@ProbeBuilder
	public static TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	protected List<String> executeCommand(String command) throws Exception
	{
		return KarafCommandHelper.executeCommand(command, commandProcessor);
	}
}