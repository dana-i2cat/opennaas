package net.i2cat.mantychore.commandsets.junos.commands;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.i2cat.mantychore.commandsets.junos.digester.InterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.RouterParser;
import net.i2cat.mantychore.models.router.RouterModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.protocol.api.ProtocolResponseMessage;

public class GetConfigurationCommand extends JunosCommand {

	public static final String	GETCONFIG	= "getConfiguration";

	public static final String	TEMPLATE	= "/getconfiguration.vm";

	public static final String	RULE		= "";

	/** The logger **/
	Logger						log			= LoggerFactory
													.getLogger(KeepAliveCommand.class);

	public GetConfigurationCommand() {
		super(GETCONFIG);

		this.setTemplate(TEMPLATE);

		log.debug("Preparing GetConfigurationCommand...");

	}

	@Override
	public void initializeCommand(IResourceModel modelInfo) throws CommandException {
		// This command doesn t need to include any model
	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {
		log.debug("Debugging response from getConfigurationCommand...");
		ProtocolResponseMessage message = (ProtocolResponseMessage) response;
		// String message.getMessage();
		RouterParser.setRouterModel((RouterModel) model);

		/* fill first parameters */
		digester.addObjectCreate("data/configuration", RouterParser.class);
		digester.addBeanPropertySetter("data/configuration/version", "hostname");
		digester.addBeanPropertySetter("data/configuration/system/host-name", "version");
		digester.addObjectCreate("data/configuration/interfaces/interface", InterfaceParser.class);
		digester.addSetNext("data/configuration/interfaces/interface", "addPhysicalInterface");
		/* Get interfaces information */

		InputStream input = new ByteArrayInputStream(message.getMessage().getBytes());

		try {
			digester.parse(input);
		} catch (IOException e) {
			log.error("Error IO in command getConfig", e.getMessage(), e);
			throw new CommandException(e.getMessage());
		} catch (SAXException e) {
			log.error("Error SAX Exception in command getConfig", e.getMessage(), e);
			throw new CommandException(e.getMessage());
		}

	}

}
