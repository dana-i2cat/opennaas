package org.opennaas.itests.ryu;

/*
 * #%L
 * org.opennaas.itests.ryu
 * %%
 * Copyright (C) 2007 - 2015 Fundació Privada i2CAT, Internet i Innovació a Catalunya
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

import java.io.IOException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.common.util.ProxyClassLoader;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.Information;
import org.opennaas.core.resources.descriptor.ResourceDescriptor;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.extensions.protocols.http.HttpProtocolSession;
import org.opennaas.extensions.ryu.alarm.IAlarmObserver;
import org.opennaas.extensions.ryu.capability.monitoringmodule.IMonitoringModuleCapability;
import org.opennaas.extensions.ryu.capability.monitoringmodule.MonitoringModuleCapability;
import org.opennaas.extensions.ryu.client.monitoringmodule.IMonitoringModuleCallbackAPI;
import org.opennaas.itests.helpers.InitializerTestHelper;
import org.opennaas.itests.helpers.OpennaasExamOptions;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class MonitoringModuleCapabilityTest {

	private static final String	REMOTE_SERVER_HOST	= "127.0.0.1";
	private static final String	REMOTE_SERVER_PORT	= "8081";
	private static final String	RESOURCE_URI		= "http://" + REMOTE_SERVER_HOST + ":" + REMOTE_SERVER_PORT + "/xifi";

	private static final String	ALARM_DPID			= "00:00:00:00:00:00:00:01";
	private static final String	ALARM_PORT			= "2";
	private static final String	ALARM_THRESHOLD		= "10";

	private static final String	CLIENT_POST_URL		= "/xifi/register_alarm/" + ALARM_DPID + "/" + ALARM_PORT;
	private static final String	CLIENT_DEL_URL		= "/xifi/delete_alarm/" + ALARM_DPID + "/" + ALARM_PORT;

	private static final String	REQUEST_FILE		= "/request.json";

	private static final String	URL_PREFFIX_KEY		= "url_prefix";
	private static final String	URL_PREFFIX_VALUE	= "/xifi/raise_alarm/";

	private IResource			ryuResource;

	@Inject
	IResourceManager			resourceManager;

	@Inject
	IProtocolManager			protocolManager;

	@Rule
	public WireMockRule			wireMockRule		= new WireMockRule(Integer.valueOf(REMOTE_SERVER_PORT));

	@Configuration
	public static Option[] configuration() {
		return options(
				OpennaasExamOptions.opennaasDistributionConfiguration(),
				OpennaasExamOptions.includeFeatures("opennaas-ryu", "itests-helpers"),
				OpennaasExamOptions.noConsole(), OpennaasExamOptions.doNotDelayShell(),
				OpennaasExamOptions.keepLogConfiguration(),
				// OpennaasExamOptions.openDebugSocket(),
				OpennaasExamOptions.keepRuntimeFolder());
	}

	@Before
	public void prepareTest() throws ResourceException, ProtocolException, IOException, JSONException {
		startResource();

		mockServer();
	}

	@After
	public void shutdownTest() throws ResourceException {
		resourceManager.stopResource(ryuResource.getResourceIdentifier());
		resourceManager.removeResource(ryuResource.getResourceIdentifier());
	}

	@Test
	public void isCapabilityAccessibleFromResource() throws ResourceException, ProtocolException {
		Assert.assertNotNull("Ryu resource should have been created.", ryuResource);
		Assert.assertEquals("RyuResource should contain 1 capability.", 1, ryuResource.getCapabilities().size());
		Assert.assertTrue("RyuResource should contain MonitoringModuleCapability.",
				ryuResource.getCapabilities().get(0) instanceof IMonitoringModuleCapability);
	}

	@Test
	public void alarmRegistrationTest() throws ResourceException {
		IMonitoringModuleCapability monitoringModuleCapab = (IMonitoringModuleCapability) ryuResource
				.getCapabilityByInterface(IMonitoringModuleCapability.class);

		IAlarmObserver alarmObserver = new SampleAlarmObserver();
		Assert.assertFalse("Sample alarm observer should have not been notified yet.", ((SampleAlarmObserver) alarmObserver).wasNotified());

		monitoringModuleCapab.registerAlarmObservation(ALARM_DPID, ALARM_PORT, ALARM_THRESHOLD, alarmObserver);
		Assert.assertFalse("Sample alarm observer should have not been notified yet.", ((SampleAlarmObserver) alarmObserver).wasNotified());

		IMonitoringModuleCallbackAPI alarmCallbackClient = createAlarmCallbackClient((MonitoringModuleCapability) monitoringModuleCapab);
		alarmCallbackClient.alarmReceived(ALARM_DPID, ALARM_PORT);

		Assert.assertTrue("Sample alarm observer should have been notified.", ((SampleAlarmObserver) alarmObserver).wasNotified());
		monitoringModuleCapab.unregisterAlarmObservation(ALARM_DPID, ALARM_PORT);

		try {

			// set notified to false to check if the alarm raises.
			((SampleAlarmObserver) alarmObserver).notified = false;

			alarmCallbackClient.alarmReceived(ALARM_DPID, ALARM_PORT);

		} catch (WebApplicationException w) {
			// expected exception, since there's no registered alarm observer for such port and dpid.
		}

		Assert.assertFalse("Sample alarm observer should have not been notified after unregistration.",
				((SampleAlarmObserver) alarmObserver).wasNotified());

	}

	private void startResource() throws ResourceException, ProtocolException {
		CapabilityDescriptor capabDesc = new CapabilityDescriptor();
		capabDesc.setCapabilityInformation(new Information(MonitoringModuleCapability.CAPABILITY_TYPE, "ryu", "1.1"));

		ResourceDescriptor resourceDesc = new ResourceDescriptor();
		resourceDesc.setInformation(new Information("ryu", "ryuResource", "1.0.0"));
		resourceDesc.setCapabilityDescriptors(Arrays.asList(capabDesc));

		ryuResource = resourceManager.createResource(resourceDesc);

		// If not exists the protocol session manager, it's created and add the session context
		InitializerTestHelper.addSessionContext(protocolManager, ryuResource.getResourceIdentifier().getId(), RESOURCE_URI,
				HttpProtocolSession.HTTP_PROTOCOL_TYPE, null);

		resourceManager.startResource(ryuResource.getResourceIdentifier());

	}

	private IMonitoringModuleCallbackAPI createAlarmCallbackClient(MonitoringModuleCapability capability) {

		ProxyClassLoader classLoader = new ProxyClassLoader();
		classLoader.addLoader(JAXRSClientFactoryBean.class.getClassLoader());
		classLoader.addLoader(IMonitoringModuleCallbackAPI.class.getClassLoader());

		JAXRSClientFactoryBean bean = new JAXRSClientFactoryBean();
		bean.setAddress("http://localhost:8888" + capability.URL_WITH_RESOURCE);

		bean.setResourceClass(IMonitoringModuleCallbackAPI.class);
		bean.setClassLoader(classLoader);

		return bean.create(IMonitoringModuleCallbackAPI.class);
	}

	class SampleAlarmObserver implements IAlarmObserver {

		boolean	notified;

		@Override
		public void alarmReceived() {
			notified = true;
		}

		public boolean wasNotified() {
			return notified;
		}
	}

	private void mockServer() throws IOException, JSONException {

		String expectedJson = IOUtils.toString(this.getClass().getResourceAsStream(REQUEST_FILE));
		JSONObject json = new JSONObject(expectedJson);
		json.put(URL_PREFFIX_KEY, URL_PREFFIX_VALUE + ryuResource.getResourceIdentifier().getId());

		WireMock.stubFor(
				WireMock.post(
						WireMock.urlEqualTo(CLIENT_POST_URL))
						.withHeader("Content-Type", WireMock.equalTo(MediaType.APPLICATION_JSON))
						.withRequestBody(WireMock.equalToJson(json.toString()))
						.willReturn(WireMock.aResponse()
								.withStatus(HttpStatus.SC_OK)
						)
				);

		WireMock.stubFor(
				WireMock.delete(
						WireMock.urlEqualTo(CLIENT_DEL_URL))
						.willReturn(WireMock.aResponse()
								.withStatus(HttpStatus.SC_OK)
						)
				);
	}
}
