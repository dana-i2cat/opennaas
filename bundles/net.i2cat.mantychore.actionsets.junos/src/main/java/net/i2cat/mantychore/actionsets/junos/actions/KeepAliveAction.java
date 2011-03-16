package net.i2cat.mantychore.actionsets.junos.actions;

import java.util.List;

import net.i2cat.mantychore.commandsets.junos.commands.KeepAliveCommand;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveAction extends Action {

	// public static final String KEEPALIVE = "keepAlive";

	Logger					logger			= LoggerFactory.getLogger(KeepAliveAction.class);
	private List<String>	commandsList	= null;
	int						index			= 0;

	public KeepAliveAction() {
		initialize();
	}

	protected void initialize() {
		commands.add(new KeepAliveCommand());
	}

}