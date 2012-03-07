package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.digester.ProtocolsParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFProtocolEndpointBase;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.System;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.google.common.io.Files;

public class ProtocolsParserTest {

	private Log	log	= LogFactory.getLog(ProtocolsParserTest.class);

	@Test
	public void testParseOSPFProtocol() throws Exception {
		String configFilePath = "/parsers/getConfigWithOSPF.xml";
		URI configFileURI = getClass().getResource(configFilePath).toURI();
		String message = Files.toString(new File(configFileURI), Charset.forName("UTF-8"));

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());
		OSPFService ospfService = (OSPFService) updatedModel.getHostedService().get(0);
		Assert.assertFalse("Service state must have been set", EnabledState.UNKNOWN.equals(ospfService.getEnabledState()));

		Assert.assertFalse("Service must have (at least) an area configuration", ospfService.getOSPFAreaConfiguration().isEmpty());
		OSPFAreaConfiguration ospfAreaConfig = ospfService.getOSPFAreaConfiguration().get(0);

		Assert.assertNotNull(ospfAreaConfig.getOSPFArea());
		Assert.assertEquals(0l, ospfAreaConfig.getOSPFArea().getAreaID());

		Assert.assertFalse(ospfAreaConfig.getOSPFArea().getEndpointsInArea().isEmpty());
		int disabledInterfaceCount = 0;
		for (OSPFProtocolEndpointBase ospfEndPointBase : ospfAreaConfig.getOSPFArea().getEndpointsInArea()) {
			Assert.assertFalse("OSPFEndpoint state must have been set", EnabledState.UNKNOWN.equals(ospfEndPointBase.getEnabledState()));
			Assert.assertFalse("OSPFEndpoint must be implemented by an existing interface", ospfEndPointBase.getLogicalPorts().isEmpty());

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
		String configFilePath = "/parsers/getConfigWithEmptyOSPF.xml";
		URI configFileURI = getClass().getResource(configFilePath).toURI();
		String message = Files.toString(new File(configFileURI), Charset.forName("UTF-8"));

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());
		OSPFService ospfService = (OSPFService) updatedModel.getHostedService().get(0);
		Assert.assertFalse("Service state must have been set", EnabledState.UNKNOWN.equals(ospfService.getEnabledState()));
	}

	@Test
	public void testParseDisabledEmptyOSPF() throws Exception {
		String configFilePath = "/parsers/getConfigWithDisabledEmptyOSPF.xml";
		URI configFileURI = getClass().getResource(configFilePath).toURI();
		String message = Files.toString(new File(configFileURI), Charset.forName("UTF-8"));

		System model = createSampleModel();
		ProtocolsParser parser = new ProtocolsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		Assert.assertFalse(updatedModel.getHostedService().isEmpty());
		OSPFService ospfService = (OSPFService) updatedModel.getHostedService().get(0);
		Assert.assertTrue("Service state must have been set to DISABLED", EnabledState.DISABLED.equals(ospfService.getEnabledState()));
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
