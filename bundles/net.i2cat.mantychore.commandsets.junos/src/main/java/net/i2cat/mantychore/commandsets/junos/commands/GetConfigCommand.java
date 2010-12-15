package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.IOException;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.LogicalInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.PhysicalInterfaceParser;
import net.i2cat.mantychore.model.EthernetPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class GetConfigCommand extends JunosCommand {

	public static final String				GETCONFIG	= "getConfig";

	public static final String				TEMPLATE	= "/getconfiguration.vm";

	public static final DigesterEngine[]	DIGENGINES	= { new PhysicalInterfaceParser(), new LogicalInterfaceParser() };

	/** logger **/
	Logger									log			= LoggerFactory
																.getLogger(JunosCommand.class);

	public GetConfigCommand(String commandID) {
		super(GETCONFIG, TEMPLATE, DIGENGINES);
	}

	@Override
	public void initializeCommand(IResourceModel arg0) throws
			CommandException {
		// the query does not need get config

	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {

		try {
			/* Parse Physical interface info */
			DigesterEngine physicalInterfParser = digEngines[0];
			physicalInterfParser.configurableParse(response.getMessage());
			HashMap<String, Object> interfs = physicalInterfParser.getMapElements();

			/* Parse logical interface info */
			DigesterEngine logicalInterfParser = digEngines[1];
			logicalInterfParser.setMapElements(interfs);
			logicalInterfParser.configurableParse(response.getMessage());

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
