package org.opennaas.itests.nettopology.internal;

/*
 * #%L
 * OpenNaaS :: iTests :: Generic Network
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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.endpoints.WSEndpointListenerHandler;
import org.opennaas.core.resources.ILifecycle.State;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.CapabilityProperty;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceHelper;
import org.opennaas.extensions.genericnetwork.model.GenericNetworkModel;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TopologyTest {

	private static final String			TOPOLOGY_FILE_RELATIVE_PATH				= "/nettopology.xml";

	private static final String			GENERIC_NETWORK_RESOURCE_TYPE			= "genericnetwork";
	private static final String			NETTOPOLOGY_CAPABILITY_TYPE				= "nettopology";
	private static final String			NETTOPOLOGY_ACTIONSET_NAME				= "internal";
	private static final String			NETTOPOLOGY_ACTIONSET_VERSION			= "1.0.0";
	public static final String			TOPOLOGY_FILE_CAPABILITY_PROPERTY_NAME	= "topology.file.path";

	private static final String			RESOURCE_NAME							= "net1";

	private static final String			NETTOPOLOGY_WS_CONTEXT					= "/opennaas/" + GENERIC_NETWORK_RESOURCE_TYPE + "/" + RESOURCE_NAME + "/" + NETTOPOLOGY_CAPABILITY_TYPE;

	@Inject
	protected BundleContext				context;

	/**
	 * Make sure blueprint for required bundles have finished initialization
	 */
	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.genericnetwork)", timeout = 50000)
	private BlueprintContainer			genericNetworkBlueprintContainer;

	@Inject
	private IResourceManager			resourceManager;

	private WSEndpointListenerHandler	wsListener;

	private IResource					netResource;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-openflowswitch", "opennaas-genericnetwork", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepLogConfiguration(),
				// OpennaasExamOptions.openDebugSocket(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void createAndStartNetworkResource() throws ResourceException, InterruptedException, IOException {

		String topologyFileAbsolutePath = obtainTopologyAbsolutePath();

		CapabilityDescriptor nettopologyCapabDesc = ResourceHelper.newCapabilityDescriptor(NETTOPOLOGY_ACTIONSET_NAME,
				NETTOPOLOGY_ACTIONSET_VERSION, NETTOPOLOGY_CAPABILITY_TYPE, null);

		// add topology file absolute path to capability descriptor
		CapabilityProperty filePathCapabProperty = new CapabilityProperty();
		filePathCapabProperty.setName(TOPOLOGY_FILE_CAPABILITY_PROPERTY_NAME);
		filePathCapabProperty.setValue(topologyFileAbsolutePath);
		nettopologyCapabDesc.getCapabilityProperties().add(filePathCapabProperty);

		List<CapabilityDescriptor> lCapabilityDescriptors = new ArrayList<CapabilityDescriptor>(1);
		lCapabilityDescriptors.add(nettopologyCapabDesc);

		ResourceDescriptor resourceDescriptor = ResourceHelper.newResourceDescriptor(lCapabilityDescriptors, GENERIC_NETWORK_RESOURCE_TYPE,
				null, RESOURCE_NAME);

		netResource = resourceManager.createResource(resourceDescriptor);

		// Start resource
		// wsListener = new WSEndpointListenerHandler();
		// wsListener.registerWSEndpointListener(NETTOPOLOGY_WS_CONTEXT, context);
		resourceManager.startResource(netResource.getResourceIdentifier());
		// wsListener.waitForEndpointToBePublished();

	}

	@After
	public void deleteNetworkResource() throws ResourceException, InterruptedException {
		if (netResource.getState().equals(State.ACTIVE)) {
			resourceManager.stopResource(netResource.getResourceIdentifier());
		}
		resourceManager.removeResource(netResource.getResourceIdentifier());
		// wsListener.waitForEndpointToBeUnpublished();
	}

	@Test
	public void topologyIsLoadedIntoModel() throws SerializationException {
		Topology topologyInModel = ((GenericNetworkModel) netResource.getModel()).getTopology();
		Assert.assertNotNull(topologyInModel);

		Topology givenTopology = ObjectSerializer.fromXml(this.getClass().getResourceAsStream(TOPOLOGY_FILE_RELATIVE_PATH)
				, Topology.class);
		Assert.assertEquals(givenTopology, topologyInModel);
	}

	private String obtainTopologyAbsolutePath() throws IOException {
		InputStream input = this.getClass().getResourceAsStream(TOPOLOGY_FILE_RELATIVE_PATH);
		File tmp = File.createTempFile("nettopology", ".tmp.xml");
		tmp.deleteOnExit();

		IOUtils.copy(input, new FileOutputStream(tmp));
		return tmp.getAbsolutePath();
	}

}
