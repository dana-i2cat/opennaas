package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import junit.framework.Assert;
import luminis.mock.MockSessionFactory;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolManager;
import org.opennaas.core.protocols.sessionmanager.impl.ProtocolSessionManager;
import org.opennaas.core.resources.alarms.CapabilityAlarm;
import org.opennaas.core.resources.alarms.SessionAlarm;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
public class AlarmsInProtocolSessionManagerTest extends AbstractIntegrationTest implements EventHandler {

	static Log			log				= LogFactory.getLog(AlarmsInProtocolSessionManagerTest.class);

	final Object		lock			= new Object();
	List<Event>			receivedEvents	= new ArrayList<Event>();

	IEventManager		eventManager;
	IProtocolManager	protocolManager;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("org.opennaas").artifactId(
						"opennaas-core-events"),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	private void initBundles() {
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		eventManager = getOsgiService(IEventManager.class, 5000);
		protocolManager = getOsgiService(IProtocolManager.class, 5000);
	}

	@Test
	public void checkHandleSessionAlarmTriggersCapabilityAlarmTest() {

		initBundles();

		try {

			String resourceId = "TestResourceId";

			IProtocolSessionFactory factory = new MockSessionFactory();
			Map serviceProperties = new HashMap<String, String>();
			serviceProperties.put(ProtocolSessionContext.PROTOCOL, "mock");

			((ProtocolManager) protocolManager).sessionFactoryAdded(factory, serviceProperties);

			// create session
			ProtocolSessionContext sessionContext = newWonesysSessionContextMock();

			ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManagerWithContext(resourceId,
					sessionContext);
			IProtocolSession session = sessionManager.obtainSession(sessionContext, false);

			// register this as ResourceAlarm listener
			int registrationNum = registerAsCapabilityAlarmListener(this);

			receivedEvents.clear();

			// generate Session Alarm
			Event newSessionAlarm = generateAlarm(session);

			// sessionManager should handle SessionAlarm and generate a CapabilityAlarm
			sessionManager.handleEvent(newSessionAlarm);

			// check CapabilityAlarm has arrived in less than 30s
			synchronized (lock) {
				try {
					lock.wait(30000);
					Assert.assertFalse(receivedEvents.isEmpty());
				} catch (InterruptedException e) {
					Assert.fail("Interrupted while waiting for events");
				}
			}

			for (Event alarm : receivedEvents) {
				Assert.assertEquals(resourceId, (String) alarm.getProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY));
				Event sessionAlarm = (Event) alarm.getProperty(CapabilityAlarm.CAUSE_PROPERTY);
				Assert.assertTrue(sessionAlarm instanceof SessionAlarm);
				Assert.assertEquals(session.getSessionId(), sessionAlarm.getProperty(SessionAlarm.SESSION_ID_PROPERTY));
			}

			unregisterAsListener(registrationNum);

			sessionManager.destroyProtocolSession(session.getSessionId());

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	/**
	 * Check a TransportAlarm is transformed into a ResourceAlarm
	 */
	@Test
	public void checkSessionAlarmTriggersCapabilityAlarmTest2() {

		initBundles();

		try {

			String resourceId = "TestResourceId";

			IProtocolSessionFactory factory = new MockSessionFactory();
			Map serviceProperties = new HashMap<String, String>();
			serviceProperties.put(ProtocolSessionContext.PROTOCOL, "mock");

			((ProtocolManager) protocolManager).sessionFactoryAdded(factory, serviceProperties);

			// create session
			ProtocolSessionContext sessionContext = newWonesysSessionContextMock();

			IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManagerWithContext(resourceId,
					sessionContext);
			IProtocolSession session = sessionManager.obtainSession(sessionContext, false);

			// register this as ResourceAlarm listener
			int registrationNum = registerAsCapabilityAlarmListener(this);

			receivedEvents.clear();

			// generate Session Alarm
			generateAndPublishAlarm(session);

			// check CapabilityAlarm has arrived in less than 30s
			// sessionManager should handle SessionAlarm and generate a CapabilityAlarm
			synchronized (lock) {
				try {
					lock.wait(30000);
					Assert.assertFalse(receivedEvents.isEmpty());
				} catch (InterruptedException e) {
					Assert.fail("Interrupted while waiting for events");
				}
			}

			for (Event alarm : receivedEvents) {
				Assert.assertEquals(resourceId, (String) alarm.getProperty(CapabilityAlarm.RESOURCE_ID_PROPERTY));
				Event sessionAlarm = (Event) alarm.getProperty(CapabilityAlarm.CAUSE_PROPERTY);
				Assert.assertTrue(sessionAlarm instanceof SessionAlarm);

				Assert.assertEquals(session.getSessionId(), sessionAlarm.getProperty(SessionAlarm.SESSION_ID_PROPERTY));
			}

			unregisterAsListener(registrationNum);

			sessionManager.destroyProtocolSession(session.getSessionId());

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	private int registerAsCapabilityAlarmListener(EventHandler handler) {
		EventFilter filter = new EventFilter(CapabilityAlarm.TOPIC);
		return eventManager.registerEventHandler(handler, filter);
	}

	private void unregisterAsListener(int registrationNum) {
		eventManager.unregisterHandler(registrationNum);
	}

	@Override
	public void handleEvent(Event event) {
		synchronized (lock) {
			receivedEvents.add(event);
			lock.notifyAll();
		}
	}

	/**
	 * Generates a WonesysAlarm
	 *
	 * @param session
	 */
	private void generateAndPublishAlarm(IProtocolSession session) {
		log.debug("Generating SessionAlarm in session: " + session.getSessionId());
		eventManager.publishEvent(generateAlarm(session));
	}

	/**
	 * Generates a WonesysAlarm
	 *
	 * @param session
	 */
	private Event generateAlarm(IProtocolSession session) {

		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put(SessionAlarm.SESSION_ID_PROPERTY, session.getSessionId());

		SessionAlarm alarm = new SessionAlarm(prop);

		return alarm;
	}

	/**
	 * Configure the protocol to connect
	 */
	private ProtocolSessionContext newWonesysSessionContextMock() {

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();

		protocolSessionContext.addParameter(
				"protocol.mock", "true");
		protocolSessionContext.addParameter(ProtocolSessionContext.PROTOCOL,
				"mock");
		// ADDED
		return protocolSessionContext;
	}

}
