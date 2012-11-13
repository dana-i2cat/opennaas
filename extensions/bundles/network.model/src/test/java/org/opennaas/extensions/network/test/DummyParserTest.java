package org.opennaas.extensions.network.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	public void testNDLToJava() throws Exception {
		String path = new File(".").getCanonicalPath();
		System.out.println(path);
		String filePath = "src/test/resources/network_example1.xml";
		try {
			NetworkTopology exampleDescriptor = getNetworkDescriptor(filePath);
			log.info(exampleDescriptor.toString());
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}
	}

	@Test
	public void testJavaToNDL() throws IOException {
		try {
			String filePath = "target/test1.xml";
			NetworkTopology mockRDF = MockNetworkDescriptor.newSimpleNDLNetworkDescriptor();
			addNetworkDescriptor(mockRDF, filePath);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
		}

	}

	@Test
	public void testNDLToJavaWithDiffLayers() throws Exception {
		String filePath = "src/test/resources/network_diffs_layer.xml";
		try {
			NetworkTopology exampleDescriptor = getNetworkDescriptor(filePath);
			log.info(exampleDescriptor.toString());
		} catch (IOException e) {
			log.error(e.getMessage());
			throw e;
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
		FileInputStream inputStream = new FileInputStream(filename);
		NetworkTopology rd = loadNetworkDescriptor(inputStream);
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
