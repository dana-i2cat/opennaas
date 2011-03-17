package net.i2cat.mantychore.commandsets.junos.tests;


import java.util.ArrayList;

import org.junit.Assert;

import net.i2cat.mantychore.commandsets.junos.commands.CommitCommand;
import net.i2cat.mantychore.commandsets.junos.commands.CreateSubInterfaceCommand;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConfigureInterfaceTest {
	Logger			log			= LoggerFactory.getLogger(ConfigureInterfaceTest.class);
	
	CommandTestsHelper commandTestHelper = new CommandTestsHelper(newSessionContextNetconf());
	
	
	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL_URI, "mock://user:pass@host.net:2212/mocksubsystem");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL, "netconf");
		// ADDED
		return protocolSessionContext;

	}
	
	
	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernet() {
		EthernetPort eth = new EthernetPort();
		eth.setElementName("fe-0/3/2");
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.1");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		ArrayList params = new ArrayList();
		params.add(eth);
		return params;

	}
	
	@Test
	public void configureEthernetInterface () {
		log.info("Configure interfaces ethernet");
		ComputerSystem modelToUpdate = new ComputerSystem(); //IT WILL NOT BE FILL UP, IT DOES NOT NEED IF WE CONFIGURE
		
		try {
			commandTestHelper.prepareAndSendCommand(new CreateSubInterfaceCommand(),newParamsInterfaceEthernet(),modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	

	
	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsInterfaceEthernetWithVLAN() {
		EthernetPort eth = new EthernetPort();
		eth.setElementName("fe-0/3/2");
		eth.setPortNumber(2);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.32.2");
		ip.setSubnetMask("255.255.255.0");
		eth.addProtocolEndpoint(ip);
		ArrayList params = new ArrayList();
		params.add(eth);
		return params;

	}

	
//	@Test
	public void configureEthernetInterfaceWithVLAN () {
		log.info("Configure interfaces ethernet with vlan");
		ComputerSystem modelToUpdate = new ComputerSystem(); //IT WILL NOT BE FILL UP, IT DOES NOT NEED IF WE CON		
		try {
			commandTestHelper.prepareAndSendCommand(new CreateSubInterfaceCommand(),newParamsInterfaceEthernetWithVLAN(),modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
		
	}
	

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsLogicalTunnel() {
		//FIXME Where do we specify some encapsulation??
		EthernetPort logicalTunnel = new EthernetPort();
		logicalTunnel.setElementName("lt-1/2/0");
		logicalTunnel.setPortNumber(1); //FIXME is it specify the peer unit??
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.120.2");
		ip.setSubnetMask("255.255.255.0");
		logicalTunnel.addProtocolEndpoint(ip);
		ArrayList params = new ArrayList();
		params.add(logicalTunnel);
		return params;

	}

	
	
//	@Test
	public void configureLogicalTunnel () {
		log.info("Configure logical tunnel");
		ComputerSystem modelToUpdate = new ComputerSystem(); //IT WILL NOT BE FILL UP, IT DOES NOT NEED IF WE CON	
		try {
			commandTestHelper.prepareAndSendCommand(new CreateSubInterfaceCommand(),newParamsLogicalTunnel(),modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
	}
	

	/*
	 * test of an interface ethernet without vlan encapsulation
	 */
	private Object newParamsLogicalTunnelWithVLAN() {
		NetworkPort networkPort = new NetworkPort();
		networkPort.setElementName("lt-1/2/0");
		networkPort.setPortNumber(3);
		IPProtocolEndpoint ip = new IPProtocolEndpoint();
		ip.setIPv4Address("192.168.120.3");
		ip.setSubnetMask("255.255.255.0");
		networkPort.addProtocolEndpoint(ip);
		ArrayList params = new ArrayList();
		params.add(networkPort);
		return params;

	}

//	@Test	
	public void configureLogicalTunnelWithVLAN() {
		log.info("Configure logical tunnel with vlan");
		ComputerSystem modelToUpdate = new ComputerSystem(); //IT WILL NOT BE FILL UP, IT DOES NOT NEED IF WE CONFIGURE
		
		try {
			commandTestHelper.prepareAndSendCommand( new CreateSubInterfaceCommand(),newParamsLogicalTunnelWithVLAN(),modelToUpdate);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			Assert.fail(e.getMessage());
		}
		
	}
	
	

	

	

}
