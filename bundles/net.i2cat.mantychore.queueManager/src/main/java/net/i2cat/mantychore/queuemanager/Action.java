package net.i2cat.mantychore.queuemanager;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.queuemanager.mock.MockProtocolWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.protocolsessionmanager.IProtocolSession;
import com.iaasframework.protocolsessionmanager.ProtocolException;

public class Action {

	Logger					log			= LoggerFactory
												.getLogger(Action.class);

	private String			protocolId;
	private String			resourceId;
	private List<Command>	commands	= new ArrayList<Command>();
	private Command			prepare, commit;

	public Action() {

	}

	public void execute() {

		MockProtocolWrapper protocolWrapper = new MockProtocolWrapper();
		IProtocolSession protocol = protocolWrapper.getProtocolSession(resourceId, protocolId);

		try {
			sendCommandToProtocol(prepare, protocol);
		} catch (ProtocolException e1) {
			e1.printStackTrace();
			log.error(e1.getMessage());

		}

		for (Command command : commands) {
			command.initialize();
			try {
				sendCommandToProtocol(command, protocol);
			} catch (ProtocolException e) {
				// TODO IT CAN NOT WORK
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());

			}
		}

		try {
			sendCommandToProtocol(commit, protocol);
		} catch (ProtocolException e) {
			// TODO IT CAN NOT WORK
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

	public void sendCommandToProtocol(Command command, IProtocolSession protocol) throws ProtocolException {
		command.parseResponse(protocol.sendReceive(command.message()));
	}

	public String getNetconfId() {
		return protocolId;
	}

	public void setNetconfId(String netconfId) {
		this.protocolId = netconfId;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public List<Command> getCommands() {
		return commands;
	}

	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}

	public Command getPrepare() {
		return prepare;
	}

	public void setPrepare(Command prepare) {
		this.prepare = prepare;
	}

	public Command getCommit() {
		return commit;
	}

	public void setCommit(Command commit) {
		this.commit = commit;
	}

}
