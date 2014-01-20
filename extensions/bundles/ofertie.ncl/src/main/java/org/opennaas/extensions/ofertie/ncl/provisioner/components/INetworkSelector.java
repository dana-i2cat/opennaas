package org.opennaas.extensions.ofertie.ncl.provisioner.components;

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

import org.opennaas.extensions.ofertie.ncl.provisioner.api.model.QosPolicyRequest;

public interface INetworkSelector {

	/**
	 * Finds a network able to allocate given qosPolicyRequest
	 * 
	 * @param flowRequest
	 * @return id of a network able to allocate given qosPolicyRequest
	 * @throws Exception
	 */
	public String findNetworkForRequest(QosPolicyRequest qosPolicyRequest) throws Exception;

	/**
	 * Finds the network containing a flow with given flowId
	 * 
	 * @param flowId
	 * @return id of the network containing a flow with given flowId
	 * @throws Exception
	 */
	public String findNetworkForFlowId(String flowId) throws Exception;

}
