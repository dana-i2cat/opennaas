package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.LogicalInterfaceParser;
import net.i2cat.mantychore.model.EthernetPort;
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

	public static final String	TEMPLATE	= "/getconfiguration.vm";

	/** logger **/
	Logger						log			= LoggerFactory
																.getLogger(JunosCommand.class);

	public RefreshCommand() {
		super(REFRESH, TEMPLATE);
	}

	@Override
	public void initializeCommand(IResourceModel arg0) throws
			CommandException {
		// the query does not need get config
		try {

			String netconfXML = prepareCommand();
			command = QueryFactory.newGet(netconfXML);

		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		} catch (ParseErrorException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {

		try {
			/* Parse Physical interface info */
			// DigesterEngine physicalInterfParser = new
			// PhysicalInterfaceParser();
			// physicalInterfParser.configurableParse(response.getMessage());
			// HashMap<String, Object> interfs =
			// physicalInterfParser.getMapElements();

			HashMap<String, Object> interfs = new HashMap<String, Object>();
			/* Parse logical interface info */
			DigesterEngine logicalInterfParser = new LogicalInterfaceParser();
			logicalInterfParser.init();
			logicalInterfParser.setMapElements(interfs);
			/*
			 * parse a string to byte array, and it is sent to the
			 * logicalInterfaceParser
			 */
			logicalInterfParser.configurableParse(new ByteArrayInputStream(response.getMessage().getBytes("UTF-8")));

			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;

			// add to the router model
			for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
				routerModel.addManagedSystemElement((EthernetPort) logicalInterfParser.getMapElements().get(keyInterf));
			}

		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (SAXException e) {
			log.error(e.getMessage());
		}

	}

}
