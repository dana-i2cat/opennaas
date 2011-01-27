package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPConfigurationInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.ManagedSystemElement;
import net.i2cat.mantychore.model.NextHopRoute;
import net.i2cat.netconf.rpc.QueryFactory;

import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class RefreshCommand extends JunosCommand {

	public static final String	REFRESH		= "refresh";
	public static final String	TEMPLATE	= "/VM_files/getconfiguration.vm";

	private String				source		= "running";
	private String				filter		= null;
	private String				attrFilter	= null;

	/** logger **/
	Logger						log			= LoggerFactory
																.getLogger(JunosCommand.class);

	public RefreshCommand() {
		super(REFRESH, TEMPLATE);
	}

	public void initializeCommand(String source, String filter, String attrFilter) {

		// Default configuration to refresh is running
		this.source = (source == null) ? "running" : source;
		this.filter = filter;
		this.attrFilter = attrFilter;

	}

	public void createCommand() {
		// the query does not need get config
		try {
			command = QueryFactory.newGetConfig(source, filter, attrFilter);
		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		} catch (ParseErrorException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void parseResponse(ManagedElement routermodel) throws CommandException {

		try {
			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) routermodel;
			DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
			logicalInterfParser.init();
			/*
			 * parse a string to byte array, and it is sent to the logicalInterfaceParser
			 */
			if (response.getMessage() != null) {
				logicalInterfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
				// add to the router model
				for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
					routerModel.addManagedSystemElement((ManagedSystemElement) logicalInterfParser.getMapElements().get(keyInterf));
				}
			}

			/* Parse routing options info */
			DigesterEngine routingOptionsfParser = new RoutingOptionsParser();
			routingOptionsfParser.init();

			if (response.getMessage() != null) {
				routingOptionsfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));
				// add to the router model
				for (String keyInterf : routingOptionsfParser.getMapElements().keySet()) {
					NextHopRoute nh = (NextHopRoute) routingOptionsfParser.getMapElements().get(keyInterf);
					routerModel.addNextHopRoute(nh);
				}
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SAXException e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {
		// TODO Auto-generated method stub

	}
}
