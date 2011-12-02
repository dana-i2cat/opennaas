package org.opennaas.core.resources.tests.network.ndl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.xml.sax.SAXException;

public class DummyParserTest {
	Log								log					= LogFactory.getLog(DummyParserTest.class);
	
	
	@Test
	public void NDLToJavaTest ()  {
		//String filePath = "/home/carlos/opennaas/master/core/opennaas-core-resources/src/test/resources/network/network_example1.xml";
		try {
			RDF exampleDescriptor = getRDFDescriptor(filePath);
			log.info(exampleDescriptor.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(e.getMessage(),e.getCause());
		}
	}
	
	
	
	public void javaToNDLTest() {
		
	}
	
	
	
	/**
	 * Helper methods to test these functionality...
	 * 
	 * @param filename
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws ResourceException
	 * @throws SAXException
	 */
	private RDF getRDFDescriptor(String filename) throws JAXBException, IOException, ResourceException, SAXException {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			log.info("URL: " + url);
			stream = url.openStream();
		} catch (MalformedURLException ignore) {
			// Then try a file
			log.info("file: " + filename);
			stream = new FileInputStream(filename);
		}

		RDF rd = getDescriptor(stream);
		return rd;
	}
	
	private RDF getDescriptor(InputStream stream) throws JAXBException, SAXException {

		RDF descriptor = null;
		try {
			JAXBContext context = JAXBContext.newInstance(RDF.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* check wellformat xml with xsd */
			// TODO I CAN NOT UNDERSTAND WHY WE CAN GET THE LOADER FROM A COMMAND
			// SchemaFactory sf = SchemaFactory.newInstance(
			// javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// ClassLoader loader = Thread.currentThread().getContextClassLoader();
			// Schema schema = sf.newSchema(new StreamSource(loader.getResourceAsStream(NAME_SCHEMA)));
			// unmarshaller.setSchema(schema);

			descriptor = (RDF) unmarshaller.unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Ingore
			}
		}
		return descriptor;
	}

}
