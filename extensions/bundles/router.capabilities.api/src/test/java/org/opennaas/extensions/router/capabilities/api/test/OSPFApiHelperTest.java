package org.opennaas.extensions.router.capabilities.api.test;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capabilities.api.helper.OSPFApiHelper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFAreaWrapper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFProtocolEndpointWrapper;
import org.opennaas.extensions.router.capabilities.api.model.ospf.OSPFServiceWrapper;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

public class OSPFApiHelperTest {

	private final static String	ENDPOINT_1_NAME	= "endpoint1";
	private final static String	ENDPOINT_2_NAME	= "endpoint2";

	private final static String	OSPF_AREA_1		= "0.0.0.0";
	private final static String	OSPF_AREA_2		= "10.10.0.0";

	private final static String	routerId		= "10.10.10.10";

	@Test
	public void buildOSPFProtocolEndpointWrapperTest() {

		OSPFProtocolEndpointBase ospfEndpoint = generateOSPFEndpoint(ENDPOINT_1_NAME, EnabledState.ENABLED);

		OSPFProtocolEndpointWrapper protocolEndpointWrapper = OSPFApiHelper.buildOSPFProtocolEndpointWrapper(ospfEndpoint);
		Assert.assertEquals(ENDPOINT_1_NAME, protocolEndpointWrapper.getName());
		Assert.assertEquals(EnabledState.ENABLED, protocolEndpointWrapper.getEnabledState());

	}

	@Test
	public void buildOSPFProtocolEndpointsWrapperCollectionTest() {

		Collection<OSPFProtocolEndpointBase> protocolEndpointList = new ArrayList<OSPFProtocolEndpointBase>();
		OSPFProtocolEndpointBase ospfEndpoint1 = generateOSPFEndpoint(ENDPOINT_1_NAME, EnabledState.ENABLED);
		OSPFProtocolEndpointBase ospfEndpoint2 = generateOSPFEndpoint(ENDPOINT_2_NAME, EnabledState.DISABLED);
		protocolEndpointList.add(ospfEndpoint1);
		protocolEndpointList.add(ospfEndpoint2);

		Collection<OSPFProtocolEndpointWrapper> endpointWrapperList = OSPFApiHelper.buildOSPFProtocolEndpointsWrapperCollection(protocolEndpointList);

		Assert.assertEquals(2, endpointWrapperList.size());

		Iterator<OSPFProtocolEndpointWrapper> endpointWrapperIterator = endpointWrapperList.iterator();
		OSPFProtocolEndpointWrapper endpointWrapper1 = endpointWrapperIterator.next();
		OSPFProtocolEndpointWrapper endpointWrapper2 = endpointWrapperIterator.next();

		Assert.assertTrue(endpointWrapper1.getName().equals(ENDPOINT_1_NAME) || endpointWrapper1.getName().equals(ENDPOINT_2_NAME));
		Assert.assertTrue(endpointWrapper2.getName().equals(ENDPOINT_1_NAME) || endpointWrapper2.getName().equals(ENDPOINT_2_NAME));
		Assert.assertFalse(endpointWrapper1.getName().equals(endpointWrapper2.getName()));

		Assert.assertTrue(endpointWrapper1.getEnabledState().equals(EnabledState.ENABLED) || endpointWrapper1.getEnabledState().equals(
				EnabledState.DISABLED));
		Assert.assertTrue(endpointWrapper2.getEnabledState().equals(EnabledState.ENABLED) || endpointWrapper2.getEnabledState().equals(
				EnabledState.DISABLED));
		Assert.assertFalse(endpointWrapper1.getEnabledState().equals(endpointWrapper2.getEnabledState()));
	}

	@Test
	public void buildOSPFAreaWrapperTest() {

		OSPFProtocolEndpointBase ospfEndpoint1 = generateOSPFEndpoint(ENDPOINT_1_NAME, EnabledState.ENABLED);
		OSPFProtocolEndpointBase ospfEndpoint2 = generateOSPFEndpoint(ENDPOINT_2_NAME, EnabledState.DISABLED);

		OSPFArea area = new OSPFArea();
		area.setName(OSPF_AREA_1);
		area.addEndpointInArea(ospfEndpoint1);
		area.addEndpointInArea(ospfEndpoint2);

		OSPFAreaWrapper areaWrapper = OSPFApiHelper.buildOSPFAreaWrapper(area);
		Assert.assertEquals(String.valueOf(area.getAreaID()), areaWrapper.getAreaID());
		Assert.assertEquals(2, areaWrapper.getOspfProtocolEndpoints().size());
		Assert.assertEquals(area.getEndpointsInArea().size(), areaWrapper.getOspfProtocolEndpoints().size());
	}

	@Test
	public void buildOSPFServiceWrapperTest() throws IOException {

		OSPFService service = new OSPFService();
		OSPFAreaConfiguration areaConfig1 = new OSPFAreaConfiguration();
		OSPFAreaConfiguration areaConfig2 = new OSPFAreaConfiguration();

		areaConfig1.setOSPFArea(generateOSPFArea(OSPF_AREA_1));
		areaConfig2.setOSPFArea(generateOSPFArea(OSPF_AREA_2));
		service.addOSPFAreaConfiguration(areaConfig1);
		service.addOSPFAreaConfiguration(areaConfig2);
		service.setEnabledState(EnabledState.ENABLED);
		service.setRouterID(routerId);

		OSPFServiceWrapper serviceWrapper = OSPFApiHelper.buildOSPFServiceWrapper(service);

		Assert.assertEquals(service.getEnabledState(), serviceWrapper.getEnabledState());
		Assert.assertEquals(EnabledState.ENABLED, serviceWrapper.getEnabledState());
		Assert.assertEquals(service.getRouterID(), serviceWrapper.getRouterId());
		Assert.assertEquals(routerId, serviceWrapper.getRouterId());

		Assert.assertEquals(2, serviceWrapper.getOspfAreas().size());
		Iterator<OSPFAreaWrapper> iterator = serviceWrapper.getOspfAreas().iterator();

		OSPFAreaWrapper areaWrapper1 = iterator.next();
		OSPFAreaWrapper areaWrapper2 = iterator.next();

		Assert.assertFalse(areaWrapper1.equals(areaWrapper2));

	}

	@Test
	public void buildOSPFServiceTest() {

		// without routerId

		OSPFServiceWrapper wrapper = new OSPFServiceWrapper();
		wrapper.setEnabledState(EnabledState.ENABLED);

		OSPFService ospfService = OSPFApiHelper.buildOSPFService(wrapper);

		Assert.assertNotNull(ospfService);
		Assert.assertNull(ospfService.getRouterID());

		Assert.assertNotNull(ospfService.getEnabledState());
		Assert.assertEquals(wrapper.getEnabledState(), ospfService.getEnabledState());
		Assert.assertEquals(EnabledState.ENABLED, ospfService.getEnabledState());

		// with routerId

		wrapper = new OSPFServiceWrapper();
		wrapper.setRouterId(routerId);
		wrapper.setEnabledState(EnabledState.ENABLED);

		ospfService = OSPFApiHelper.buildOSPFService(wrapper);

		Assert.assertNotNull(ospfService);
		Assert.assertNotNull(ospfService.getRouterID());
		Assert.assertEquals(wrapper.getRouterId(), ospfService.getRouterID());
		Assert.assertEquals(routerId, ospfService.getRouterID());

		Assert.assertNotNull(ospfService.getEnabledState());
		Assert.assertEquals(wrapper.getEnabledState(), ospfService.getEnabledState());
		Assert.assertEquals(EnabledState.ENABLED, ospfService.getEnabledState());

	}

	private OSPFArea generateOSPFArea(String areaId) throws IOException {

		OSPFProtocolEndpointBase ospfEndpoint1 = generateOSPFEndpoint(ENDPOINT_1_NAME, EnabledState.ENABLED);
		OSPFProtocolEndpointBase ospfEndpoint2 = generateOSPFEndpoint(ENDPOINT_2_NAME, EnabledState.DISABLED);

		OSPFArea area = new OSPFArea();

		area.setAreaID(IPUtilsHelper.ipv4StringToLong(areaId));
		area.addEndpointInArea(ospfEndpoint1);
		area.addEndpointInArea(ospfEndpoint2);

		return area;
	}

	private OSPFProtocolEndpointBase generateOSPFEndpoint(String endpointName, EnabledState enabled) {

		OSPFProtocolEndpointBase ospfEndpoint = new OSPFProtocolEndpointBase();
		ospfEndpoint.setName(endpointName);
		ospfEndpoint.setEnabledState(enabled);

		return ospfEndpoint;
	}
}
