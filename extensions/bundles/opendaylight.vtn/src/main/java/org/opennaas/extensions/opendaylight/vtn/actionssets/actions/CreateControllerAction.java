package org.opennaas.extensions.opendaylight.vtn.actionssets.actions;


import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.extensions.opendaylight.vtn.actionssets.OpenDaylightAction;
import org.opennaas.extensions.openflowswitch.capability.OpenflowForwardingActionSet;
import org.opennaas.extensions.opendaylight.vtn.model.OpenDaylightController;
import org.opennaas.extensions.opendaylight.vtn.protocol.client.IOpenDaylightvtnAPIClient;

public class CreateControllerAction extends OpenDaylightAction {

    @Override
    public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
        try {
            OpenDaylightController controller = (OpenDaylightController) params;
            log.error("Flow: " + controller.getController_id());
            IOpenDaylightvtnAPIClient client = getOpenDaylightProtocolSession(protocolSessionManager).getOpenDaylightClientForUse();
            client.createController(controller);

        } catch (Exception e) {
            //probably the flow is not defined correctly
            throw new ActionException(e);
        }

        return ActionResponse.okResponse(OpenflowForwardingActionSet.CREATEOFFORWARDINGRULE);
    }

    @Override
    public boolean checkParams(Object params) throws ActionException {

        if (params == null || !(params instanceof OpenDaylightController)) {
            throw new ActionException("Invalid parameters for action " + this.actionID);
        }

        OpenDaylightController controller = (OpenDaylightController) params;

        if (controller.getController_id() == null || controller.getController_id().isEmpty()) {
            throw new ActionException("No flow id given to params in action " + this.actionID);
        }

        return true;

    }

}
