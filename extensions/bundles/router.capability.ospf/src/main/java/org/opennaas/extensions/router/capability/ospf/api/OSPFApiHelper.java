package org.opennaas.extensions.router.capability.ospf.api;

/*
 * #%L
 * OpenNaaS :: Router :: OSPF capability
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;

public abstract class OSPFApiHelper {

	public static OSPFServiceWrapper buildOSPFServiceWrapper(OSPFService ospfService) {

		OSPFServiceWrapper ospfServiceWrapper = new OSPFServiceWrapper();
		Collection<OSPFAreaWrapper> ospfAreaWrappers = new ArrayList<OSPFAreaWrapper>();

		for (OSPFAreaConfiguration areaConfig : ospfService.getOSPFAreaConfiguration()) {

			OSPFAreaWrapper ospfAreaWrapper = buildOSPFAreaWrapper(areaConfig.getOSPFArea());

			ospfAreaWrappers.add(ospfAreaWrapper);
		}

		ospfServiceWrapper.setOspfArea(ospfAreaWrappers);

		return ospfServiceWrapper;

	}

	public static OSPFAreaWrapper buildOSPFAreaWrapper(OSPFArea ospfArea) {

		OSPFAreaWrapper ospfAreaWrapper = new OSPFAreaWrapper();
		ospfAreaWrapper.setAreaID((String.valueOf(ospfArea.getAreaID())));

		Collection<OSPFProtocolEndpointWrapper> ospfEndpointsWrapper = buildOSPFProtocolEndpointsWrapperCollection(ospfArea.getEndpointsInArea());
		ospfAreaWrapper.setOspfProtocolEndpoints(ospfEndpointsWrapper);

		return ospfAreaWrapper;
	}

	public static Collection<OSPFProtocolEndpointWrapper> buildOSPFProtocolEndpointsWrapperCollection(
			Collection<OSPFProtocolEndpointBase> protocolEndpointList) {

		List<OSPFProtocolEndpointWrapper> ospfEndpointsWrapper = new ArrayList<OSPFProtocolEndpointWrapper>();

		for (OSPFProtocolEndpointBase pE : protocolEndpointList) {

			OSPFProtocolEndpointWrapper endpointWrapper = buildOSPFProtocolEndpointWrapper(pE);

			ospfEndpointsWrapper.add(endpointWrapper);
		}

		return ospfEndpointsWrapper;

	}

	public static OSPFProtocolEndpointWrapper buildOSPFProtocolEndpointWrapper(OSPFProtocolEndpointBase pE) {

		OSPFProtocolEndpointWrapper endpointWrapper = new OSPFProtocolEndpointWrapper();

		endpointWrapper.setName(pE.getName());
		endpointWrapper.setState(pE.getEnabledState());

		return endpointWrapper;
	}
}
