package org.opennaas.extensions.router.opener.actionssets.actions;

/*
 * #%L
 * OpenNaaS :: Router :: Opener ActionSet
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
import org.opennaas.extensions.router.capability.ip.IPActionSet;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint.ProtocolIFType;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceIPRequest;
import org.opennaas.extensions.router.opener.client.rpc.SetInterfaceResponse;
import org.opennaas.extensions.router.opener.client.rpc.Utils;

public class SetIPv4Action extends OpenerAction {

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager)
			throws ActionException {

		SetInterfaceIPRequest request = prepareRequest((NetworkPort) params);

		try {
			SetInterfaceResponse openerResponse = getOpenerProtocolSession(protocolSessionManager).getOpenerClientForUse().setInterfaceIPAddress(
					request, 0);
			ActionResponse response = actionResposeFromSetInterfaceResponse(openerResponse);
			response.setActionID(IPActionSet.SET_IPv4);
			return response;
		} catch (ProtocolException e) {
			throw new ActionException(e);
		} catch (Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public boolean checkParams(Object params) throws ActionException {
		if (params == null || !(params instanceof NetworkPort))
			throw new ActionException("Invalid parameters for action " + getActionID());

		if (((LogicalPort) params).getProtocolEndpoint().isEmpty())
			throw new ActionException("No IP address given to action " + getActionID());

		if (!(((LogicalPort) params).getProtocolEndpoint().get(0) instanceof IPProtocolEndpoint)) {
			throw new ActionException("No IP address given to action " + getActionID());
		}

		if (((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getProtocolIFType() == null ||
				!((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getProtocolIFType().equals(ProtocolIFType.IPV4)) {
			throw new ActionException("Invalid IP protocol type given to action " + getActionID() + ". Required " + ProtocolIFType.IPV4);
		}

		if (((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getIPv4Address() == null ||
				((IPProtocolEndpoint) (((LogicalPort) params).getProtocolEndpoint().get(0))).getIPv4Address().isEmpty()) {
			throw new ActionException("No IP address given to action " + getActionID());
		}

		return true;
	}

	private SetInterfaceIPRequest prepareRequest(NetworkPort subInterface) {

		IPProtocolEndpoint ipPep = (IPProtocolEndpoint) subInterface.getProtocolEndpoint().get(0);
		String subInterfaceName;
		if (subInterface.getPortNumber() == 0) {
			subInterfaceName = subInterface.getName();
		} else {
			subInterfaceName = subInterface.getName() + "." + subInterface.getPortNumber();
		}

		// assuming ipPep.getProtocolIFType().equals(ProtocolIFType.IPV4)
		String prefixLength = IPUtilsHelper.parseLongToShortIpv4NetMask(ipPep.getSubnetMask());

		return Utils.createSetInterfaceIPRequest(subInterfaceName, ipPep.getIPv4Address(), prefixLength);
	}

}
