package net.i2cat.mantychore.action.junos.test;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.commandsets.junos.velocity.VelocityEngine;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;

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

		String message = callVelocity(template, newParamsInterfaceLT(), ipUtilsHelper);
		Assert.assertNotNull(message);
		log.info(message);
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

	public String callVelocity(String template, Object params, Object ipUtilsHelper) {
		String velocitycommand = null;
		velocityEngine.setParam(params);
		velocityEngine.addExtraParam("ipUtilsHelper", ipUtilsHelper);
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

	public String callVelocity(String template, Object params) {
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

	public LogicalTunnelPort newParamsInterfaceLT() {
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

}
