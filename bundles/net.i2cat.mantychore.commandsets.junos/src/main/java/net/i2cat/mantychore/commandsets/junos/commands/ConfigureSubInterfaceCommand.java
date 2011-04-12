package net.i2cat.mantychore.commandsets.junos.commands;

import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.netconf.rpc.QueryFactory;

public class ConfigureSubInterfaceCommand extends JunosCommand {

	public static final String	CONFIGURESUBINTERFACE	= "CreateSubinterface";

	// for physical interfaces

	public static final String	TEMPLATE_ETHER			= "/VM_files/configureEthernet.vm";
	public static final String	TEMPLATE_LOGICALTUNNEL	= "/VM_files/configureLogicalTunnel.vm";

	/*
	 * This params have to define in the initialize process. The all necessary
	 * information is in the "params" variable.
	 */
	private final String		target					= "candidate";
	private final String		defaultOperation		= null;
	private final String		testOption				= null;
	private final String		errorOption				= null;

	public ConfigureSubInterfaceCommand() {
		super(CONFIGURESUBINTERFACE, TEMPLATE_ETHER);
	}

	@Override
	public Object sendQuery() {
		// returns the Query with the corresponding command
		return QueryFactory.newEditConfig(target, defaultOperation, testOption,
				errorOption, netconfXML);
	}

	@Override
	public void parseResponse(Object response, Object model) {
		// response is an RPCElement -->Reply
	}

	@Override
	public void initialize() throws CommandException {
		// Select template. It depends of what type of interface will configure

		if (params instanceof LogicalTunnelPort) {
			template = TEMPLATE_LOGICALTUNNEL;
		} else if (params instanceof EthernetPort) {
			template = TEMPLATE_ETHER;
		}
		super.setTemplate(template);
		NetworkPort networkPort = new NetworkPort();
		super.addExtraParams("networkPort", networkPort);

		IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		super.addExtraParams("ipUtilsHelper", ipUtilsHelper);

		super.initialize();

	}

}
