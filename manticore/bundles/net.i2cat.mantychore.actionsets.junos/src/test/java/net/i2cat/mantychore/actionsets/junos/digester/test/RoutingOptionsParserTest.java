package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.digester.RoutingOptionsParser;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.RouteCalculationService;
import net.i2cat.mantychore.model.Service;
import net.i2cat.mantychore.model.System;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.google.common.io.Files;

public class RoutingOptionsParserTest {

	private Log	log	= LogFactory.getLog(RoutingOptionsParserTest.class);

	@Test
	public void testParseRoutingOptions() throws Exception {

		String configFilePath = "/parsers/getConfigWithRoutingOptionsAndRouterId.xml";
		URI configFileURI = getClass().getResource(configFilePath).toURI();
		String message = Files.toString(new File(configFileURI), Charset.forName("UTF-8"));

		System model = createSampleModel();
		RoutingOptionsParser parser = new RoutingOptionsParser(model);
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		System updatedModel = parser.getModel();

		// check nextHopRoute is set
		Assert.assertFalse(updatedModel.getNextHopRoute().isEmpty());
		Assert.assertEquals(2, updatedModel.getNextHopRoute().size());

		// check routerId is set
		String routerId = null;
		for (Service service : updatedModel.getHostedService()) {
			if (service instanceof RouteCalculationService) {
				Assert.assertNotNull("Existing RouteCalculationServices have routerId setted up.", ((RouteCalculationService) service).getRouterID());
				if (routerId == null) {
					routerId = ((RouteCalculationService) service).getRouterID();
				} else {
					Assert.assertEquals("routerId is the same in all RouteCalculationServices", routerId,
							((RouteCalculationService) service).getRouterID());
				}
			}
		}
		log.info(parser.toPrint());
	}

	private System createSampleModel() {
		System model = new ComputerSystem();

		RouteCalculationService service = new OSPFService();
		model.addHostedService(service);
		model.addHostedService(new OSPFService());

		return model;
	}
}
