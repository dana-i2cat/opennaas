package org.opennaas.extensions.router.junos.commandsets.tests;

import org.opennaas.extensions.router.junos.commandsets.commands.CommitNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.EditNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.GetNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.commands.KeepAliveNetconfCommand;
import org.opennaas.extensions.router.junos.commandsets.velocity.VelocityEngine;
import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NetconfCommandTest {
	public static final String	CONFIGURESUBINTERFACE	= "CreateSubinterface";

	// for physical interfaces

	public static final String	TEMPLATE_ETHER			= "/VM_files/configureEthernet.vm";

	public static final String	REFRESH					= "refresh";

	public static final String	TEMPLATE_REFRESH		= "/VM_files/getconfiguration.vm";

	Log							log						= LogFactory.getLog(NetconfCommandTest.class);

	private VelocityEngine		velocityEngine;

	@Before
	public void init() {
		log.info("Initialing test");
		velocityEngine = new VelocityEngine();

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		System.out.println(eth.getLinkTechnology().toString());

		return eth;
	}

	@Test
	public void editNetconfCommandTest() {
		velocityEngine.setTemplate(TEMPLATE_ETHER);
		velocityEngine.setParam(newParamsInterfaceEthernet());

		NetworkPort networkPort = new NetworkPort();
		velocityEngine.addExtraParam("networkPort", networkPort);

		IPUtilsHelper ipUtilsHelper = new IPUtilsHelper();
		velocityEngine.addExtraParam("ipUtilsHelper", ipUtilsHelper);

		try {
			String template = velocityEngine.mergeTemplate();
			EditNetconfCommand editCommand = new EditNetconfCommand(template);

			log.info((editCommand.message()).toXML());

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void getNetconfCommandTest() {
		velocityEngine.setTemplate(TEMPLATE_REFRESH);
		velocityEngine.setParam(newParamsInterfaceEthernet());

		try {
			String template = velocityEngine.mergeTemplate();
			GetNetconfCommand getCommand = new GetNetconfCommand(template);
			log.info((getCommand.message()).toXML());

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}
	}

	@Test
	public void keepAliveCommandTest() {
		try {
			KeepAliveNetconfCommand keepAliveCommand = new KeepAliveNetconfCommand();
			log.info((keepAliveCommand.message()).toXML());

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

	@Test
	public void commitCommandTest() {
		try {
			CommitNetconfCommand commitCommand = new CommitNetconfCommand();
			log.info((commitCommand.message()).toXML());

		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

	}

}
