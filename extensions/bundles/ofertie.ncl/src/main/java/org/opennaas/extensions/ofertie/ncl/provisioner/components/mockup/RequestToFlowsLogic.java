package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
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

import org.opennaas.extensions.ofertie.ncl.helpers.QoSPolicyRequestParser;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IPathFinder;
import org.opennaas.extensions.ofertie.ncl.provisioner.components.IRequestToFlowsLogic;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.NCLModel;
import org.opennaas.extensions.ofertie.ncl.provisioner.model.Route;
import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

public class RequestToFlowsLogic implements IRequestToFlowsLogic {

	public static final String	DEFAULT_FLOW_PRIORITY	= "32000";

	private NCLModel			nclModel;

	private IPathFinder			pathFinder;

	public NCLModel getNclModel() {
		return nclModel;
	}

	public void setNclModel(NCLModel nclModel) {
		this.nclModel = nclModel;
	}

	/**
	 * @return the pathFinder
	 */
	public IPathFinder getPathFinder() {
		return pathFinder;
	}

	/**
	 * @param pathFinder
	 *            the pathFinder to set
	 */
	public void setPathFinder(IPathFinder pathFinder) {
		this.pathFinder = pathFinder;
	}

	@Override
	public List<NetOFFlow> getRequiredFlowsToSatisfyRequest(QosPolicyRequest qosPolicyRequest) throws Exception {

		Route route = getPathFinder().findPathForRequest(qosPolicyRequest);
		List<NetOFFlow> flows = QoSPolicyRequestParser.parseFlowRequestIntoSDNFlow(qosPolicyRequest, route);

		return flows;
	}

}
