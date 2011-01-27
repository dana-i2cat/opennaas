package net.i2cat.mantychore.commandsets.junos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class KeepAliveCommand extends JunosCommand {

	public static final String	KEEPALIVE	= "keepAlive";

	public static final String	TEMPLATE	= "/VM_files/keepalive.vm";

	/** The logger **/
	Logger						log			= LoggerFactory
																.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KEEPALIVE, TEMPLATE);
	}

	@Override
	public void parseResponse(IResourceModel arg0) throws CommandException {
		// the response doesn t include information
	}

	@Override
	public void initializeCommand(IResourceModel modelInfo) throws
			CommandException {
		// the command doesn t include query
	}

	@Override
	public void createCommand() {
		// It is not necessary to create any command
	}

}
