package org.opennaas.extensions.roadm.wonesys.actionsets.actions;

import java.util.Vector;

import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.commandsets.commands.psroadm.SetChannel;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.opticalSwitch.FiberConnection;
import org.opennaas.extensions.router.model.opticalSwitch.WDMChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.ProteusOpticalSwitch;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.action.ActionResponse.STATUS;
import org.opennaas.core.resources.command.CommandException;
import org.opennaas.core.resources.command.Response;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

public class RemoveConnectionAction extends Action {

	public RemoveConnectionAction() {
		super();
		initialize();

	}

	protected void initialize() {
		this.setActionID(ActionConstants.REMOVECONNECTION);
	}

	@Override
	public boolean checkParams(Object arg0) throws ActionException {

		if (!(params instanceof FiberConnection)) {
			return false;
		}
		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		ActionResponse actionResponse = new ActionResponse();
		actionResponse.setActionID(this.actionID);
		/* get protocol */
		IProtocolSession protocol = null;
		try {
			protocol = protocolSessionManager.obtainSessionByProtocol("wonesys", false);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}

		/* check params */
		if (!checkParams(params))
			throw new ActionException("The Action " + getActionID() + " don't accept params");

		/* load params */
		FiberConnection connection = MakeConnectionAction.loadFiberConnectionFromRequest((FiberConnection) params,
				(ProteusOpticalSwitch) this.modelToUpdate);

		// check given connection exists
		// return error if not
		if (!connectionExists(connection, (ProteusOpticalSwitch) modelToUpdate)) {
			return ActionResponse.errorResponse("Could not remove connection. There is no such connection in model");
		} else {
			connection = getFiberConnection(connection, (ProteusOpticalSwitch) this.modelToUpdate);
		}

		CardDescription[] cardsToDesconfigure = getConnectionsToRemove(connection);

		/* remove cards */
		ProteusOpticalSwitch switchModel = (ProteusOpticalSwitch) this.modelToUpdate;
		for (CardDescription card : cardsToDesconfigure) {

			if (!switchModel.getCard(card.getChassis(), card.getSlot()).isPassive()) {

				/* read channel plan from the model */
				WDMChannelPlan channelPlan = (WDMChannelPlan) switchModel.getCard(card.getChassis(), card.getSlot()).getChannelPlan();
				int channelNum = channelPlan.getChannelNumberFromLambda(card.getLambda());

				SetChannel channel = new SetChannel(card.getChassis(), card.getSlot(), channelNum, 0);
				channel.initialize();
				String response;
				try {
					response = (String) protocol.sendReceive(channel.message());
					Response resp = channel.checkResponse(response);
					validateResponse(resp);
					actionResponse.addResponse(resp);
				} catch (ProtocolException e) {
					Vector<String> errors = new Vector<String>();
					errors.add(e.getMessage());
					Response.errorResponse((String) channel.message(), errors);
					// TODO Specify workflow if we don't use
					throw new ActionException(e);
				}
			}
		}

		/* updateModel */
		removeConnectionInModel(connection, switchModel);

		actionResponse.setStatus(STATUS.OK);

		return actionResponse;
	}

	public static boolean connectionExists(FiberConnection connection, ProteusOpticalSwitch switchModel) {
		return getFiberConnection(connection, switchModel) != null;
	}

	public static FiberConnection getFiberConnection(FiberConnection connection, ProteusOpticalSwitch switchModel) {

		FiberConnection matchingConnection = null;

		for (FiberConnection existentConnection : switchModel.getFiberConnections()) {
			if ( // same cards
			existentConnection.getSrcCard().getChasis() == connection.getSrcCard().getChasis() &&
					existentConnection.getSrcCard().getModuleNumber() == connection.getSrcCard().getModuleNumber() &&
					existentConnection.getDstCard().getChasis() == connection.getDstCard().getChasis() &&
					existentConnection.getDstCard().getModuleNumber() == connection.getDstCard().getModuleNumber() &&
					// same ports
					existentConnection.getSrcPort().getPortNumber() == connection.getSrcPort().getPortNumber() &&
					existentConnection.getDstPort().getPortNumber() == connection.getDstPort().getPortNumber() &&
					// same channels
					existentConnection.getSrcFiberChannel().getLambda() == connection.getSrcFiberChannel().getLambda() &&
					existentConnection.getDstFiberChannel().getLambda() == connection.getDstFiberChannel().getLambda()) {
				matchingConnection = existentConnection;
				break;
			}
		}

		return matchingConnection;
	}

	private void removeConnectionInModel(FiberConnection connection, ProteusOpticalSwitch switchModel) throws ActionException {

		WDMFCPort srcSubPort = MakeConnectionAction.getSubportWithLambda(connection.getSrcPort(), connection.getSrcFiberChannel().getLambda());
		WDMFCPort dstSubPort = MakeConnectionAction.getSubportWithLambda(connection.getDstPort(), connection.getDstFiberChannel().getLambda());

		// remove chain of deviceConnections and subports
		WDMFCPort subPort = srcSubPort;
		// FIXME a malformed route may lead to an endless loop!!!
		while (!subPort.equals(dstSubPort)) {

			// there is a port connected to subPort using same lambda
			WDMFCPort tmpSubPort = null;
			for (LogicalDevice port : subPort.getOutgoingDeviceConnections()) {
				if (port instanceof WDMFCPort) {
					if (((WDMFCPort) port).getDWDMChannel().getLambda() == subPort.getDWDMChannel().getLambda()) {
						tmpSubPort = (WDMFCPort) port;
						break;
					}
				}
			}

			if (tmpSubPort == null)
				throw new ActionException("");

			// remove subport from parent port
			subPort.getDevices().get(0).removePortOnDevice(subPort);
			// remove connection between subports
			subPort.removeDeviceConnection(tmpSubPort);

			subPort = tmpSubPort;
		}

		// remove subport from parent port
		dstSubPort.getDevices().get(0).removePortOnDevice(dstSubPort);

		switchModel.getFiberConnections().remove(connection);
	}

	private CardDescription[] getConnectionsToRemove(FiberConnection connection) {
		// TODO IT SHOULD BE IMPROVED TO CREATE PATHS AMONG DIFS. DEVICES TO REMOVE CONNECTIONS

		CardDescription srcCardDescription = new CardDescription();
		srcCardDescription.setChassis(connection.getSrcCard().getChasis());
		srcCardDescription.setLambda(connection.getSrcFiberChannel().getLambda());
		srcCardDescription.setSlot(connection.getSrcCard().getSlot());
		srcCardDescription.setPortNum(connection.getSrcPort().getPortNumber());

		CardDescription dstCardDescription = new CardDescription();
		dstCardDescription.setChassis(connection.getDstCard().getChasis());
		dstCardDescription.setLambda(connection.getDstFiberChannel().getLambda());
		dstCardDescription.setSlot(connection.getDstCard().getSlot());
		dstCardDescription.setPortNum(connection.getDstPort().getPortNumber());

		CardDescription[] cards = { srcCardDescription, dstCardDescription };
		return cards;
	}

	private void validateResponse(Response response) throws CommandException {

		if (response.getStatus().equals(Response.Status.ERROR)) {
			if (response.getErrors().size() > 0)
				throw new CommandException(response.getErrors().get(0));
			else
				throw new CommandException("Command Failed");
		}
	}

	public class CardDescription {
		int		chassis;
		int		slot;
		double	lambda;
		int		portNum;

		public double getLambda() {
			return lambda;
		}

		public void setLambda(double lambda) {
			this.lambda = lambda;
		}

		public int getChassis() {
			return chassis;
		}

		public void setChassis(int chassis) {
			this.chassis = chassis;
		}

		public int getSlot() {
			return slot;
		}

		public void setSlot(int slot) {
			this.slot = slot;
		}

		public int getPortNum() {
			return portNum;
		}

		public void setPortNum(int portNum) {
			this.portNum = portNum;
		}

	}

}
