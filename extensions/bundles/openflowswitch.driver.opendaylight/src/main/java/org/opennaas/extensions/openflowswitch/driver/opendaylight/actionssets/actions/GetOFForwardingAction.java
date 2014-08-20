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

import java.util.List;

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
public class GetOFForwardingAction extends OpenDaylightAction {

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
