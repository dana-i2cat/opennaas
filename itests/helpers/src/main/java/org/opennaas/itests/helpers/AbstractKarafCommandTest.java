package org.opennaas.itests.helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.felix.gogo.runtime.CommandNotFoundException;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.junit.ProbeBuilder;
import org.osgi.framework.Constants;

public class AbstractKarafCommandTest
{
	@Inject
	protected CommandProcessor	commandProcessor;

	@ProbeBuilder
	public static TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
		probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,org.apache.felix.service.*;status=provisional");
		return probe;
	}

	protected List<String> executeCommand(String command) throws Exception
	{
		return executeCommand(command, commandProcessor);
	}

	protected List<String> executeCommand(String command, CommandProcessor cp) throws Exception
	{
		boolean notfound;
		int notfoundCounter = 0;

		// Run some commands to make sure they are installed properly
		ByteArrayOutputStream outputError = new ByteArrayOutputStream();
		PrintStream psE = new PrintStream(outputError);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);

		CommandSession cs = cp.createSession(System.in, ps, psE);

		ArrayList<String> outputs = new ArrayList<String>();

		do {
			try {
				cs.execute(command);
				outputs.add(output.toString());
				outputs.add(outputError.toString());
				cs.close();
				notfound = false;
			} catch (CommandNotFoundException nfe) {
				notfound = true;
				notfoundCounter++;
				if (notfoundCounter > 50) {
					throw nfe;
				}
				Thread.sleep(200);
			} catch (IllegalArgumentException e) {
				// throw new IllegalArgumentException("Action should have thrown an exception because: " + e.toString());
				notfound = true;
				notfoundCounter++;
				if (notfoundCounter > 50) {
					throw e;
				}
				Thread.sleep(200);
			} catch (NoSuchMethodException a) {
				// log.error("Method for command not found: " + a.getLocalizedMessage());
				throw new NoSuchMethodException("Method for command not found.");
			}
		} while (notfound);

		return outputs;
	}
}