
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.i2cat.mantychore.commandsets.commands.AddStaticRouteCommand;
import net.i2cat.mantychore.models.router.RouterModel;
import net.i2cat.mantychore.models.router.StaticRoute;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCommandAddStaticRoute {

	/** The logger **/
	Logger	log	= LoggerFactory.getLogger(TestCommandAddStaticRoute.class);

	// @Test
	// public void keepAliveCommand() {
	// KeepAliveCommand commandAlive = new KeepAliveCommand();
	// try {
	// String command = commandAlive.prepareCommand();
	// printCommand(command);
	// assertNotNull(command);
	// } catch (ResourceNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ParseErrorException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// }
	//
	// @Test
	// public void GetConfigurationCommand() {
	// GetConfigurationCommand commandGetConfig = new GetConfigurationCommand();
	// try {
	// String command = commandGetConfig.prepareCommand();
	// printCommand(command);
	// assertNotNull(command);
	// } catch (ResourceNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (ParseErrorException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	@Test
	public void AddStaticRouteCommand() {

		ArrayList rutas = new ArrayList();
		Map map1 = new HashMap();
		map1.put("IpAddressDestinationSubNetwork", "192.168.13.0/24");
		map1.put("IpAddressNextHop", "194.68.13.1");
		rutas.add(map1);

		Map map2 = new HashMap();
		map2.put("IpAddressDestinationSubNetwork", "192.168.15.0/24");
		map2.put("IpAddressNextHop", "194.68.15.1");
		rutas.add(map2);

		Map map3 = new HashMap();
		map3.put("IpAddressDestinationSubNetwork", "192.168.16.0/24");
		map3.put("IpAddressNextHop", "194.68.16.1");
		rutas.add(map3);

		AddStaticRouteCommand addStaticRouteCommand = new AddStaticRouteCommand(
				"routerV1", rutas);

		RouterModel model = new RouterModel();
		model.setHostName("routerV1");

		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setDestinationNetworkIPAddress("192.168.13.0/24");
		staticRoute.setNextHopIPAddress("194.68.13.1");

		model.addStaticRoutes(staticRoute);

		try {
			String command = addStaticRouteCommand.prepareCommand();
			// printCommand(command);
			assertNotNull(command);

			log.debug("Mensaje de respuesta: " + addStaticRouteCommand.message);
			addStaticRouteCommand.parseResponse(model);

			List<StaticRoute> routes = model.getStaticRoutes();
			assertNotNull(routes);

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

	// private void printCommand(String command) {
	// log.info("command tested");
	// log.info("--------------------------------------");
	// log.info(command);
	// log.info("--------------------------------------");
	// }

}
