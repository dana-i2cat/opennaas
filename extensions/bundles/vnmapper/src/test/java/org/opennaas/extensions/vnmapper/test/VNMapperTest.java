package org.opennaas.extensions.vnmapper.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import org.opennaas.extensions.vnmapper.capability.example.ExampleCapability;
import org.opennaas.extensions.vnmapper.capability.example.Global;
import org.opennaas.extensions.vnmapper.capability.example.InPNetwork;
import org.opennaas.extensions.vnmapper.capability.example.MappingResult;
import org.opennaas.extensions.vnmapper.capability.example.VNTRequest;

public class VNMapperTest {

	private final static String	REQUEST_1_URL	= "/samples/sample1/request.xml";
	private final static String	TOPOLOGY_1_URL	= "/samples/sample1/topology.xml";
	private final static String	RESULT_1_URL	= "/samples/sample1/output.karaf";

	private ExampleCapability	capab;
	private VNTRequest			vnt;
	private IModel				networkModel;

	@Before
	public void setUp() {

		String resourceId = "1234";
		Information capabilityInformation = new Information();
		capabilityInformation.setType(ExampleCapability.CAPABILITY_TYPE);

		CapabilityDescriptor descriptor = new CapabilityDescriptor();

		descriptor.setCapabilityInformation(capabilityInformation);
		capab = new ExampleCapability(descriptor, resourceId);

		Global.pathChoice = 1;
		Global.maxPathLinksNum = 5;
		Global.stepsMax = 100;
	}

	@Test
	public void sample1Test() throws IOException, SerializationException, ResourceException {

		String file = this.getClass().getResource(REQUEST_1_URL).getPath();
		loadRequestFromFile(file);
		loadNetworkTopologyFromFile(TOPOLOGY_1_URL);

		InPNetwork net = capab.getInPNetworkFromNetworkTopology(networkModel);
		MappingResult result = capab.executeAlgorithm(vnt, net);

		String resultString = result.toString();

		String expectedResult = textFileToString(RESULT_1_URL);

		Assert.assertEquals(expectedResult, resultString);

	}

	private void loadRequestFromFile(String url) throws ResourceException {
		vnt = new VNTRequest();
		vnt = vnt.readVNTRequestFromXMLFile(url);
	}

	private void loadNetworkTopologyFromFile(String url) throws IOException, SerializationException, ResourceException {

		String xmlTopology = textFileToString(url);
		NetworkTopology networkTopology = (NetworkTopology) ObjectSerializer.fromXml(xmlTopology, NetworkTopology.class);

		ResourceDescriptor descriptor = new ResourceDescriptor();
		descriptor.setNetworkTopology(networkTopology);

		networkModel = NetworkMapperDescriptorToModel.descriptorToModel(descriptor);

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
