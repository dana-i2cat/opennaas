package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.nio.charset.Charset;

import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.google.common.io.Files;

public class IPInterfaceParserTest {
	private Log	log	= LogFactory.getLog(IPInterfaceParserTest.class);

	@Test
	public void testStatusParserTest() throws Exception {
		IPInterfaceParser parser = new IPInterfaceParser();

		String configFilePath = "/parsers/getconfig.xml";
		URI configFileURI = getClass().getResource(configFilePath).toURI();
		String message = Files.toString(new File(configFileURI), Charset.forName("UTF-8"));

		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

		log.info(parser.toPrint());
	}
}
