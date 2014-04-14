package org.opennaas.itests.core.queue;

/*
 * #%L
 * OpenNaaS :: iTests :: Core
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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.Reply;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.CorruptStateException;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.IncorrectLifecycleStateException;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.capability.ICapability;
import org.opennaas.core.resources.capability.ICapabilityFactory;
import org.opennaas.core.resources.capability.ICapabilityLifecycle;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.helpers.ResourceDescriptorFactory;
import org.opennaas.core.resources.mock.MockAction;
import org.opennaas.core.resources.mock.MockResource;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.swissbox.tracker.ServiceLookup;
import org.osgi.framework.BundleContext;
import org.osgi.service.blueprint.container.BlueprintContainer;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class PrepareCommitRollbackTest
{
	private final static Log		log	= LogFactory.getLog(PrepareCommitRollbackTest.class);

	private MockResource			mockResource;
	private ICapability				queueCapability;
	private IQueueManagerCapability	queueManagerCapability;

	@Inject
	private BundleContext			bundleContext;

	@Inject
	private IProtocolManager		protocolManager;

	@Inject
	private IResourceManager		resourceManager;

	@Inject
	@Filter("(capability=queue)")
	private ICapabilityFactory		queueManagerFactory;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.router.repository)", timeout = 20000)
	private BlueprintContainer		routerService;

	@Inject
	@Filter(value = "(osgi.blueprint.container.symbolicname=org.opennaas.extensions.queuemanager)", timeout = 20000)
	private BlueprintContainer		queueService;

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-router", "opennaas-router-driver-junos"),
				OpennaasExamOptions.keepLogConfiguration(),
				OpennaasExamOptions.includeSwissboxFramework(),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(), 
				OpennaasExamOptions.keepRuntimeFolder());
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newSessionContextNetconf() {
		String uri = System.getProperty("protocol.uri");

		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				ProtocolSessionContext.PROTOCOL_URI, uri);
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"netconf");
		protocolSessionContext.addParameter(ProtocolSessionContext.AUTH_TYPE, "password");
		return protocolSessionContext;
	}

	@Before
	public void before() throws ProtocolException, IncorrectLifecycleStateException, ResourceException, CorruptStateException {
		mockResource = new MockResource();
		mockResource.setModel(new ComputerSystem());
		List<String> capabilities = new ArrayList<String>();

		capabilities.add("queue");
		ResourceDescriptor resourceDescriptor = ResourceDescriptorFactory.newResourceDescriptor("mockresource", "router", capabilities);

		mockResource.setResourceDescriptor(resourceDescriptor);

		// will not be the same that mockResource but will do the trick
		resourceManager.createResource(resourceDescriptor);

		protocolManager.getProtocolSessionManagerWithContext(mockResource.getResourceId(), newSessionContextNetconf());

		log.info("INFO: Before test, getting queue...");
		queueCapability = queueManagerFactory.create(mockResource);
		((ICapabilityLifecycle) queueCapability).initialize();
		queueManagerCapability = ServiceLookup.getService(bundleContext, IQueueManagerCapability.class, 50000,
				"(capability=queue)(capability.name=" + mockResource.getResourceId() + ")");
	}

	@Test
	public void testPrepareRestoreAction() throws Exception {
		String uri = System.getProperty("protocol.uri");

		if (uri == null || uri.equals("${protocol.uri}")) {
			uri = "mock://user:pass@host.net:2212/mocksubsystem";
		}
		String compare = null;
		String toCompare = null;
		Query query = QueryFactory.newGetConfig("running", "<configuration></configuration>", null);
		Reply reply = sendNetconfMessage(query, uri);
		compare = reply.getContain();

		IAction action = new MockAction();
		action.setActionID("mockAction");
		queueManagerCapability.queueAction(action);
		action = new CorruptedAction();
		action.setActionID("corruptedAction");
		queueManagerCapability.queueAction(action);

		boolean isChecked = false;
		try {
			queueManagerCapability.execute();
		} catch (Exception e) {
			if (e instanceof CapabilityException)
				isChecked = true;
		}
		if (!uri.startsWith("mock:"))
			Assert.assertTrue(isChecked);

		reply = sendNetconfMessage(query, uri);
		toCompare = reply.getContain();

		Assert.assertEquals(compare, toCompare);
	}

	public Reply sendNetconfMessage(Query query, String uri) throws Exception {
		SessionContext sessionContext = new SessionContext();
		sessionContext.setURI(new URI(uri));
		NetconfSession session = new NetconfSession(sessionContext);
		session.connect();
		Reply reply = session.sendSyncQuery(query);
		if (reply.containsErrors())
			throw new Exception();
		session.disconnect();
		return reply;

	}

	public String readStringFromFile(String pathFile) throws Exception {
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
