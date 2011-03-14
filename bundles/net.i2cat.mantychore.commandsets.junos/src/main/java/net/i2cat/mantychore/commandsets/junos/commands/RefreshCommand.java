package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPConfigurationInterfaceParser;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class RefreshCommand extends JunosCommand {

	public static final String REFRESH = "refresh";
	public static final String TEMPLATE = "/VM_files/getconfiguration.vm";

	//FIXME HOW WE SEND THE REFRESH COMMAND?
	private String source = "running";
	private String filter = null;
	private String attrFilter = null;

	/** logger **/
	Logger log = LoggerFactory.getLogger(JunosCommand.class);

	public RefreshCommand() {
		super(REFRESH, TEMPLATE);
	}
	


	@Override
	public Object sendQuery() {
		// TODO Auto-generated method stub
		return QueryFactory.newGetConfig(source, netconfXML, attrFilter);

	}

	@Override
	public void parseResponse(Object response, Object model) {
		
		 try {
		 net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;
		 DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
		 logicalInterfParser.init();
		 
		 //IT MUST TO RECEIVE A REPLY, THIS ASSERT CAN NOT HAPPEN

		 /* getting interface information */
		 Reply rpcReply = (Reply)response;
		 String message = rpcReply.getContain();
		 logicalInterfParser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		 
		 for (String keyInterf: logicalInterfParser.getMapElements().keySet()) {
			 routerModel.addLogicalDevice((LogicalDevice) logicalInterfParser.getMapElements().get(keyInterf));
		 }
		
		 
//		 /* Parse routing options info */
//		 DigesterEngine routingOptionsfParser = new RoutingOptionsParser();
//		 routingOptionsfParser.init();
//		
//		 if (response.getMessage() != null) {
//		 routingOptionsfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
//		 // add to the router model
//		 for (String keyInterf : routingOptionsfParser.getMapElements().keySet()) {
//		 NextHopRoute nh = (NextHopRoute) routingOptionsfParser.getMapElements().get(keyInterf);
//		 routerModel.addNextHopRoute(nh);
//		 }
//		 }
		
		 } catch (IOException e) {
		 log.error(e.getMessage());
		 } catch (SAXException e) {
		 log.error(e.getMessage());
		 }
		
	}



	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}
}
