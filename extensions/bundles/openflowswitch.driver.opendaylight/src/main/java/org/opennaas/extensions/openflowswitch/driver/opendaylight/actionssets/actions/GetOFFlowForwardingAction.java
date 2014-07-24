package org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.actions;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.OpenDaylightAction;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author Josep Batallé Oronich
 *
 */
public class GetOFFlowForwardingAction extends OpenDaylightAction {

    @Override
    public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
        OpenDaylightOFFlow flow;
        try {
            // obtain the switch ID from the protocol session
            String switchId = getSwitchIdFromSession(protocolSessionManager);
            String[] receivedParams = (String[]) params;
            String DPID = receivedParams[0];
            String name = receivedParams[1];

            IOpenDaylightStaticFlowPusherClient client = getOpenDaylightProtocolSession(protocolSessionManager).getOpenDaylightClientForUse();
            flow = client.getFlow(DPID, name);

        } catch (Exception e) {
            throw new ActionException(e);
        }
        ActionResponse response = new ActionResponse();
        response.setStatus(ActionResponse.STATUS.OK);
        response.setResult(flow);

        return response;
    }

    @Override
    public boolean checkParams(Object params) throws ActionException {
        if (params != null) {
            throw new ActionException("Invalid parameters for action " + this.actionID);
        }

        return true;
    }

}
