package net.i2cat.mantychore.commandsets.junos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.capabilities.protocol.ProtocolResponseMessage;

public class KeepAliveCommand extends JunosCommand {
	public static final String	KEEP_ALIVE	= "keepAlive";
	public static final String	TEMPLATE	= "resources/keepalive.vm";

	/** The logger **/
	Logger						logger		= LoggerFactory
													.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KeepAliveCommand.KEEP_ALIVE);
		this.setTemplate(TEMPLATE);
	}

	@Override
	public void parseResponse(IResourceModel model) throws CommandException {
		ProtocolResponseMessage message = (ProtocolResponseMessage) response;

	}

}
