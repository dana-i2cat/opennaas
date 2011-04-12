package net.i2cat.mantychore.commandsets.junos.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.netconf.rpc.QueryFactory;

public class CommitCommand extends JunosCommand {
	
	public static final String COMMIT = "commit";

	public static final String TEMPLATE = "/VM_files/commit.vm";

	/** The logger **/
	Logger log = LoggerFactory.getLogger(CommitCommand.class);


	
	public CommitCommand() {
		super(COMMIT);
	}

	@Override
	public Object sendQuery() {
		return QueryFactory.newCommit();
	}

	@Override
	public void parseResponse(Object arg0, Object arg1) throws CommandException {
		// TODO Auto-generated method stub

	}

}
