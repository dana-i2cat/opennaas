package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.IPProtocolEndpoint;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.ProtocolEndpoint;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.model.System;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class IPInterfaceParserTest {
	private final Log	log	= LogFactory.getLog(IPInterfaceParserTest.class);

	@Test
	public void testStatusParserTest() throws Exception {
		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfig.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		str += printGRETunnels(model);

		for (LogicalDevice device : model.getLogicalDevices()) {
			if (device instanceof EthernetPort) {
				EthernetPort port = (EthernetPort) device;
				Assert.assertNotNull("OperationalStatus must be set", port.getOperationalStatus());

				str += "- EthernetPort: " + '\n';
				str += port.getName() + '.' + port.getPortNumber() + '\n';
				str += port.getOperationalStatus();
				str += '\n';
				for (ProtocolEndpoint protocolEndpoint : port.getProtocolEndpoint()) {
					if (protocolEndpoint instanceof IPProtocolEndpoint) {
						IPProtocolEndpoint ipProtocol = (IPProtocolEndpoint)
								protocolEndpoint;
						str += "ipv4: " + ipProtocol.getIPv4Address() + '\n';
						str += "ipv6: " + ipProtocol.getIPv6Address() + '\n';
					}
				}

			}
			else {
				str += "not searched device";
			}

		}

		log.info(str);
	}

	@Test
	public void testGREIsNotCreatedIfNoUnitConfigurated() throws Exception {

		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfigWithoutGREUnit.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		List<GRETunnelService> greServices = model.getAllHostedServicesByType(new GRETunnelService());
		Assert.assertTrue("There should be no GREService if no gre unit is configured", greServices.isEmpty());

		log.info(str);
	}

	@Test
	public void testGREIsNotCreatedIfNoGRE() throws Exception {

		System model = createSampleModel();
		IPInterfaceParser parser = new IPInterfaceParser(model);

		String message = readStringFromFile("/parsers/getconfigWithoutGRE.xml");

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));
		String str = "\n";

		model = parser.getModel();

		List<GRETunnelService> greServices = model.getAllHostedServicesByType(new GRETunnelService());
		Assert.assertTrue("There should be no GREService if no gre is configured", greServices.isEmpty());

		log.info(str);
	}

	private String printGRETunnels(System model) {
		String str = "";
		int greCount = 0;
		for (Service service : model.getHostedService()) {
			if (service instanceof GRETunnelService) {
				greCount++;
				String name = ((GRETunnelService) service).getName();
				GRETunnelConfiguration gretunnelConfiguration = ((GRETunnelService) service).getGRETunnelConfiguration();
				str += " - GRE Tunnel Configuration : " + "\n";

				String source = gretunnelConfiguration.getSourceAddress();
				String destination = gretunnelConfiguration.getDestinationAddress();
				int key = gretunnelConfiguration.getKey();
				str += name + "\n";
				str += "key : " + String.valueOf(key) + "\n";
				str += "source : " + source + "\n";
				str += "destination : " + destination + "\n";

				int protocolEpCount = 0;
				for (ProtocolEndpoint pE : service.getProtocolEndpoint()) {
					if (pE instanceof GRETunnelEndpoint) {
						protocolEpCount++;
						GRETunnelEndpoint gE = (GRETunnelEndpoint) pE;
						String ip = gE.getIPv4Address();
						if (ip == null) {
							ip = gE.getIPv6Address();
							str += "ipv6 :" + ip + "\n";
						} else {
							str += "ipv4 : " + ip + "\n";
						}

					}
				}
				Assert.assertTrue(protocolEpCount > 0);
			}
			Assert.assertTrue("There is only one gre service (we know in config there's only one)", greCount == 1);
		}
		return str;
	}

	private System createSampleModel() {
		System model = new ComputerSystem();

		// create interfaces to check OSPFEnpoint is created on them
		EthernetPort interface1 = new EthernetPort();
		interface1.setName("fe-0/1/3");
		interface1.setPortNumber(0);
		IPProtocolEndpoint ipProtocolEndpoint = new IPProtocolEndpoint();
		ipProtocolEndpoint.setIPv4Address("192.168.1.3");
		ipProtocolEndpoint.setSubnetMask("255.255.255.0");
		interface1.addProtocolEndpoint(ipProtocolEndpoint);

		model.addLogicalDevice(interface1);

		return model;
	}

	/**
	 * Simple parser. It was used for proves with xml files
	 * 
	 * @param stream
	 * @return
	 */
	private String readStringFromFile(String pathFile) throws Exception {
		String answer = null;
		InputStream inputFile = getClass().getResourceAsStream(pathFile);
		InputStreamReader streamReader = new InputStreamReader(inputFile);
		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(streamReader);
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		answer = fileData.toString();

		return answer;
	}

}