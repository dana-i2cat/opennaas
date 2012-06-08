package org.opennaas.extensions.router.junos.actionssets.velocity.test;

import java.io.IOException;
import java.util.Map;

import junit.framework.Assert;
import org.opennaas.extensions.router.junos.commandsets.velocity.VelocityEngine;
import org.opennaas.extensions.router.model.GRETunnelConfiguration;
import org.opennaas.extensions.router.model.GRETunnelEndpoint;
import org.opennaas.extensions.router.model.GRETunnelService;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class VelocityTemplatesTest {
	// This class if for testing the velocity templates
	// to check input params and the output
	Log						log			= LogFactory.getLog(VelocityTemplatesTest.class);
	private VelocityEngine	velocityEngine;
	protected String		template	= null;

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

	@Test
	public void testCreateGRETunnelTemplate() {
		template = "/VM_files/createTunnel.vm";
		String message = callGRETunnelVelocity(template, newParamsGRETunnelService());

		Assert.assertNotNull(message);
		log.info(message);
	}

	@Test
	public void testDeleteGRETunnelTemplate() {
		template = "/VM_files/deleteTunnel.vm";
		String message = callGRETunnelVelocity(template, newParamsGRETunnelService());

		Assert.assertNotNull(message);
		log.info(message);
	}

	private GRETunnelService newParamsGRETunnelService() {

		GRETunnelService greService = new GRETunnelService();
		greService.setElementName("");
		greService.setName("gre.3");

		GRETunnelConfiguration greConfig = new GRETunnelConfiguration();
		greConfig.setSourceAddress("147.56.89.62");
		greConfig.setDestinationAddress("193.45.23.1");

		GRETunnelEndpoint gE = new GRETunnelEndpoint();
		gE.setIPv4Address("192.168.32.1");
		gE.setSubnetMask("255.255.255.0");

		greService.setGRETunnelConfiguration(greConfig);
		greService.addProtocolEndpoint(gE);

		return greService;

	}

	public String callGRETunnelVelocity(String template, Object params) {
		String velocitycommand = null;

		velocityEngine.setParam(params);
		velocityEngine.addExtraParam("portNumber", 0);
		velocityEngine.addExtraParam("ipUtilsHelper", new IPUtilsHelper());
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

	protected String callVelocity(String template, Object params, Map<String, Object> extraParams) {
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

}
