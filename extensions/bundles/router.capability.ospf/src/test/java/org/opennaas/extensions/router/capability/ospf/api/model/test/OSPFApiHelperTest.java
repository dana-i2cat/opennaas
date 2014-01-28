package org.opennaas.extensions.router.capability.ospf.api.model.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.extensions.router.capability.ospf.api.OSPFApiHelper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFAreaWrapper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFProtocolEndpointWrapper;
import org.opennaas.extensions.router.capability.ospf.api.OSPFServiceWrapper;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;

public class OSPFApiHelperTest {

	private final static String	ENDPOINT_1_NAME	= "endpoint1";
	private final static String	ENDPOINT_2_NAME	= "endpoint2";

	private final static String	OSPF_AREA_1		= "ospfArea1";
	private final static String	OSPF_AREA_2		= "ospfArea2";

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
		Assert.assertEquals(area.getName(), areaWrapper.getName());
		Assert.assertEquals(2, areaWrapper.getOspfProtocolEndpoints().size());
		Assert.assertEquals(area.getEndpointsInArea().size(), areaWrapper.getOspfProtocolEndpoints().size());
	}

	@Test
	public void buildOSPFServiceWrapperTest() {

		OSPFService service = new OSPFService();
		OSPFAreaConfiguration areaConfig1 = new OSPFAreaConfiguration();
		OSPFAreaConfiguration areaConfig2 = new OSPFAreaConfiguration();

		areaConfig1.setOSPFArea(generateOSPFArea(OSPF_AREA_1));
		areaConfig2.setOSPFArea(generateOSPFArea(OSPF_AREA_2));
		service.addOSPFAreaConfiguration(areaConfig1);
		service.addOSPFAreaConfiguration(areaConfig2);

		OSPFServiceWrapper serviceWrapper = OSPFApiHelper.buildOSPFServiceWrapper(service);

		Assert.assertEquals(2, serviceWrapper.getOspfAreas().size());
		Iterator<OSPFAreaWrapper> iterator = serviceWrapper.getOspfAreas().iterator();

		OSPFAreaWrapper areaWrapper1 = iterator.next();
		OSPFAreaWrapper areaWrapper2 = iterator.next();

		Assert.assertFalse(areaWrapper1.equals(areaWrapper2));

	}

	private OSPFArea generateOSPFArea(String name) {

		OSPFProtocolEndpointBase ospfEndpoint1 = generateOSPFEndpoint(ENDPOINT_1_NAME, EnabledState.ENABLED);
		OSPFProtocolEndpointBase ospfEndpoint2 = generateOSPFEndpoint(ENDPOINT_2_NAME, EnabledState.DISABLED);

		OSPFArea area = new OSPFArea();
		area.setName(name);
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
