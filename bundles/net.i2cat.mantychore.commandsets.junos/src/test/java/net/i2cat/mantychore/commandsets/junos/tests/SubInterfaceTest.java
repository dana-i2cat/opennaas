package net.i2cat.mantychore.commandsets.junos.tests;

import net.i2cat.mantychore.commandsets.junos.commands.ConfigureSubInterfaceCommand;
import net.i2cat.mantychore.commandsets.junos.commands.VlanTaggingCommand;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.netconf.rpc.Query;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubInterfaceTest {
	private final Logger	log	= LoggerFactory
										.getLogger(SubInterfaceTest.class);

	private void testCommand(Command command, Object params)
			throws CommandException {
		command.setParams(params);
		command.initialize();
		log.info(((Query) command.message()).toXML());

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsVlanTagging() {
		EthernetPort eth = new EthernetPort();
		eth.setElementName("fe-0/3/2");
		return eth;

	}

	@Test
	public void vlanTaggingTest() {

		log.info("vlanTagging Test...");
		try {
			testCommand(new VlanTaggingCommand(), newParamsVlanTagging());

			log.info("OK : vlantagging test!");

		} catch (CommandException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceLogicalTunnel() {
		LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
		logicalTunnel.setElementName("lt-0/1/2");
		logicalTunnel.setPortNumber(12);
		logicalTunnel.setPeer_unit(2);
		logicalTunnel.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		logicalTunnel.addProtocolEndpoint(ip);
		return logicalTunnel;

	}

	@Test
	public void configureLTwithVlanTest() {
		log.info("lt interfaces with vlan tagging Test...");

		try {
			testCommand(new ConfigureSubInterfaceCommand(),
					newParamsInterfaceLogicalTunnel());

			log.info("OK : configured lt with vlan tagging test!");

		} catch (CommandException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceLogicalTunnelWithVlan() {
		LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
		logicalTunnel.setElementName("lt-0/1/2");
		logicalTunnel.setPortNumber(12);
		logicalTunnel.setPeer_unit(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		logicalTunnel.addProtocolEndpoint(ip);
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(1);
		logicalTunnel.addProtocolEndpoint(vlanEndpoint);
		return logicalTunnel;

	}

	@Test
	public void configureLTwithEthernetTest() {
		log.info("lt interfaces with vlan ethernet Test...");

		try {
			testCommand(new ConfigureSubInterfaceCommand(),
					newParamsInterfaceLogicalTunnelWithVlan());

			log.info("OK : configured lt with ethernet test!");

		} catch (CommandException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernetWithVlan() {
		EthernetPort eth = new EthernetPort();
		eth.setElementName("fe-0/3/2");
		eth.setPortNumber(32);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		VLANEndpoint vlanEndpoint = new VLANEndpoint();
		vlanEndpoint.setVlanID(1);
		eth.addProtocolEndpoint(vlanEndpoint);
		return eth;

	}

	@Test
	public void configureGEFEwithVlanTest() {
		log.info("ge | fe interfaces with vlan tagging Test...");
		try {
			testCommand(new ConfigureSubInterfaceCommand(),
					newParamsInterfaceEthernetWithVlan());

			log.info("OK : configured ge | fe with vlan test!");

		} catch (CommandException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setLinkTechnology(NetworkPort.LinkTechnology.ETHERNET);
		eth.setElementName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		System.out.println(eth.getLinkTechnology().toString());

		return eth;
	}

	@Test
	public void configureGEFEwithEthernetTest() {
		log.info("ge | fe interfaces with ethernet Test...");
		try {
			testCommand(new ConfigureSubInterfaceCommand(),
					newParamsInterfaceEthernet());

			log.info("OK : configured ge | fe with ethernet test!");

		} catch (CommandException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
