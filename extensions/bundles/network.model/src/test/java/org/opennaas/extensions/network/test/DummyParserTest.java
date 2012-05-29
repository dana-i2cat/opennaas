package org.opennaas.extensions.network.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.mock.MockNetworkDescriptor;
import org.xml.sax.SAXException;

public class DummyParserTest {
	Log	log	= LogFactory.getLog(DummyParserTest.class);

	@Test
	public void NDLToJavaTest() {
		String filePath = "network/network_example1.xml";
		try {
			NetworkTopology exampleDescriptor = getNetworkDescriptor(filePath);
			log.info(exampleDescriptor.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		}
	}

	@Test
	public void javaToNDLTest() {
		try {
			String filePath = "target/test1.xml";
			NetworkTopology mockRDF = MockNetworkDescriptor.newSimpleNDLNetworkDescriptor();
			addNetworkDescriptor(mockRDF, filePath);
		} catch (IOException e) {
			log.error(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		}

	}

	@Test
	public void NDLToJavaTestWithDiffLayers() {
		String filePath = "network/network_diffs_layer.xml";

		try {
			NetworkTopology exampleDescriptor = getNetworkDescriptor(filePath);
			log.info(exampleDescriptor.toString());
		} catch (Exception e) {
			log.error(e.getMessage());
			log.error(e.getMessage(), e.getCause());
		}
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
	private NetworkTopology getNetworkDescriptor(String filename) throws JAXBException, IOException, ResourceException, SAXException {
		InputStream stream = null;
		// First try a URL
		try {
			URL url = new URL(filename);
			log.info("URL: " + url);
			stream = url.openStream();
		} catch (MalformedURLException ignore) {
			// Then try a file
			// Added class loader to read files
			stream = this.getClass().getClassLoader().getResourceAsStream(filename);
			log.error("file: " + filename);
			// stream = new FileInputStream(filename);
		}

		NetworkTopology rd = loadNetworkDescriptor(stream);
		return rd;
	}

	private NetworkTopology loadNetworkDescriptor(InputStream stream) throws JAXBException, SAXException {

		NetworkTopology descriptor = null;
		try {
			JAXBContext context = JAXBContext.newInstance(NetworkTopology.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			/* check wellformat xml with xsd */
			// TODO I CAN NOT UNDERSTAND WHY WE CAN GET THE LOADER FROM A COMMAND
			// SchemaFactory sf = SchemaFactory.newInstance(
			// javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
			// ClassLoader loader = Thread.currentThread().getContextClassLoader();
			// Schema schema = sf.newSchema(new StreamSource(loader.getResourceAsStream(NAME_SCHEMA)));
			// unmarshaller.setSchema(schema);

			descriptor = (NetworkTopology) unmarshaller.unmarshal(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// Ignore
			}
		}
		return descriptor;

	}

	private void addNetworkDescriptor(NetworkTopology networkDescriptor, String filename) throws IOException {

		OutputStream stream = new FileOutputStream(filename);
		saveNetworkDescriptor(networkDescriptor, stream);

	}

	public OutputStream saveNetworkDescriptor(NetworkTopology networkDescriptor, OutputStream stream) {

		try {
			JAXBContext context = JAXBContext.newInstance(NetworkTopology.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(networkDescriptor, stream);
		} catch (JAXBException e) {
			log.error(e.getMessage(), e.getCause());
		}
		return stream;

	}

}
