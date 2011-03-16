package net.i2cat.mantychore.actionsets.junos.actions;

import net.i2cat.mantychore.commandsets.junos.commands.CommitCommand;
import net.i2cat.mantychore.commons.Action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommitAction extends Action {

	// FIXME class with ANTION ID ( static strings)
	// public static final String COMMIT = "commit";

	Logger	logger	= LoggerFactory.getLogger(CommitAction.class);
	int		index	= 0;

	public CommitAction() {
		initialize();
	}

	protected void initialize() {
		commands.add(new CommitCommand());
	}
}