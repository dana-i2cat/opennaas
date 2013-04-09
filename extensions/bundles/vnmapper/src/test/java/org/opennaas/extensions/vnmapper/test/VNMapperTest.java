package org.opennaas.extensions.vnmapper.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

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
import org.opennaas.extensions.vnmapper.PLink;
import org.opennaas.extensions.vnmapper.PNode;
import org.opennaas.extensions.vnmapper.VLink;
import org.opennaas.extensions.vnmapper.VNState;
import org.opennaas.extensions.vnmapper.VNTRequest;
import org.opennaas.extensions.vnmapper.VNode;
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
	private final static String	SAMPLE_7_URL	= "/samples/sample7/";
	private final static String	SAMPLE_8_URL	= "/samples/sample8/";

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

		Global.PNodeChoice = 1;
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

	/**
	 * Test with capacities. <br/>
	 * All physical nodes have vnode capacity = 16 <br/>
	 * Physical links have following bw capacity: <br/>
	 * link : 0--1 : 100 <br/>
	 * link : 0--4 : 200 <br/>
	 * link : 1--2 : 300 <br/>
	 * link : 2--3 : 400 <br/>
	 * link : 2--4 : 500 <br/>
	 * link : 3--4 : 600 <br/>
	 * 
	 * @throws IOException
	 * @throws SerializationException
	 * @throws ResourceException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	@Test
	public void sample7Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_7_URL);

		// set capacities in physical nodes
		for (PNode node : testInput.net.getNodes()) {
			node.setCapacity(16);
		}
		// set capacities in physical links
		testInput.net.getConnections().get(0).get(1).setCapacity(100);
		testInput.net.getConnections().get(0).get(4).setCapacity(200);
		testInput.net.getConnections().get(1).get(2).setCapacity(300);
		testInput.net.getConnections().get(2).get(3).setCapacity(400);
		testInput.net.getConnections().get(2).get(4).setCapacity(500);
		testInput.net.getConnections().get(3).get(4).setCapacity(600);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		// Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	/**
	 * Test with capacities. <br/>
	 * Physical nodes have following vnode capacity: <br/>
	 * node 0--router0 : 10 <br/>
	 * node 1--router1 : 11 <br/>
	 * node 2--router2 : 12 <br/>
	 * node 3--router3 : 13 <br/>
	 * node 4--router4 : 14 <br/>
	 * All physical links have bw capacity = 600 <br/>
	 * 
	 * @throws IOException
	 * @throws SerializationException
	 * @throws ResourceException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	@Test
	public void sample8Test() throws IOException, SerializationException, ResourceException, ParserConfigurationException, SAXException {

		TestInput testInput = loadTestInput(SAMPLE_8_URL);

		// set capacities in physical nodes
		testInput.net.getNodes().get(0).setCapacity(10);
		testInput.net.getNodes().get(1).setCapacity(11);
		testInput.net.getNodes().get(2).setCapacity(12);
		testInput.net.getNodes().get(3).setCapacity(13);
		testInput.net.getNodes().get(4).setCapacity(14);
		// set capacities in physical links
		for (PLink link : testInput.net.getLinks()) {
			link.setCapacity(600);
		}

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		VNMapperInput input = new VNMapperInput(testInput.net, testInput.vnt);
		VNMapperOutput output = new VNMapperOutput(result, input);

		Assert.assertEquals(testInput.expectedOutput, output.toString());

	}

	@Test
	public void overloadVnodeCapacityWithNodesNumTest() throws Exception {

		IModel networkModel = loadNetworkTopologyFromFile(SAMPLE_1_URL + TOPOLOGY_FILE);
		InPNetwork net = capab.getInPNetworkFromNetworkTopology(networkModel);

		int totalVnodeCapacity = 0;
		for (PNode node : net.getNodes()) {
			totalVnodeCapacity += node.getCapacity();
		}

		VNTRequest request1 = new VNTRequest();
		// put totalVnodeCapacity+1 nodes in request
		ArrayList<VNode> vnodes = new ArrayList<VNode>(totalVnodeCapacity + 1);
		for (int i = 0; i <= totalVnodeCapacity; i++) {
			VNode n = new VNode();
			n.setId(i);
			n.setPnodeID("-");
			n.setCapacity(1);
			vnodes.add(n);
		}
		request1.setVnodes(vnodes);
		request1.setVnodeNum(vnodes.size());

		// initialize connections (required for the algorithm)
		// TODO this should be part of algorithm initialization
		for (int i = 0; i < request1.getVnodeNum(); i++) {
			request1.getConnections().add(new ArrayList<VLink>());
			for (int j = 0; j < request1.getVnodeNum(); j++) {
				request1.getConnections().get(i).add(new VLink());
			}
		}

		MappingResult result = capab.executeAlgorithm(request1, net);

		Assert.assertEquals(VNState.SUCCESSFUL, result.getMatchingState());
		Assert.assertEquals(VNState.ERROR, result.getMappingState());
	}

	@Test
	public void overloadVnodeCapacityWithNodesCapacityTest() throws Exception {

		TestInput testInput = loadTestInput(SAMPLE_1_URL);

		int maxPNodeCapacity = 0;
		for (PNode node : testInput.net.getNodes()) {
			if (node.getCapacity() > maxPNodeCapacity)
				maxPNodeCapacity = node.getCapacity();
		}

		// get random index [0, size())
		int randomIndex = new Random(System.nanoTime()).nextInt(testInput.vnt.getVnodes().size());

		testInput.vnt.getVnodes().get(randomIndex).setCapacity(maxPNodeCapacity + 1);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		Assert.assertEquals(VNState.ERROR, result.getMatchingState());
		Assert.assertEquals(VNState.SKIPPED, result.getMappingState());
	}

	@Test
	public void overloadLinkCapacityTest() throws Exception {

		TestInput testInput = loadTestInput(SAMPLE_1_URL);

		int maxPLinkCapacity = 0;
		for (PLink link : testInput.net.getLinks()) {
			if (link.getCapacity() > maxPLinkCapacity)
				maxPLinkCapacity = link.getCapacity();
		}

		// get random index [0, size())
		int randomIndex = new Random(System.nanoTime()).nextInt(testInput.vnt.getVlinks().size());

		testInput.vnt.getVlinks().get(randomIndex).setCapacity(maxPLinkCapacity + 10);

		MappingResult result = capab.executeAlgorithm(testInput.vnt, testInput.net);

		Assert.assertEquals(VNState.SUCCESSFUL, result.getMatchingState());
		Assert.assertEquals(VNState.ERROR, result.getMappingState());
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
