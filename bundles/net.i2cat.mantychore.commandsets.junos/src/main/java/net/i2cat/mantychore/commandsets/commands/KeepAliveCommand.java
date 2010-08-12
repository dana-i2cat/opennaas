package net.i2cat.mantychore.commandsets.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class KeepAliveCommand extends JunosCommand {
	public static final String	KEEP_ALIVE	= "keepAlive";
	public static final String	TEMPLATE	= "/keepalive.vm";

	/** The logger **/
	Logger						log			= LoggerFactory
													.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KeepAliveCommand.KEEP_ALIVE);
		this.setTemplate(TEMPLATE);
	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {

	}

}
