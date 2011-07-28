package helpers;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.osgi.service.command.CommandProcessor;
import org.osgi.service.command.CommandSession;
//import org.apache.felix.service.command.CommandProcessor;
//import org.apache.felix.service.command.CommandSession;



public class KarafCommandHelper {

	public static ArrayList<String> executeCommand(String command, CommandProcessor c) throws Exception {
		// Run some commands to make sure they are installed properly
		CommandProcessor cp = c;
		ByteArrayOutputStream outputError = new ByteArrayOutputStream();
		PrintStream psE = new PrintStream(outputError);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(output);
		CommandSession cs = cp.createSession(System.in, ps, psE);

		try {
			cs.execute(command);
			ArrayList<String> outputs = new ArrayList<String>();
			outputs.add(output.toString());
			outputs.add(outputError.toString());
			return outputs;
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
