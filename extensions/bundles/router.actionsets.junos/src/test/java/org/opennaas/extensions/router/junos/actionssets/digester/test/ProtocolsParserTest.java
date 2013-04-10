package org.opennaas.extensions.router.junos.actionssets.digester.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.extensions.router.junos.commandsets.digester.ProtocolsParser;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.RouteCalculationService.AlgorithmType;
import org.opennaas.extensions.router.model.System;

public class ProtocolsParserTest {

	private Log	log	= LogFactory.getLog(ProtocolsParserTest.class);

	@Test
	public void testParseOSPFv3Protocol() throws Exception {

		String message = readStringFromFile("/parsers/getConfigWithOSPFv3.xml");

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());

		List<OSPFService> ospfServices = updatedModel.getAllHostedServicesByType(new OSPFService());
		Assert.assertEquals("There is only one OSPFService", 1, ospfServices.size());

		OSPFService ospfService = ospfServices.get(0);
		Assert.assertFalse("Service state must have been set", EnabledState.UNKNOWN.equals(ospfService.getEnabledState()));
		Assert.assertTrue("OSPF Service must be v3", ospfService.getAlgorithmType().equals(AlgorithmType.OSPFV3));

		Assert.assertFalse("Service must have (at least) an area configuration", ospfService.getOSPFAreaConfiguration().isEmpty());
		OSPFAreaConfiguration ospfAreaConfig = ospfService.getOSPFAreaConfiguration().get(0);

		Assert.assertNotNull(ospfAreaConfig.getOSPFArea());
		Assert.assertEquals(0l, ospfAreaConfig.getOSPFArea().getAreaID());

		Assert.assertFalse(ospfAreaConfig.getOSPFArea().getEndpointsInArea().isEmpty());
		int disabledInterfaceCount = 0;
		for (OSPFProtocolEndpointBase ospfEndPointBase : ospfAreaConfig.getOSPFArea().getEndpointsInArea()) {
			Assert.assertFalse("OSPFEndpoint state must have been set", EnabledState.UNKNOWN.equals(ospfEndPointBase.getEnabledState()));
			Assert.assertFalse("OSPFEndpoint must be implemented by an existing interface ", ospfEndPointBase.getLogicalPorts().isEmpty());

			if (ospfEndPointBase.getEnabledState().equals(EnabledState.DISABLED))
				disabledInterfaceCount++;

			// FIXME Unsupported! Right now OSPFEndpoints are not binded (see ProtocolsParser)
			// Assert.assertFalse("OSPFEndpoint must be binded to existing ProtocolEndpoints",
			// ospfEndPointBase.getBindedProtocolEndpoints().isEmpty());
			// for (ProtocolEndpoint pe : ospfEndPointBase.getBindedProtocolEndpoints()) {
			// Assert.assertTrue("OSPFEndpoint must be binded only to IPProtocolEndpoints",
			// pe instanceof IPProtocolEndpoint);
			// }
		}
		Assert.assertTrue("There is a disabled interface", disabledInterfaceCount > 0);
		Assert.assertTrue("Not all interfaces are disabled", disabledInterfaceCount < ospfAreaConfig.getOSPFArea().getEndpointsInArea().size());
	}

	@Test
	public void testParseOSPFProtocol() throws Exception {
		String message = readStringFromFile("/parsers/getConfigWithOSPF.xml");

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());

		List<OSPFService> ospfServices = updatedModel.getAllHostedServicesByType(new OSPFService());
		Assert.assertEquals("There is only one OSPFService", 1, ospfServices.size());

		OSPFService ospfService = ospfServices.get(0);
		Assert.assertFalse("Service state must have been set", EnabledState.UNKNOWN.equals(ospfService.getEnabledState()));

		Assert.assertFalse("Service must have (at least) an area configuration", ospfService.getOSPFAreaConfiguration().isEmpty());
		OSPFAreaConfiguration ospfAreaConfig = ospfService.getOSPFAreaConfiguration().get(0);

		Assert.assertNotNull(ospfAreaConfig.getOSPFArea());
		Assert.assertEquals(0l, ospfAreaConfig.getOSPFArea().getAreaID());

		Assert.assertFalse(ospfAreaConfig.getOSPFArea().getEndpointsInArea().isEmpty());
		int disabledInterfaceCount = 0;
		for (OSPFProtocolEndpointBase ospfEndPointBase : ospfAreaConfig.getOSPFArea().getEndpointsInArea()) {
			Assert.assertFalse("OSPFEndpoint state must have been set", EnabledState.UNKNOWN.equals(ospfEndPointBase.getEnabledState()));
			Assert.assertFalse("OSPFEndpoint must be implemented by an existing interface ", ospfEndPointBase.getLogicalPorts().isEmpty());

			if (ospfEndPointBase.getEnabledState().equals(EnabledState.DISABLED))
				disabledInterfaceCount++;

			// FIXME Unsupported! Right now OSPFEndpoints are not binded (see ProtocolsParser)
			// Assert.assertFalse("OSPFEndpoint must be binded to existing ProtocolEndpoints",
			// ospfEndPointBase.getBindedProtocolEndpoints().isEmpty());
			// for (ProtocolEndpoint pe : ospfEndPointBase.getBindedProtocolEndpoints()) {
			// Assert.assertTrue("OSPFEndpoint must be binded only to IPProtocolEndpoints",
			// pe instanceof IPProtocolEndpoint);
			// }
		}
		Assert.assertTrue("There is a disabled interface", disabledInterfaceCount > 0);
		Assert.assertTrue("Not all interfaces are disabled", disabledInterfaceCount < ospfAreaConfig.getOSPFArea().getEndpointsInArea().size());
	}

	@Test
	public void testParseEmptyOSPF() throws Exception {
		String message = readStringFromFile("/parsers/getConfigWithEmptyOSPF.xml");

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());
		List<OSPFService> ospfServices = updatedModel.getAllHostedServicesByType(new OSPFService());
		Assert.assertEquals(1, ospfServices.size());

		OSPFService ospfService = ospfServices.get(0);
		Assert.assertFalse("Service state must have been set", EnabledState.UNKNOWN.equals(ospfService.getEnabledState()));
	}

	@Test
	public void testParseDisabledEmptyOSPF() throws Exception {
		String message = readStringFromFile("/parsers/getConfigWithDisabledEmptyOSPF.xml");

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());
		List<OSPFService> ospfServices = updatedModel.getAllHostedServicesByType(new OSPFService());
		Assert.assertEquals(1, ospfServices.size());

		OSPFService ospfService = (OSPFService) ospfServices.get(0);
		Assert.assertTrue("Service state must have been set to DISABLED", EnabledState.DISABLED.equals(ospfService.getEnabledState()));
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

	private System createSampleModel() {
		System model = new ComputerSystem();

		// create interfaces to check OSPFEnpoint is created on them
		NetworkPort interface1 = new NetworkPort();
		interface1.setName("fe-0/3/0");
		interface1.setPortNumber(0);

		NetworkPort interface2 = new NetworkPort();
		interface2.setName("fe-0/3/1");
		interface2.setPortNumber(0);

		model.addLogicalDevice(interface1);
		model.addLogicalDevice(interface2);

		return model;
	}
}
