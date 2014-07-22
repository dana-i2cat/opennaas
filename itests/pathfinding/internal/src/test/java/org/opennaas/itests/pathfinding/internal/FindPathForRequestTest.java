package org.opennaas.itests.pathfinding.internal;

/*
 * #%L
 * OpenNaaS :: iTests :: PathFinding :: Internal
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.ops4j.pax.exam.CoreOptions.options;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.genericnetwork.capability.nettopology.INetTopologyCapability;
import org.opennaas.extensions.genericnetwork.capability.nettopology.NetTopologyCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.IPathFindingCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingCapability;
import org.opennaas.extensions.genericnetwork.capability.pathfinding.PathFindingParamsMapping;
import org.opennaas.extensions.genericnetwork.model.circuit.Route;
import org.opennaas.extensions.genericnetwork.model.circuit.request.CircuitRequest;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Destination;
import org.opennaas.extensions.genericnetwork.model.circuit.request.Source;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class FindPathForRequestTest {

	@Inject
	protected IResourceManager	resourceManager;

	protected IResource			genericNetwork;

	private static final Log	log							= LogFactory.getLog(FindPathForRequestTest.class);

	private static final String	GENERICNET_RESOURCE_TYPE	= "genericnetwork";
	private static final String	GENERICNET_RESOURCE_NAME	= "sampleGenericNetwork";

	private static final String	PATHFINDING_CAPABILITY_TYPE	= PathFindingCapability.CAPABILITY_TYPE;
	private static final String	NETTOPOLOGY_CAPABILITY_TYPE	= NetTopologyCapability.CAPABILITY_TYPE;

	private static final String	CAPABILITY_VERSION			= "1.0.0";
	private static final String	INTERNAL_ACTIONSET_NAME		= "internal";

	private static final String	MOCK_URI					= "mock://user:pass@host.net:2212/mocksubsystem";

	private static final String	ROUTE_FILES_URI				= "/routes.xml";
	private static final String	ROUTES_MAPPING_URI			= "/mapping.xml";
	private static final String	TOPOLOGY_FILE_URI			= "/nettopology.xml";

	private static final String	SRC_IP						= "192.168.10.10";
	private static final String	DST_IP						= "192.168.10.11";
	private static final String	SRC_PORT					= "portA";
	private static final String	DST_PORT					= "portC";
	private static final String	TOS							= "4";

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-genericnetwork", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void prepareTest() throws ResourceException, ProtocolException, IOException {
		startResource();

	}

	@After
	public void shutdownTest() throws ResourceException, ProtocolException {
		stopResource();

	}

	@Test
	public void isCapabilityAccessibleFromResource() throws ResourceException, ProtocolException {

		Assert.assertEquals(2, genericNetwork.getCapabilities().size());
		Assert.assertTrue(genericNetwork.getCapabilities().get(0) instanceof IPathFindingCapability);
		Assert.assertTrue(genericNetwork.getCapabilities().get(1) instanceof INetTopologyCapability);

	}

	/*
	 * When Candidates are more specific than requests, there will be no matching route. An ActionException is expected requesting a route without
	 * ports when mapping file declares ports.
	 */
	@Test(expected = ActionException.class)
	public void findPathForRequestTestWithoutPorts() throws ResourceException {

		CircuitRequest request = generateSampleRequestWithoutPorts();

		IPathFindingCapability pathFindingCapab = (IPathFindingCapability) genericNetwork.getCapabilityByInterface(IPathFindingCapability.class);

		// no possible route, no matching
		pathFindingCapab.findPathForRequest(request);
	}

	@Test
	public void findPathForRequestTestWithPorts() throws ResourceException {

		CircuitRequest request = generateSampleRequestWithPorts();

		IPathFindingCapability pathFindingCapab = (IPathFindingCapability) genericNetwork.getCapabilityByInterface(IPathFindingCapability.class);
		Route route = pathFindingCapab.findPathForRequest(request);

		Assert.assertNotNull(route);
		Assert.assertEquals("PathFinding capability should have selected route with id 3", "3", route.getId());
	}

	private CircuitRequest generateSampleRequestWithPorts() {
		CircuitRequest request = new CircuitRequest();

		Source source = new Source();
		source.setAddress(SRC_IP);
		source.setLinkPort(SRC_PORT);

		Destination destination = new Destination();
		destination.setAddress(DST_IP);
		destination.setLinkPort(DST_PORT);

		request.setSource(source);
		request.setDestination(destination);
		request.setLabel(TOS);

		return request;
	}

	private CircuitRequest generateSampleRequestWithoutPorts() {

		CircuitRequest request = new CircuitRequest();

		Source source = new Source();
		source.setAddress(SRC_IP);
		Destination destination = new Destination();
		destination.setAddress(DST_IP);

		request.setSource(source);
		request.setDestination(destination);
		request.setLabel(TOS);

		return request;
	}

	/**
	 * Start genericnetwork resource with pathfinding capability
	 * 
	 * @throws IOException
	 * 
	 */
	protected void startResource() throws ResourceException, ProtocolException, IOException {

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>();

		String topologyFileAbsolutePath = obtainTopologyAbsolutePath();

		CapabilityDescriptor pathFindingDescriptor = ResourceHelper.newCapabilityDescriptor(INTERNAL_ACTIONSET_NAME,
				CAPABILITY_VERSION, PATHFINDING_CAPABILITY_TYPE, MOCK_URI);

		CapabilityProperty routeURIProperty = new CapabilityProperty();
		routeURIProperty.setName(PathFindingParamsMapping.ROUTES_FILE_KEY);
		routeURIProperty.setValue(readFile(ROUTE_FILES_URI));

		CapabilityProperty mapURImapURIProperty = new CapabilityProperty();
		mapURImapURIProperty.setName(PathFindingParamsMapping.ROUTES_MAPPING_KEY);
		mapURImapURIProperty.setValue(readFile(ROUTES_MAPPING_URI));

		pathFindingDescriptor.getCapabilityProperties().add(routeURIProperty);
		pathFindingDescriptor.getCapabilityProperties().add(mapURImapURIProperty);

		lCapabilityDescriptors.add(pathFindingDescriptor);

		CapabilityDescriptor topologyDescriptor = ResourceHelper.newCapabilityDescriptor(INTERNAL_ACTIONSET_NAME,
				CAPABILITY_VERSION, NETTOPOLOGY_CAPABILITY_TYPE, MOCK_URI);

		CapabilityProperty topologyFile = new CapabilityProperty();
		topologyFile.setName(NetTopologyCapability.TOPOLOGY_FILE);
		topologyFile.setValue(topologyFileAbsolutePath);

		topologyDescriptor.getCapabilityProperties().add(topologyFile);
		lCapabilityDescriptors.add(topologyDescriptor);
		// Router Resource Descriptor
		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, GENERICNET_RESOURCE_TYPE,
				MOCK_URI, GENERICNET_RESOURCE_NAME);

		// Create resource
		genericNetwork = resourceManager.createResource(resourceDescriptor);

		// Start resource
		resourceManager.startResource(genericNetwork.getResourceIdentifier());
	}

	/**
	 * Stop and remove the genericnetwork resource
	 * 
	 * @throws ResourceException
	 * @throws ProtocolException
	 */
	protected void stopResource() throws ResourceException, ProtocolException {
		// Stop resource
		resourceManager.stopResource(genericNetwork.getResourceIdentifier());

		// Remove resource
		resourceManager.removeResource(genericNetwork.getResourceIdentifier());
	}

	private String readFile(String url) throws IOException {
		InputStream input = this.getClass().getResourceAsStream(url);
		File tmp = File.createTempFile(url, ".tmp.xml");
		tmp.deleteOnExit();

		IOUtils.copy(input, new FileOutputStream(tmp));
		return tmp.getAbsolutePath();
	}

	private String obtainTopologyAbsolutePath() throws IOException {
		InputStream input = this.getClass().getResourceAsStream(TOPOLOGY_FILE_URI);
		File tmp = File.createTempFile("nettopology", ".tmp.xml");
		tmp.deleteOnExit();

		IOUtils.copy(input, new FileOutputStream(tmp));
		return tmp.getAbsolutePath();
	}
}
