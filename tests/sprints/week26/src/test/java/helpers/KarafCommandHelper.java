package helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;

public class KarafCommandHelper {

	public static String executeCommand(String command, CommandProcessor c) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = c;
		ByteArrayOutputStream outputError = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(outputError);
		CommandSession cs = cp.createSession(System.in, System.out, ps);

		try {
			cs.execute(command);
			return outputError.toString();
		} catch (IllegalArgumentException e) {
			Assert.fail("Action should have thrown an exception because: " + e.toString());
		} catch (NoSuchMethodException a) {
			// log.error("Method for command not found: " + a.getLocalizedMessage());
			Assert.fail("Method for command not found.");
		}

		cs.close();
		return null;
	}

}
