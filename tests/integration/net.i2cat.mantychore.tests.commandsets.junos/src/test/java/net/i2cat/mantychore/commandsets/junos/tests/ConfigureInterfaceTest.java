package net.i2cat.mantychore.commandsets.junos.tests;

import net.i2cat.mantychore.commandsets.junos.commands.ConfigureSubInterfaceCommand;
import net.i2cat.mantychore.commandsets.junos.commands.DeleteSubInterfaceCommand;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.junit.Assert;
import org.junit.Test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class ConfigureInterfaceTest {
	Log			log					= LogFactory
													.getLog(ConfigureInterfaceTest.class);

	CommandTestsHelper	commandTestHelper	= new CommandTestsHelper(
													newSessionContextNetconf());

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");
		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		// ADDED
		return protocolSessionContext;

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
	public void configureEthernetInterface() {
		/* remove interface */
		removeSubInterface();

		log.info("Configure interfaces ethernet");
		ComputerSystem modelToUpdate = new ComputerSystem(); // IT WILL NOT BE
																// FILL UP, IT
																// DOES NOT NEED
																// IF WE
																// CONFIGURE

		try {
			commandTestHelper.prepareAndSendCommand(
					new ConfigureSubInterfaceCommand(),
					newParamsInterfaceEthernet(), modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
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

	// @Test
	public void configureEthernetInterfaceWithVLAN() {
		/* remove interface */
		removeSubInterface();

		log.info("Configure interfaces ethernet with vlan");
		ComputerSystem modelToUpdate = new ComputerSystem(); // IT WILL NOT BE
																// FILL UP, IT
																// DOES NOT NEED
																// IF WE CON
		try {
			commandTestHelper.prepareAndSendCommand(
					new ConfigureSubInterfaceCommand(),
					newParamsInterfaceEthernetWithVlan(), modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
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
	public void configureLogicalTunnel() {
		/* remove logical tunnel */
		removeLogicalTunnel();

		log.info("Configure logical tunnel");
		ComputerSystem modelToUpdate = new ComputerSystem(); // IT WILL NOT BE
																// FILL UP, IT
																// DOES NOT NEED
																// IF WE CON
		try {
			commandTestHelper.prepareAndSendCommand(
					new ConfigureSubInterfaceCommand(),
					newParamsInterfaceLogicalTunnel(), modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
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
		vlanEndpoint.setVlanID(12);
		logicalTunnel.addProtocolEndpoint(vlanEndpoint);
		return logicalTunnel;

	}

	@Test
	public void configureLogicalTunnelWithVLAN() {
		/* remove logical tunnel */
		removeLogicalTunnel();

		log.info("Configure logical tunnel with vlan");
		ComputerSystem modelToUpdate = new ComputerSystem(); // IT WILL NOT BE
																// FILL UP, IT
																// DOES NOT NEED
																// IF WE
																// CONFIGURE

		try {
			commandTestHelper.prepareAndSendCommand(
					new ConfigureSubInterfaceCommand(),
					newParamsInterfaceLogicalTunnelWithVlan(), modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}

	}

	public void removeSubInterface() {
		ComputerSystem modelToUpdate = new ComputerSystem();
		EthernetPort eth = new EthernetPort();
		eth.setElementName("fe-0/3/2");
		eth.setPortNumber(32);
		try {
			commandTestHelper.prepareAndSendCommand(new DeleteSubInterfaceCommand(), eth, modelToUpdate);

			log.info("OK : configured ge | fe with ethernet test!");

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public void removeLogicalTunnel() {
		ComputerSystem modelToUpdate = new ComputerSystem();
		LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
		logicalTunnel.setElementName("lt-0/1/2");
		logicalTunnel.setPortNumber(12);
		logicalTunnel.setPeer_unit(2);

		try {
			commandTestHelper.prepareAndSendCommand(new DeleteSubInterfaceCommand(), logicalTunnel, modelToUpdate);

			log.info("OK : configured ge | fe with ethernet test!");

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

}
