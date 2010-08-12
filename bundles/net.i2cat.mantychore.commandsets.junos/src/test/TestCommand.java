import net.i2cat.mantychore.commandsets.commands.GetConfigurationCommand;
import net.i2cat.mantychore.commandsets.commands.KeepAliveCommand;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCommand {

	/** The logger **/
	Logger	log	= LoggerFactory.getLogger(TestCommand.class);

	@Test
	public void keepAliveCommand() {
		KeepAliveCommand commandAlive = new KeepAliveCommand();
		try {
			String command = commandAlive.prepareCommand();
			printCommand(command);
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void GetConfigurationCommand() {
		GetConfigurationCommand commandGetConfig = new GetConfigurationCommand();
		try {
			String command = commandGetConfig.prepareCommand();
			printCommand(command);
		} catch (ResourceNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void printCommand(String command) {
		log.info("command tested");
		log.info("--------------------------------------");
		log.info(command);
		log.info("--------------------------------------");
	}

}
