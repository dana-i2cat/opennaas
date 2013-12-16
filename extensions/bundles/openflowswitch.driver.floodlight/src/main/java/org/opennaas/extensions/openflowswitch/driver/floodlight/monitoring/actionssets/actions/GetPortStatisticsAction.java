package org.opennaas.extensions.openflowswitch.driver.floodlight.monitoring.actionssets.actions;

import java.util.Map;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.monitoring.PortStatistics;
import org.opennaas.extensions.openflowswitch.driver.floodlight.offorwarding.actionssets.FloodlightAction;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.IFloodlightPortsStatisticsClient;
import org.opennaas.extensions.openflowswitch.driver.floodlight.protocol.portstatisticsclient.wrappers.SwitchStatisticsMap;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class GetPortStatisticsAction extends FloodlightAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {
			// obtain the switch ID from the protocol session
			String switchId = getSwitchIdFromSession(protocolSessionManager);

			// call Floodlight client
			IFloodlightPortsStatisticsClient client = getFloodlightProtocolSession(protocolSessionManager).getFloodlightPortsStatisticsClientForUse();
			SwitchStatisticsMap statistics = client.getPortsStatistics(switchId);

			// transform objects
			Map<String, Map<Integer, PortStatistics>> switchStatisticsMap = statistics.getSwitchStatisticsMap();
			Map<Integer, PortStatistics> portStatistics = switchStatisticsMap.get(switchId);

			// create action response with an object response
			ActionResponse response = new ActionResponse();
			response.setStatus(ActionResponse.STATUS.OK);
			response.setResult(portStatistics);

			return response;
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params != null) {
			throw new ActionException("Invalid parameters for action " + this.actionID);
		}
		return true;
	}

}
