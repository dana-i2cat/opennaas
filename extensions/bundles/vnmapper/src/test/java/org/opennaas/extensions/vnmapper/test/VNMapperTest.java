package org.opennaas.extensions.vnmapper.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.extensions.network.repository.NetworkMapperDescriptorToModel;
import org.opennaas.extensions.vnmapper.Global;
import org.opennaas.extensions.vnmapper.InPNetwork;
import org.opennaas.extensions.vnmapper.MappingResult;
import org.opennaas.extensions.vnmapper.VNTRequest;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMapperInput;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMapperOutput;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMappingCapability;
import org.xml.sax.SAXException;

public class VNMapperTest {

	private final static String	TOPOLOGY_FILE	= "topology.xml";
	private final static String	REQUEST_FILE	= "request.xml";
	private final static String	RESULT_FILE		= "output.karaf";

	private final static String	SAMPLE_1_URL	= "/samples/sample1/";
	private final static String	SAMPLE_2_URL	= "/samples/sample2/";
	private final static String	SAMPLE_3_URL	= "/samples/sample3/";
	private final static String	SAMPLE_4_URL	= "/samples/sample4/";
	private final static String	SAMPLE_5_URL	= "/samples/sample5/";
	private final static String	SAMPLE_6_URL	= "/samples/sample6/";

	private VNMappingCapability	capab;

	class TestInput {
		InPNetwork	net;
		VNTRequest	vnt;
		String		expectedOutput;
	}

	@Before
	public void setUp() {

		String resourceId = "1234";

		Information capabilityInformation = new Information();
		capabilityInformation.setType(VNMappingCapability.CAPABILITY_TYPE);

		CapabilityDescriptor descriptor = new CapabilityDescriptor();
		descriptor.setCapabilityInformation(capabilityInformation);
		capab = new VNMappingCapability(descriptor, resourceId);

		Global.pathChoice = 1;
		Global.maxPathLinksNum = 5;
		Global.stepsMax = 100;
	}

	@Test
	public void sample1Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_1_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void sample2Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_2_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void sample3Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_3_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void sample4Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_4_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void sample5Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_5_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void sample6Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_6_URL);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	private TestInput loadTestInput(String sampleUrl) throws IOException, SerializationException, ResourceException, ParserConfigurationException,
			SAXException {

		String file = this.getClass().getResource(sampleUrl + REQUEST_FILE).getPath();
		VNTRequest vnt = loadRequestFromFile(file);
		IModel networkModel = loadNetworkTopologyFromFile(sampleUrl + TOPOLOGY_FILE);

		InPNetwork net = capab.getInPNetworkFromNetworkTopology(networkModel);

		String expectedResult = textFileToString(sampleUrl + RESULT_FILE);

		TestInput input = new TestInput();
		input.vnt = vnt;
		input.net = net;
		input.expectedOutput = expectedResult;
		return input;

	}

	private VNTRequest loadRequestFromFile(String url) throws ResourceException, ParserConfigurationException, SAXException, IOException {
		VNTRequest vnt = new VNTRequest();
		vnt = vnt.readVNTRequestFromXMLFile(url);
		return vnt;
	}

	private IModel loadNetworkTopologyFromFile(String url) throws IOException, SerializationException, ResourceException {

		IModel networkModel = null;
		String xmlTopology = textFileToString(url);
		NetworkTopology networkTopology = (NetworkTopology) ObjectSerializer.fromXml(xmlTopology, NetworkTopology.class);

		ResourceDescriptor descriptor = new ResourceDescriptor();
		descriptor.setNetworkTopology(networkTopology);

		networkModel = NetworkMapperDescriptorToModel.descriptorToModel(descriptor);
		return networkModel;
	}

	private String textFileToString(String fileLocation) throws IOException {
		String fileString = "";
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(fileLocation)));
		String line;
		while ((line = br.readLine()) != null) {
			fileString += line += "\n";
		}
		br.close();
		return fileString;
	}

}
