package net.i2cat.mantychore.actionsets.junos.digester.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.i2cat.mantychore.commandsets.junos.digester.IPInterfaceParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class IPInterfaceParserTest {
	private Log	log	= LogFactory.getLog(IPInterfaceParserTest.class);

	@Test
	public void testStatusParserTest() throws Exception {
		IPInterfaceParser parser = new IPInterfaceParser();

		String message = readStringFromFile("/parsers/getconfig.xml");
		parser.init();
		parser.configurableParse(new ByteArrayInputStream(message.getBytes()));

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

}
