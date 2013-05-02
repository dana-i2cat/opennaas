package org.opennaas.itests.vnmapper;

import static org.junit.Assert.assertEquals;
import static org.openengsb.labs.paxexam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeFeatures;
import static org.opennaas.itests.helpers.OpennaasExamOptions.includeTestHelper;
import static org.opennaas.itests.helpers.OpennaasExamOptions.noConsole;
import static org.opennaas.itests.helpers.OpennaasExamOptions.opennaasDistributionConfiguration;
import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.network.capability.basic.NetworkBasicCapability;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.NetworkConnection;
import org.opennaas.extensions.network.model.topology.NetworkElement;
import org.opennaas.extensions.network.model.topology.Path;
import org.opennaas.extensions.network.model.virtual.VirtualDevice;
import org.opennaas.extensions.network.model.virtual.VirtualLink;
import org.opennaas.extensions.network.repository.NetworkMapperDescriptorToModel;
import org.opennaas.extensions.vnmapper.VNTRequest;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMapperOutput;
import org.opennaas.extensions.vnmapper.capability.vnmapping.VNMappingCapability;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * @author Adrian Rosello
 * 
 *         The tests of this class try to check the VNMapping capability output, which is a network model. This model must contain virtual nodes and
 *         virtual links, that must be linked with elements of the input physical network topology. Output schema is defined in output files.
 * 
 */
@RunWith(JUnit4TestRunner.class)
public class VNMapperCapabilityTest {

	Log								log						= LogFactory.getLog(VNMapperCapabilityTest.class);

	protected static final String	MAPPING_CAPABILITY_TYPE	= VNMappingCapability.CAPABILITY_TYPE;
	protected static final String	CAPABILITY_VERSION		= null;
	protected static final String	ACTION_NAME				= null;
	protected static final String	CAPABILITY_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	protected static final String	NETWORK_RESOURCE_TYPE	= "network";
	protected static final String	MAPPER_RESOURCE_TYPE	= "vnmapper";

	protected static final String	BASIC_CAPABILITY_TYPE	= NetworkBasicCapability.CAPABILITY_TYPE;
	protected static final String	RESOURCE_URI			= "mock://user:pass@host.net:2212/mocksubsystem";
	protected static final String	VNMAPPER_RESOURCE_NAME	= "VNMapper resource";
	protected static final String	NETWORK_RESOURCE_NAME	= "Network resource";

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IResourceManager		resourceManager;

	IResource						networkResource;
	IResource						vnmapperResource;

	@Configuration
	public static Option[] configuration() {
		return options(opennaasDistributionConfiguration(),
				includeFeatures("opennaas-vnmapper"),
				includeTestHelper(),
				// openDebugSocket(),
				noConsole(),
				keepRuntimeFolder());
	}

	/**
	 * Start a vnmapper resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	@Before
	public void startResource() throws ResourceException, ProtocolException {

		List<CapabilityDescriptor> capDescriptors = new ArrayList<CapabilityDescriptor>();

		capDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILITY_VERSION,
				MAPPING_CAPABILITY_TYPE,
				CAPABILITY_URI));

		// Network Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(capDescriptors, MAPPER_RESOURCE_TYPE, RESOURCE_URI,
				VNMAPPER_RESOURCE_NAME);

		// Create resource
		vnmapperResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(vnmapperResource.getResourceIdentifier());

		startNetworkResource();

	}

	/**
	 * Stop and remove the vnmapper resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	@After
	public void stopResource() throws ResourceException, ProtocolException {
		resourceManager.stopResource(vnmapperResource.getResourceIdentifier());
		resourceManager.removeResource(vnmapperResource.getResourceIdentifier());

		// remove remaining if any
		List<IResource> toRemove = resourceManager.listResources();

		for (IResource resource : toRemove) {
			if (resource.getState().equals(State.ACTIVE)) {
				resourceManager.stopResource(resource.getResourceIdentifier());
			}
			resourceManager.removeResource(resource.getResourceIdentifier());
		}

	}

	/**
	 * Test to check if capability is available from OSGi.
	 */
	@Test
	public void isCapabilityAccessibleFromResourceTest()
			throws ResourceException, ProtocolException
	{
		assertEquals(1, vnmapperResource.getCapabilities().size());
		assertEquals(MAPPING_CAPABILITY_TYPE, vnmapperResource.getCapabilities().get(0).getCapabilityInformation().getType());
	}

	@Test
	public void Sample1Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample1/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample1/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 3 virtual devices.", 3, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos20", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos20"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos30", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos30"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos40", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 2 virtual links.", 2, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-1 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-1 should end in Virtual Device 1.", vlink1.getSink().getDevice(), vDevice1);

		Assert.assertEquals("Virtual Path 0-1 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos20:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos30:em0.0", phyLink.getSink().getName());

		VirtualLink vlink2 = (VirtualLink) virtualLinks.get(1);
		Assert.assertEquals("Virtual Link 1-2 should begin in Virtual Device 1.", vlink2.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-2 should end in Virtual Device 2.", vlink2.getSink().getDevice(), vDevice2);
		Assert.assertNotNull(vlink2.getSource());
		Assert.assertNotNull(vlink2.getSink());
		Assert.assertNotNull(vlink2.getImplementedBy());

		path = vlink2.getImplementedBy();
		Assert.assertEquals("Virtual Path 1-2 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos30:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos40:em1.0", phyLink.getSink().getName());

	}

	@Test
	public void Sample2Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample2/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample2/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 3 virtual devices.", 3, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos20", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos20"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos40", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos30", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos30"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 2 virtual links.", 2, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-1 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-1 should end in Virtual Device 1.", vlink1.getSink().getDevice(), vDevice1);

		Assert.assertEquals("Virtual Path 0-1 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos20:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos40:em0.0", phyLink.getSink().getName());

		VirtualLink vlink2 = (VirtualLink) virtualLinks.get(1);
		Assert.assertEquals("Virtual Link 1-2 should begin in Virtual Device 1.", vlink2.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-2 should end in Virtual Device 2.", vlink2.getSink().getDevice(), vDevice2);
		Assert.assertNotNull(vlink2.getSource());
		Assert.assertNotNull(vlink2.getSink());
		Assert.assertNotNull(vlink2.getImplementedBy());

		path = vlink2.getImplementedBy();
		Assert.assertEquals("Virtual Path 1-2 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos30:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos40:em1.0", phyLink.getSink().getName());

	}

	@Test
	public void Sample3Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample3/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample3/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 3 virtual devices.", 3, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos40", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos50", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos50"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos60", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos60"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 3 virtual links.", 3, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-1 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-1 should end in Virtual Device 1.", vlink1.getSink().getDevice(), vDevice1);

		Assert.assertEquals("Virtual Path 0-1 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos40:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos50:em0.0", phyLink.getSink().getName());

		VirtualLink vlink2 = (VirtualLink) virtualLinks.get(1);
		Assert.assertEquals("Virtual Link 0-2 should begin in Virtual Device 1.", vlink2.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-2 should end in Virtual Device 2.", vlink2.getSink().getDevice(), vDevice2);
		Assert.assertNotNull(vlink2.getSource());
		Assert.assertNotNull(vlink2.getSink());
		Assert.assertNotNull(vlink2.getImplementedBy());

		path = vlink2.getImplementedBy();
		Assert.assertEquals("Virtual Path 0-2 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos40:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos60:em0.0", phyLink.getSink().getName());

		VirtualLink vlink3 = (VirtualLink) virtualLinks.get(2);
		Assert.assertEquals("Virtual Link 1-2 should begin in Virtual Device 1.", vlink3.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-2 should end in Virtual Device 2.", vlink3.getSink().getDevice(), vDevice2);
		Assert.assertNotNull(vlink3.getSource());
		Assert.assertNotNull(vlink3.getSink());
		Assert.assertNotNull(vlink3.getImplementedBy());

		path = vlink3.getImplementedBy();
		Assert.assertEquals("Virtual Path 1-2 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos50:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos60:em1.0", phyLink.getSink().getName());

	}

	@Test
	public void Sample4Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample4/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample4/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 5 virtual devices.", 5, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos20", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos20"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos40", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos50", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos50"));

		VirtualDevice vDevice3 = (VirtualDevice) virtualDevices.get(3);
		Assert.assertEquals("3", vDevice3.getName());
		Assert.assertEquals("VirtualDevice 3 should match physical router router:junos60", vDevice3.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos60"));

		VirtualDevice vDevice4 = (VirtualDevice) virtualDevices.get(4);
		Assert.assertEquals("4", vDevice4.getName());
		Assert.assertEquals("VirtualDevice 4 should match physical router router:junos30", vDevice4.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos30"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 4 virtual links.", 4, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-4 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-4 should end in Virtual Device 4.", vlink1.getSink().getDevice(), vDevice4);

		Assert.assertEquals("Virtual Path 0-4 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos20:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos30:em1.0", phyLink.getSink().getName());

		VirtualLink vlink2 = (VirtualLink) virtualLinks.get(1);
		Assert.assertEquals("Virtual Link 1-2 should begin in Virtual Device 1.", vlink2.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-2 should end in Virtual Device 2.", vlink2.getSink().getDevice(), vDevice2);
		Assert.assertNotNull(vlink2.getSource());
		Assert.assertNotNull(vlink2.getSink());
		Assert.assertNotNull(vlink2.getImplementedBy());

		path = vlink2.getImplementedBy();
		Assert.assertEquals("Virtual Path 1-2 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos40:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos50:em0.0", phyLink.getSink().getName());

		VirtualLink vlink3 = (VirtualLink) virtualLinks.get(2);
		Assert.assertEquals("Virtual Link 1-3 should begin in Virtual Device 1.", vlink3.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-3 should end in Virtual Device 3.", vlink3.getSink().getDevice(), vDevice3);
		Assert.assertNotNull(vlink3.getSource());
		Assert.assertNotNull(vlink3.getSink());
		Assert.assertNotNull(vlink3.getImplementedBy());

		path = vlink3.getImplementedBy();
		Assert.assertEquals("Virtual Path 1-3 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos40:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos60:em0.0", phyLink.getSink().getName());

		VirtualLink vlink4 = (VirtualLink) virtualLinks.get(3);
		Assert.assertEquals("Virtual Link 2-3 should begin in Virtual Device 1.", vlink4.getSource().getDevice(), vDevice2);
		Assert.assertEquals("Virtual Link 3-3 should end in Virtual Device 3.", vlink4.getSink().getDevice(), vDevice3);
		Assert.assertNotNull(vlink4.getSource());
		Assert.assertNotNull(vlink4.getSink());
		Assert.assertNotNull(vlink4.getImplementedBy());

		path = vlink4.getImplementedBy();
		Assert.assertEquals("Virtual Path 2-3 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos50:em1.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos60:em1.0", phyLink.getSink().getName());

	}

	@Test
	public void Sample5Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample5/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample5/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 3 virtual devices.", 3, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos20", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos20"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos40", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos30", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos30"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 1 virtual link.", 1, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-2 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-2 should end in Virtual Device 1.", vlink1.getSink().getDevice(), vDevice2);

		Assert.assertEquals("Virtual Path 0-2 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos20:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos30:em0.0", phyLink.getSink().getName());

	}

	@Test
	public void Sample6Test() throws ResourceException, IOException, SerializationException, ParserConfigurationException, SAXException {

		networkResource.setModel(loadNetworkTopologyFromFile("/inputs/sample6/topology.xml"));
		VNTRequest vnt = loadRequestFromFile("/inputs/sample6/request.xml");

		VNMappingCapability capab = (VNMappingCapability) vnmapperResource.getCapabilityByType(MAPPING_CAPABILITY_TYPE);

		Assert.assertNotNull(capab);

		VNMapperOutput out = capab.mapVN(networkResource.getResourceIdentifier().getId(), vnt);

		NetworkModel virtualModel = out.getNetworkModel();
		Assert.assertNotNull(virtualModel);

		NetworkModel physicalModel = (NetworkModel) networkResource.getModel();
		Assert.assertNotNull(physicalModel);

		// /// CHECK VIRTUAL DEVICES

		List<NetworkElement> virtualDevices = NetworkModelHelper
				.getNetworkElementsByClassName(VirtualDevice.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 4 virtual devices.", 4, virtualDevices.size());

		VirtualDevice vDevice0 = (VirtualDevice) virtualDevices.get(0);
		Assert.assertEquals("0", vDevice0.getName());
		Assert.assertEquals("VirtualDevice 0 should match physical router router:junos20", vDevice0.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos20"));

		VirtualDevice vDevice1 = (VirtualDevice) virtualDevices.get(1);
		Assert.assertEquals("1", vDevice1.getName());
		Assert.assertEquals("VirtualDevice 1 should match physical router router:junos40", vDevice1.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos40"));

		VirtualDevice vDevice2 = (VirtualDevice) virtualDevices.get(2);
		Assert.assertEquals("2", vDevice2.getName());
		Assert.assertEquals("VirtualDevice 2 should match physical router router:junos30", vDevice2.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos30"));

		VirtualDevice vDevice3 = (VirtualDevice) virtualDevices.get(3);
		Assert.assertEquals("3", vDevice3.getName());
		Assert.assertEquals("VirtualDevice 3 should match physical router router:junos50", vDevice3.getImplementedBy(),
				NetworkModelHelper.getDeviceByName(physicalModel, "router:junos50"));

		// /// CHECK VIRTUAL LINKS

		List<NetworkElement> virtualLinks = NetworkModelHelper.getNetworkElementsByClassName(VirtualLink.class, virtualModel.getNetworkElements());
		Assert.assertEquals("Virtual Network Model should contain 2 virtual links.", 2, virtualLinks.size());

		VirtualLink vlink1 = (VirtualLink) virtualLinks.get(0);
		Assert.assertNotNull(vlink1.getSource());
		Assert.assertNotNull(vlink1.getSink());
		Assert.assertNotNull(vlink1.getImplementedBy());

		Path path = vlink1.getImplementedBy();
		Assert.assertEquals("Virtual Link 0-2 should begin in Virtual Device 0.", vlink1.getSource().getDevice(), vDevice0);
		Assert.assertEquals("Virtual Link 0-2 should end in Virtual Device 2.", vlink1.getSink().getDevice(), vDevice2);

		Assert.assertEquals("Virtual Path 0-2 should only contain one segment.", 1, path.getPathSegments().size());
		NetworkConnection netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		Link phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos20:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos30:em0.0", phyLink.getSink().getName());

		VirtualLink vlink2 = (VirtualLink) virtualLinks.get(1);
		Assert.assertNotNull(vlink2.getSource());
		Assert.assertNotNull(vlink2.getSink());
		Assert.assertNotNull(vlink2.getImplementedBy());

		path = vlink2.getImplementedBy();
		Assert.assertEquals("Virtual Link 1-3 should begin in Virtual Device 1.", vlink2.getSource().getDevice(), vDevice1);
		Assert.assertEquals("Virtual Link 1-3 should end in Virtual Device 3.", vlink2.getSink().getDevice(), vDevice3);

		Assert.assertEquals("Virtual Path 1-3 should only contain one segment.", 1, path.getPathSegments().size());
		netConnection = path.getPathSegments().get(0);
		Assert.assertTrue(netConnection instanceof Link);

		phyLink = (Link) netConnection;
		Assert.assertEquals("router:junos40:em0.0", phyLink.getSource().getName());
		Assert.assertEquals("router:junos50:em0.0", phyLink.getSink().getName());

	}

	void startNetworkResource() throws ResourceException {
		List<CapabilityDescriptor> capDescriptors = new ArrayList<CapabilityDescriptor>();

		capDescriptors.add(ResourceHelper.newCapabilityDescriptor(ACTION_NAME,
				CAPABILITY_VERSION,
				BASIC_CAPABILITY_TYPE,
				CAPABILITY_URI));

		// Network Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(capDescriptors, NETWORK_RESOURCE_TYPE, RESOURCE_URI,
				NETWORK_RESOURCE_NAME);

		// Create resource
		networkResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(networkResource.getResourceIdentifier());
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

	private VNTRequest loadRequestFromFile(String url) throws ResourceException, ParserConfigurationException, SAXException, IOException {

		String xmlRequest = textFileToString(url);
		DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = newDocumentBuilder.parse(new ByteArrayInputStream(xmlRequest.getBytes()));
		VNTRequest vnt = new VNTRequest();

		return vnt.parseVNTRequestFromDoc(doc);

	}

}
