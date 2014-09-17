package org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: OpenDaylight
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.openflowswitch.capability.offorwarding.OpenflowForwardingActionSet;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.actionssets.OpenDaylightAction;
import org.opennaas.extensions.openflowswitch.driver.opendaylight.protocol.client.IOpenDaylightStaticFlowPusherClient;
import org.opennaas.extensions.openflowswitch.model.OpenDaylightOFFlow;

/**
 *
 * @author Josep Batallé Oronich
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
public class RemoveOFForwardingAction extends OpenDaylightAction {

    @Override
    public boolean checkParams(Object params) throws ActionException {

        if (params == null || !(params instanceof String[])) {
            throw new ActionException("Invalid parameters for action " + OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);
        }

        return true;
    }

    @Override
    public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
        String[] receivedParams = (String[]) params;
        String DPID = receivedParams[0];
        String name = receivedParams[1];
        IOpenDaylightStaticFlowPusherClient client;
        String switchId;

        try {
            client = getOpenDaylightProtocolSession(protocolSessionManager).getOpenDaylightClientForUse();
            switchId = getSwitchIdFromSession(protocolSessionManager);
            OpenDaylightOFFlow flow = getFlowFromSwitchByName(name, switchId, client);
            client.deleteFlow(DPID, name);
        } catch (Exception e) {
            log.error("Remove Error flow. DPID: " + DPID + " Name: " + name);
            throw new ActionException(e);
        }

        return ActionResponse.okResponse(OpenflowForwardingActionSet.REMOVEOFFORWARDINGRULE);
    }

    /**
     *
     * @param flowName
     * @param switchId
     * @param client
     * @return existing flow with given flowName in switch
     * @throws Exception
     * @throws ActionException if there is no flow with given flowName in switch
     * @throws ProtocolException
     */
    private OpenDaylightOFFlow getFlowFromSwitchByName(String flowName, String switchId, IOpenDaylightStaticFlowPusherClient client)
            throws ProtocolException, ActionException, Exception {
        for (OpenDaylightOFFlow flow : client.getFlows(switchId)) {
            if (flow.getName().equals(flowName)) {
                return flow;
            }
        }
        throw new ActionException("Given flow does not exist: " + flowName);
    }
}
