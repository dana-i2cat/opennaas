package org.opennaas.extensions.opendaylight.vtn.actionssets.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.opendaylight.vtn.actionssets.OpenDaylightAction;
import org.opennaas.extensions.opendaylight.vtn.model.VTN;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.IOpenDaylightvtnAPIClient;

/**
 *
 * @author Josep Batallé (i2CAT)
 *
 */
public class CreateVTNAction extends OpenDaylightAction {

    private static final String FORWARDING_ACTION = "OUTPUT";//OpenDaylight requires capital letters

    Log log = LogFactory.getLog(CreateVTNAction.class);

    @Override
    public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
        try {
            VTN vtn = (VTN) params;
            log.error("Flow: " + vtn.getVtn_name());
            IOpenDaylightvtnAPIClient client = getOpenDaylightProtocolSession(protocolSessionManager).getOpenDaylightClientForUse();
            client.createVTN(vtn);

        } catch (Exception e) {
            //probably the flow is not defined correctly
            throw new ActionException(e);
        }

        return ActionResponse.okResponse(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
    }

    @Override
    public boolean checkParams(Object params) throws ActionException {

        if (params == null || !(params instanceof VTN)) {
            throw new ActionException("Invalid parameters for action " + this.actionID);
        }

        VTN vtn = (VTN) params;

        if (vtn.getVtn_name() == null || vtn.getVtn_name().isEmpty()) {
            throw new ActionException("No flow id given to params in action " + this.actionID);
        }

        return true;

    }
}
