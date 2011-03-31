package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.ConfigureSubInterfaceCommand;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SetInterfaceAction extends Action {

	Logger	logger	= LoggerFactory.getLogger(GetConfigurationAction.class);

	int		index	= 0;

	public SetInterfaceAction() {
		initialize();
	}

	protected void initialize() {
		commands.add(new ConfigureSubInterfaceCommand());
	}

}
