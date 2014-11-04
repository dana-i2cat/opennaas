package org.opennaas.extensions.openflowswitch.driver.ryu.offorwarding.actionssets;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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

import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.RyuProtocolSession;
import org.opennaas.extensions.openflowswitch.model.OpenflowSwitchModel;

/**
 * Ryu {@link Action}
 * 
 * @author Julio Carlos Barrera
 *
 */
public abstract class RyuAction extends Action {

	protected RyuProtocolSession getRyuProtocolSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		return (RyuProtocolSession) protocolSessionManager.obtainSessionByProtocol(RyuProtocolSession.RYU_PROTOCOL_TYPE, false);
	}

	protected String getSwitchIdFromSession(IProtocolSessionManager protocolSessionManager) throws ProtocolException {
		ProtocolSessionContext sessionContext = getRyuProtocolSession(protocolSessionManager).getSessionContext();
		return (String) sessionContext.getSessionParameters().get(RyuProtocolSession.SWITCHID_CONTEXT_PARAM_NAME);
	}

	protected OpenflowSwitchModel getModel()
	{
		return (OpenflowSwitchModel) getModelToUpdate();
	}

}
