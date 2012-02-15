package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

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

public class RoutingOptionsParserTest {

	private Log	log	= LogFactory.getLog(IPInterfaceParserTest.class);

	@Test
	public void testParseRoutingOptions() throws Exception {

		String message = readStringFromFile("/parsers/getConfigWithRoutingOptionsAndRouterId.xml");

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

		RouteCalculationService service = new OSPFService();
		model.addHostedService(service);
		model.addHostedService(new OSPFService());

		return model;
	}
}
