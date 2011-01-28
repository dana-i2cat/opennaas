package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshCommand extends JunosCommand {

	public static final String	REFRESH		= "refresh";
	public static final String	TEMPLATE	= "/VM_files/getconfiguration.vm";

	private String				source		= null;
	private String				filter		= null;
	private String				attrFilter	= null;

	/** logger **/
	Logger						log			= LoggerFactory
																.getLogger(JunosCommand.class);

	public RefreshCommand() {
		super(REFRESH, TEMPLATE);
	}

	public void initializeCommand(String source) {
		this.source = source;
	}

	// public void parseResponse(ManagedElement routermodel) throws CommandException {
	//
	// try {
	// net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) routermodel;
	// DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
	// logicalInterfParser.init();
	// /*
	// * parse a string to byte array, and it is sent to the logicalInterfaceParser
	// */
	// if (response.getMessage() != null) {
	// logicalInterfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
	// // add to the router model
	// for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
	// routerModel.addManagedSystemElement((ManagedSystemElement) logicalInterfParser.getMapElements().get(keyInterf));
	// }
	// }
	//
	// /* Parse routing options info */
	// DigesterEngine routingOptionsfParser = new RoutingOptionsParser();
	// routingOptionsfParser.init();
	//
	// if (response.getMessage() != null) {
	// routingOptionsfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
	// // add to the router model
	// for (String keyInterf : routingOptionsfParser.getMapElements().keySet()) {
	// NextHopRoute nh = (NextHopRoute) routingOptionsfParser.getMapElements().get(keyInterf);
	// routerModel.addNextHopRoute(nh);
	// }
	// }
	//
	// } catch (IOException e) {
	// log.error(e.getMessage());
	// } catch (SAXException e) {
	// log.error(e.getMessage());
	// }
	//
	// }

	@Override
	public Object message() {
		// TODO Auto-generated method stub
		return QueryFactory.newGetConfig(source, netconfXML, attrFilter);

	}

	@Override
	public void parseResponse(Object response, Object model) {
		// TODO Auto-generated method stub
		// try {
		// net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) routermodel;
		// DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
		// logicalInterfParser.init();
		//
		//		
		// /*
		// * parse a string to byte array, and it is sent to the logicalInterfaceParser
		// */
		// if (response.getMessage() != null) {
		// logicalInterfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
		// // add to the router model
		// for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
		// routerModel.addManagedSystemElement((ManagedSystemElement) logicalInterfParser.getMapElements().get(keyInterf));
		// }
		// }
		//
		// /* Parse routing options info */
		// DigesterEngine routingOptionsfParser = new RoutingOptionsParser();
		// routingOptionsfParser.init();
		//
		// if (response.getMessage() != null) {
		// routingOptionsfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
		// // add to the router model
		// for (String keyInterf : routingOptionsfParser.getMapElements().keySet()) {
		// NextHopRoute nh = (NextHopRoute) routingOptionsfParser.getMapElements().get(keyInterf);
		// routerModel.addNextHopRoute(nh);
		// }
		// }
		//
		// } catch (IOException e) {
		// log.error(e.getMessage());
		// } catch (SAXException e) {
		// log.error(e.getMessage());
		// }
	}
}
