package net.i2cat.mantychore.actionsets.junos.velocity.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;
import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFProtocolEndpoint;
import net.i2cat.mantychore.model.OSPFService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;

public class VelocityTemplatesTest {
	// This class if for testing the velocity templates
	// to check input params and the output
	Log						log			= LogFactory.getLog(VelocityTemplatesTest.class);
	private VelocityEngine	velocityEngine;
	private String			template	= null;

	@Before
	public void init() {
		log.info("Initialing test");
		velocityEngine = new VelocityEngine();
	}

	@Test
	public void testGetConfigurationTemplate() {
		template = "/VM_files/getconfiguration.vm";
		String message = callVelocity(template, null);
		Assert.assertNotNull(message);
		// TODO implements a method to check the message is well form
		log.info(message);
	}

	@Test
	public void testsetIpv4Template() {
		template = "/VM_files/configureIPv4.vm";
		IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("ipUtilsHelper", ipUtilsHelper);

		String message = callVelocity(template, newParamsInterfaceLT(), extraParams);
		Assert.assertNotNull(message);
		log.info(message);
	}

	@Test
	public void testConfigureOSPFTemplate() throws Exception {
		template = "/VM_files/ospfConfigure.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED);
		extraParams.put("enabledState", EnabledState.ENABLED);
		IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		extraParams.put("ipUtilsHelper", ipUtilsHelper);

		String message = callVelocity(template, createOSPFService(), extraParams);
		Assert.assertNotNull(message);
		// TODO Use xpath to check xml tree is correct
		Assert.assertTrue(message.contains("10.11.12.13"));
		Assert.assertFalse(message.contains("<enable/>"));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testIPUtilsHelper() throws IOException {
		String ip1 = "10.11.12.13";
		String ip2 = "0.0.0.0";
		String ip3 = "255.255.255.255";

		String ip1gen = IPUtilsHelper.ipv4LongToString(IPUtilsHelper.ipv4StringToLong(ip1));
		String ip2gen = IPUtilsHelper.ipv4LongToString(IPUtilsHelper.ipv4StringToLong(ip2));
		String ip3gen = IPUtilsHelper.ipv4LongToString(IPUtilsHelper.ipv4StringToLong(ip3));

		Assert.assertEquals(ip1, ip1gen);
		Assert.assertEquals(ip2, ip2gen);
		Assert.assertEquals(ip3, ip3gen);
	}

	private String callVelocity(String template, Object params, Map<String, Object> extraParams) {
		String velocitycommand = null;
		velocityEngine.setParam(params);
		for (String name : extraParams.keySet())
			velocityEngine.addExtraParam(name, extraParams.get(name));
		velocityEngine.setTemplate(template);
		try {
			velocitycommand = velocityEngine.mergeTemplate();
		} catch (ResourceNotFoundException e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		} catch (ParseErrorException e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		}
		return velocitycommand;
	}

	private String callVelocity(String template, Object params) {
		String velocitycommand = null;
		velocityEngine.setParam(params);
		velocityEngine.setTemplate(template);
		try {
			velocitycommand = velocityEngine.mergeTemplate();
		} catch (ResourceNotFoundException e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		} catch (ParseErrorException e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			Assert.fail();
		}
		return velocitycommand;
	}

	private LogicalTunnelPort newParamsInterfaceLT() {
		LogicalTunnelPort ltp = new LogicalTunnelPort();
		ltp.setElementName("");
		ltp.setLinkTechnology(NetworkPort.LinkTechnology.OTHER);
		ltp.setName("lt-0/3/2");
		ltp.setPeer_unit(101);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		ltp.addProtocolEndpoint(ip);
		return ltp;
	}

	private OSPFService createOSPFService() {
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
