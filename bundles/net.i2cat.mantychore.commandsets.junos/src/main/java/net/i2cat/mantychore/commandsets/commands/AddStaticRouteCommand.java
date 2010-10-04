package net.i2cat.mantychore.commandsets.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import net.i2cat.mantychore.commandsets.digester.InterfaceParser;
import net.i2cat.mantychore.commandsets.digester.RouterParser;
import net.i2cat.mantychore.commandsets.digester.StaticRouteParser;
import net.i2cat.mantychore.models.router.RouterModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.protocol.api.ProtocolResponseMessage;

public class AddStaticRouteCommand extends JunosCommand {

	public static final String		ADDSTATICROUTE	= "addStaticRoute";

	public static final String		TEMPLATE		= "/addStaticRoute.vm";

	private HashMap<String, String>	params			= new HashMap<String, String>();

	/** The logger **/
	Logger							log				= LoggerFactory
															.getLogger(AddStaticRouteCommand.class);

	public AddStaticRouteCommand(String logicalRouterName,
			ArrayList<String> listStaticRoutingProtocol) {
		super(ADDSTATICROUTE);
		log.debug("Preparing GetConfigurationCommand...");
		this.setList(listStaticRoutingProtocol);
		this.setTemplate(TEMPLATE);

		params.put("logicalRouterName", logicalRouterName);

		// params.putAll(listStaticRoutingProtocol);

		this.setParams(params);

	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {
		log.debug("Debugging response from addStaticRoute...");
		ProtocolResponseMessage message = (ProtocolResponseMessage) response;
		// String message.getMessage();

		RouterParser.setRouterModel((RouterModel) model);

		// Digester digester = new Digester ();

		/* fill first parameters */
		digester
				.addObjectCreate(
						"data/configuration/logical-systems/routing-options/static/route",
						StaticRouteParser.class);
		digester
				.addBeanPropertySetter("data/configuration/version", "hostname");
		digester.addBeanPropertySetter("data/configuration/system/host-name",
				"version");
		digester.addObjectCreate("data/configuration/interfaces/interface",
				InterfaceParser.class);
		digester.addSetNext("data/configuration/interfaces/interface",
				"addPhysicalInterface");
		/* Get interfaces information */

		InputStream input = new ByteArrayInputStream(message.getMessage()
				.getBytes());

		try {
			digester.parse(input);
		} catch (IOException e) {
			log.error("Error IO in command getConfig", e.getMessage(), e);
			throw new CommandException(e.getMessage());
		} catch (SAXException e) {
			log.error("Error SAX Exception in command getConfig", e
					.getMessage(), e);
			throw new CommandException(e.getMessage());
		}

	}
}
