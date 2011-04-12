package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.KeepAliveCommand;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeepAliveAction extends Action {

	Logger	logger	= LoggerFactory.getLogger(KeepAliveAction.class);
	int		index	= 0;

	public KeepAliveAction() {
		initialize();
	}

	protected void initialize() {
		commands.add(new KeepAliveCommand());
	}

}