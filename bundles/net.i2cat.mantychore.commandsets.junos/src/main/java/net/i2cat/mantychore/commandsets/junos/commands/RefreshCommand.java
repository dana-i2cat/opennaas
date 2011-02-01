package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.netconf.rpc.QueryFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshCommand extends JunosCommand {

	public static final String REFRESH = "refresh";
	public static final String TEMPLATE = "/VM_files/getconfiguration.vm";

	private String source = null;
	private String filter = null;
	private String attrFilter = null;

	/** logger **/
	Logger log = LoggerFactory.getLogger(JunosCommand.class);

	public RefreshCommand() {
		super(REFRESH, TEMPLATE);
	}

	public void initializeCommand(String source) {
		this.source = source;
	}

	@Override
	public Object message() {
		// TODO Auto-generated method stub
		return QueryFactory.newGetConfig(source, netconfXML, attrFilter);

	}

	@Override
	public void parseResponse(Object response, Object model) {

	}
}
