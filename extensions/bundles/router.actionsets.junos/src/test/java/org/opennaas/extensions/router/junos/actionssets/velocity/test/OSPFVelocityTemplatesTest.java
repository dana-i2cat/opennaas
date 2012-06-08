package org.opennaas.extensions.router.junos.actionssets.velocity.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpoint;
import org.opennaas.extensions.router.model.OSPFService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;

/**
 * This class if for testing the ospf velocity templates to check input params and the output
 * 
 * @author Jordi Puig
 * 
 */
public class OSPFVelocityTemplatesTest extends VelocityTemplatesTest {

	private final Log	log	= LogFactory.getLog(OSPFVelocityTemplatesTest.class);

	@Test
	public void testAddOSPFInterfaceInAreaTemplate() throws Exception {

		template = "/VM_files/ospfAddInterfaceInArea.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());
		extraParams.put("elementName", "");
		extraParams.put("ipUtilsHelper", IPUtilsHelper.class);

		String message = callVelocity(template, getOSPFArea(), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<name>0.0.0.0</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.1</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.2</name>"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testClearOSPFTemplate() throws Exception {

		template = "/VM_files/ospfClear.vm";

		String message = callVelocity(template, getOSPFService(), new HashMap<String, Object>());

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<ospf operation =\"delete\">"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testConfigureOSPFTemplate() throws Exception {

		template = "/VM_files/ospfConfigure.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());

		String message = callVelocity(template, getOSPFService(), extraParams);
		Assert.assertNotNull(message);
		// TODO Use xpath to check xml tree is correct
		Assert.assertTrue(message.contains("10.11.12.13"));
		Assert.assertFalse(message.contains("<enable/>"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testConfigureOSPFTemplateWithoutRID() throws Exception {

		template = "/VM_files/ospfConfigure.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());

		OSPFService service = getOSPFService();
		service.setRouterID(null);

		String message = callVelocity(template, service, extraParams);
		Assert.assertNotNull(message);

		log.info(XmlHelper.formatXML(message));

		// TODO Use xpath to check xml tree is correct
		Assert.assertFalse("Rpc message must not change routing-options", message.contains("routing-options"));
		Assert.assertFalse("Rpc message must not change router-id", message.contains("router-id"));
		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testConfigureOSPFAreaTemplate() throws Exception {

		template = "/VM_files/ospfConfigureArea.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());
		extraParams.put("elementName", "");
		extraParams.put("ipUtilsHelper", IPUtilsHelper.class);

		String message = callVelocity(template, getOSPFAreaConfiguration(), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<name>0.0.0.0</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.1</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.2</name>"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testConfigureOSPFInterfaceStatusTemplate() throws Exception {

		template = "/VM_files/ospfConfigureInterfaceStatus.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());
		extraParams.put("elementName", "");
		extraParams.put("ipUtilsHelper", IPUtilsHelper.class);

		// Enable
		String message = callVelocity(template, getConfigureOSPFInterfaceStatusParameters(true), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<name>0.0.0.0</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.1</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.2</name>"));
		Assert.assertFalse(message.contains("<disable/>"));

		// Disable
		message = callVelocity(template, getConfigureOSPFInterfaceStatusParameters(false), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<name>0.0.0.0</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.1</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.2</name>"));
		Assert.assertTrue(message.contains("<disable/>"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testConfigureOSPFStatusTemplate() throws Exception {

		template = "/VM_files/ospfConfigureStatus.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());

		OSPFService service = new OSPFService();

		service.setEnabledState(EnabledState.ENABLED);
		String message1 = callVelocity(template, service, extraParams);
		Assert.assertNotNull(message1);
		Assert.assertFalse(message1.contains("<disable/>"));

		service.setEnabledState(EnabledState.DISABLED);
		String message2 = callVelocity(template, service, extraParams);
		Assert.assertTrue(message2.contains("<disable/>"));

		service.setEnabledState(EnabledState.ENABLED);
		String message3 = callVelocity(template, service, extraParams);
		Assert.assertFalse(message3.contains("<disable/>"));

		log.info(XmlHelper.formatXML(message1));
		log.info(XmlHelper.formatXML(message2));
		log.info(XmlHelper.formatXML(message3));
	}

	@Test
	public void testRemoveOSPFTemplate() throws Exception {

		template = "/VM_files/ospfClear.vm";

		OSPFService service = new OSPFService();
		service.setEnabledState(EnabledState.ENABLED);

		String message = callVelocity(template, service, new HashMap<String, Object>());
		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<ospf operation =\"delete\">"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testRemoveOSPFInterfaceInArea() throws Exception {

		template = "/VM_files/ospfRemoveInterfaceInArea.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enableState", EnabledState.ENABLED.toString());
		extraParams.put("ipUtilsHelper", IPUtilsHelper.class);
		extraParams.put("elementName", "");

		String message = callVelocity(template, getOSPFArea(), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<name>fe-0/0/2.1</name>"));
		Assert.assertTrue(message.contains("<name>fe-0/0/2.2</name>"));
		Assert.assertTrue(message.contains("<interface operation=\"delete\">"));

		log.info(XmlHelper.formatXML(message));
	}

	/**
	 * Get the OSPFAreaConfiguration
	 * 
	 * @return OSPFAreaConfiguration
	 * @throws IOException
	 */
	private OSPFAreaConfiguration getOSPFAreaConfiguration() throws IOException {

		// Add OSPFArea and areaId = 0.0.0.0
		OSPFAreaConfiguration ospfAreaConfiguration = new OSPFAreaConfiguration();
		ospfAreaConfiguration.setOSPFArea(getOSPFArea());

		return ospfAreaConfiguration;
	}

	/**
	 * Create a two OSPFProtocolEndpoint with the state like the state param
	 * 
	 * @return List<OSPFProtocolEndpoint>
	 * @throws IOException
	 */
	private OSPFService getConfigureOSPFInterfaceStatusParameters(Boolean state) throws IOException {

		OSPFService service = new OSPFService();

		OSPFAreaConfiguration areaConfig = new OSPFAreaConfiguration();
		service.addOSPFAreaConfiguration(areaConfig);

		// Add OSPFArea and areaId = 0.0.0.0
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(0);
		areaConfig.setOSPFArea(ospfArea);

		OSPFProtocolEndpoint endpoint1 = getOSPFProtocolEndpoint("fe-0/0/2", "1");
		OSPFProtocolEndpoint endpoint2 = getOSPFProtocolEndpoint("fe-0/0/2", "2");

		endpoint1.setEnabledState(state ? EnabledState.ENABLED : EnabledState.DISABLED);
		endpoint2.setEnabledState(state ? EnabledState.ENABLED : EnabledState.DISABLED);

		ospfArea.addEndpointInArea(endpoint1);
		ospfArea.addEndpointInArea(endpoint2);

		OSPFAreaConfiguration areaConfig2 = new OSPFAreaConfiguration();
		service.addOSPFAreaConfiguration(areaConfig2);

		// Add OSPFArea and areaId = 0.0.0.2
		OSPFArea ospfArea2 = new OSPFArea();
		ospfArea2.setAreaID(2);
		areaConfig2.setOSPFArea(ospfArea2);

		OSPFProtocolEndpoint endpoint3 = getOSPFProtocolEndpoint("fe-0/0/3", "1");

		endpoint3.setEnabledState(state ? EnabledState.ENABLED : EnabledState.DISABLED);

		ospfArea2.addEndpointInArea(endpoint3);

		return service;
	}

	/**
	 * Create a OSPFArea
	 * 
	 * @return OSPFArea
	 * @throws IOException
	 */
	private OSPFArea getOSPFArea() throws IOException {

		// Add OSPFArea and areaId = 0.0.0.0
		OSPFArea ospfArea = new OSPFArea();
		ospfArea.setAreaID(0);

		// Interface 1
		ospfArea.addEndpointInArea(getOSPFProtocolEndpoint("fe-0/0/2", "1"));
		// Interface 2
		ospfArea.addEndpointInArea(getOSPFProtocolEndpoint("fe-0/0/2", "2"));

		return ospfArea;
	}

	/**
	 * Create a OSPFProtocolEndpoint from the params
	 * 
	 * @param areaId
	 * @param portName
	 * @return OSPFProtocolEndpoint
	 * @throws IOException
	 */
	private OSPFProtocolEndpoint getOSPFProtocolEndpoint(String name, String number) throws IOException {

		OSPFProtocolEndpoint ospfProtocolEndpoint = new OSPFProtocolEndpoint();
		ospfProtocolEndpoint.setName(name + "." + number);
		ospfProtocolEndpoint.setEnabledState(EnabledState.ENABLED);

		return ospfProtocolEndpoint;
	}

	/**
	 * Create a OSPFService
	 * 
	 * @return
	 */
	private OSPFService getOSPFService() {
		// create interfaces
		EthernetPort port1 = new EthernetPort();
		port1.setName("fe-0/3/0");
		port1.setPortNumber(1);

		EthernetPort port2 = new EthernetPort();
		port2.setName("fe-0/3/0");
		port2.setPortNumber(2);

		EthernetPort port3 = new EthernetPort();
		port3.setName("fe-0/3/1");

		// create ospf config
		OSPFService service = new OSPFService();
		service.setRouterID("10.11.12.13");

		// 1st area
		OSPFAreaConfiguration areaConfig1 = new OSPFAreaConfiguration();
		OSPFArea area1 = new OSPFArea();
		area1.setAreaID(0l);
		areaConfig1.setOSPFArea(area1);

		OSPFProtocolEndpoint pep1 = new OSPFProtocolEndpoint();
		pep1.setEnabledState(EnabledState.ENABLED);
		OSPFProtocolEndpoint pep2 = new OSPFProtocolEndpoint();
		pep2.setEnabledState(EnabledState.DISABLED);

		pep1.addLogiaclPort(port1);
		pep2.addLogiaclPort(port2);

		area1.addEndpointInArea(pep1);
		area1.addEndpointInArea(pep2);
		service.addOSPFAreaConfiguration(areaConfig1);

		// 2nd area
		OSPFAreaConfiguration areaConfig2 = new OSPFAreaConfiguration();
		OSPFArea area2 = new OSPFArea();
		area2.setAreaID(1l);
		areaConfig2.setOSPFArea(area2);

		OSPFProtocolEndpoint pep3 = new OSPFProtocolEndpoint();
		pep3.setEnabledState(EnabledState.ENABLED);
		pep3.addLogiaclPort(port3);

		area2.addEndpointInArea(pep3);
		service.addOSPFAreaConfiguration(areaConfig2);

		return service;
	}

}
