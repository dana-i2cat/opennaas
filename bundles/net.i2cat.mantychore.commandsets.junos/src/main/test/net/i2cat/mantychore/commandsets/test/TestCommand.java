package net.i2cat.mantychore.commandsets.test;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import net.i2cat.mantychore.commandsets.commands.AddStaticRouteCommand;
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
			assertNotNull(command);
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
			assertNotNull(command);
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
	public void AddStaticRouteCommand() {
		// List<String> rutas = new ArrayList<String>();
		// rutas.add("192.168.13.0/24");
		// rutas.add("192.168.15.0/24");

		HashMap<String, String> rutas = new HashMap<String, String>();
		Map map = new HashMap();
		// map.put("IpAddressDestinationSubNetwork", "192.168.13.0/24");
		// map.put("IpAddressNextHop", "194.68.13.1");

		map.put("IpAddressDestinationSubNetwork", "192.168.15.0/24");
		map.put("IpAddressNextHop", "194.68.15.1");
		rutas.putAll(map);

		System.out.println(rutas.values());

		AddStaticRouteCommand addStaticRouteCommand = new AddStaticRouteCommand(
				"routerV1", rutas);
		try {
			String command = addStaticRouteCommand.prepareCommand();
			printCommand(command);
			assertNotNull(command);
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
