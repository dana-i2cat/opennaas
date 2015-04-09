package org.opennaas.extensions.abno.capability.linkprovisioning.actionset.internal.actions;

/*
 * #%L
 * OpenNaaS :: XIFI ABNO
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

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.abno.capability.linkprovisioning.LinkProvisioningActionSet;
import org.opennaas.extensions.abno.capability.linkprovisioning.api.ProvisionLinkRequest;
import org.opennaas.extensions.abno.capability.linkprovisioning.protocol.ABNOProtocolSession;
import org.opennaas.extensions.abno.client.IABNOClient;
import org.opennaas.extensions.abno.client.model.ABNOResponse;

/**
 * Internal implementation of {@link LinkProvisioningActionSet#PROVISION_LINK} {@link Action} of {@link LinkProvisioningActionSet}
 * 
 * @author Julio Carlos Barrera
 *
 */
public class ProvisionLinkAction extends Action {

	private static final Log	log	= LogFactory.getLog(ProvisionLinkAction.class);

	public ProvisionLinkAction() {
		super();
		actionID = LinkProvisioningActionSet.PROVISION_LINK;
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (!(params instanceof ProvisionLinkRequest))
			throw new ActionException("Invalid parameters in action " + actionID + ". Expected: ProvisionLinkRequest");

		return true;
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {
		checkParams(params);

		ProvisionLinkRequest provisionLinkRequest = (ProvisionLinkRequest) params;

		// obtain client
		IABNOClient client;
		try {
			client = ((ABNOProtocolSession) protocolSessionManager.obtainSessionByProtocol(ABNOProtocolSession.ABNO_PROTOCOL_TYPE, false))
					.getABNOClientForUse();
		} catch (ProtocolException e) {
			String msg = "Unable to extract ANBOClient from ABNOProtocolSession";
			log.error(msg);
			throw new ActionException(msg, e);
		}

		// call client
		try {
			ABNOResponse response = client.createUnicastLink(provisionLinkRequest.getSrcRegion(), provisionLinkRequest.getDstRegion(),
					provisionLinkRequest.getSrcMACAddress(), provisionLinkRequest.getDstMACAddress(), provisionLinkRequest.getSrcInterface(),
					provisionLinkRequest.getDstInterface(), provisionLinkRequest.getOperation().toString(), provisionLinkRequest.getOperationType()
							.toString(), new Random().nextInt());
		} catch (Exception e) {
			String msg = "Error calling createUnicastLink from ABNOClient";
			log.error(msg);
			throw new ActionException(msg, e);
		}

		return ActionResponse.okResponse(actionID);
	}

}
