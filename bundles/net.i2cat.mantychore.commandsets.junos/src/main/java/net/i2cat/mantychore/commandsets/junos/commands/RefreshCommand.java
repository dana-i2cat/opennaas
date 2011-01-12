package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.IPConfigurationInterfaceParser;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.ManagedSystemElement;
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

	public void createCommand(Object params) {
		// the query does not need get config
		try {

			command = QueryFactory.newGet(netconfXML);

		} catch (ResourceNotFoundException e) {
			log.error(e.getMessage());
		} catch (ParseErrorException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void parseResponse(ManagedElement model) throws CommandException {

		try {
			/* Parse Physical interface info */
			// DigesterEngine physicalInterfParser = new
			// PhysicalInterfaceParser();
			// physicalInterfParser.configurableParse(response.getMessage());
			// HashMap<String, Object> interfs =
			// physicalInterfParser.getMapElements();

			HashMap<String, Object> interfs = new HashMap<String, Object>();
			/* Parse logical interface info */
			DigesterEngine logicalInterfParser = new IPConfigurationInterfaceParser();
			logicalInterfParser.init();

			/*
			 * parse a string to byte array, and it is sent to the logicalInterfaceParser
			 */
			log.debug(response.getMessage());
			ByteArrayInputStream bis = new ByteArrayInputStream(response.getMessage().getBytes("UTF-8"));
			System.out.println(response.getMessage());
			logicalInterfParser.configurableParse(bis);

			net.i2cat.mantychore.model.System routerModel = (net.i2cat.mantychore.model.System) model;
			HashMap<String, Object> interfaces = logicalInterfParser.getMapElements();

			// add to the router model

			for (String keyInterf : logicalInterfParser.getMapElements().keySet()) {
				routerModel.addManagedSystemElement((ManagedSystemElement) logicalInterfParser.getMapElements().get(keyInterf));
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
