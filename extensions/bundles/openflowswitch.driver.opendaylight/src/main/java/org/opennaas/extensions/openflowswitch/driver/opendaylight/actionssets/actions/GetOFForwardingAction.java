package org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.actions;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.OpenDaylightAction;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author Josep Batallé Oronich
 * @author Julio Carlos Barrera
 *
 */
public class GetOFForwardingAction extends OpenDaylightAction {

    Log log = LogFactory.getLog(GetOFForwardingAction.class);

    @Override
    public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
        List<OpenDaylightOFFlow> flows;
        try {
            // obtain the switch ID from the protocol session
            String switchId = getSwitchIdFromSession(protocolSessionManager);
            IOpenDaylightStaticFlowPusherClient client = getOpenDaylightProtocolSession(protocolSessionManager).getOpenDaylightClientForUse();
            flows = client.getFlows(switchId);

        } catch (Exception e) {
            throw new ActionException(e);
        }
        ActionResponse response = new ActionResponse();
        response.setStatus(ActionResponse.STATUS.OK);
        response.setResult(flows);

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
