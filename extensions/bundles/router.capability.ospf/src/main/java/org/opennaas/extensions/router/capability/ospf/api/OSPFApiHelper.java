package org.opennaas.extensions.router.capability.ospf.api;

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
		ospfAreaWrapper.setName(ospfArea.getName());

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
