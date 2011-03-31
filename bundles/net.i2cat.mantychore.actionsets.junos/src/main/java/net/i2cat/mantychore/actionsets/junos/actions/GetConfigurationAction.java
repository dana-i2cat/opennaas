package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.RefreshCommand;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetConfigurationAction extends Action {
	Logger	logger	= LoggerFactory.getLogger(GetConfigurationAction.class);
	int		index	= 0;

	public GetConfigurationAction() {
		initialize();
	}

	protected void initialize() {
		// FIXME IT IS NECESSARY TO PUT THE REFRESH NAME IN ANOTHER CLASS..
		commands.add(new RefreshCommand());
	}

}