package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.mantychore.commandsets.junos.digester.DigesterEngine;
import net.i2cat.mantychore.commandsets.junos.digester.LogicalInterfaceParser;
import net.i2cat.mantychore.commandsets.junos.digester.PhysicalInterfaceParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.capabilities.commandset.CommandException;
import com.iaasframework.capabilities.model.IResourceModel;

public class KeepAliveCommand extends JunosCommand {

	public static final String				KEEPALIVE	= "keepAlive";

	public static final String				TEMPLATE	= "/keepalive.vm";

	public static final DigesterEngine[]	DIGENGINES	= { new PhysicalInterfaceParser(), new LogicalInterfaceParser() };

	/** The logger **/
	Logger									log			= LoggerFactory
																.getLogger(KeepAliveCommand.class);

	public KeepAliveCommand() {
		super(KEEPALIVE, TEMPLATE, DIGENGINES);
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

}
