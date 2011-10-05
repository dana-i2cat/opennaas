package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;
import net.i2cat.nexus.events.EventFilter;
import net.i2cat.nexus.events.IEventManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

@RunWith(JUnit4TestRunner.class)
public class RawSocketAlarmsTest extends AbstractIntegrationTest implements EventHandler {

	public static Log			log							= LogFactory.getLog(RawSocketAlarmsTest.class);

	private boolean				alarmReceived				= false;
	private int					alarmCounter				= 0;
	private List<WonesysAlarm>	receivedAlarms				= new ArrayList<WonesysAlarm>();

	public static final String	MSG_RCVD_EVENT_TOPIC		= RawSocketTransport.MSG_RCVD_EVENT_TOPIC;
	public static final String	MESSAGE_PROPERTY_NAME		= RawSocketTransport.MESSAGE_PROPERTY_NAME;
	public static final String	TRANSPORT_ID_PROPERTY_NAME	= RawSocketTransport.TRANSPORT_ID_PROPERTY_NAME;

	private IEventManager		eventManager;

	@Inject
	BundleContext				bundleContext				= null;

	@Configuration
	public static Option[] configuration() throws Exception {

		Option[] options = combine(
				IntegrationTestsHelper.getLuminisTestOptions(),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.events"),
				mavenBundle().groupId("net.i2cat.nexus").artifactId(
						"net.i2cat.nexus.tests.helper")
				// , vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005")
				);

		return options;
	}

	public void initBundles() {
		
		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);
		
		eventManager = getOsgiService(IEventManager.class, 2000);
	}

	/**
	 * Test to group all tests in this class, loading container only once.
	 */
	@Test
	public void allRawSocketAlarmsTest() {

		initBundles();

		alarmsReceivedTest();
		checkAlarmsAndCommandsTest();
		// checkAllAlarmsAreSupportedTest();
		// checkEventChannelConfigChangedTest();
		// checkAlarmsStoredInAlarmHistory();

	}

	/**
	 * Test that WonesysProtocol receives alarms correctly from transport and notifies listeners upon alarm reception.
	 */
	// @Test
	public void alarmsReceivedTest() {

		// initBundles();
		String sessionID = "session1";

		String alarmTopic = WonesysProtocolSession.ALARM_RCVD_EVENT_TOPIC;
		Properties properties = new Properties();
		properties.put(WonesysProtocolSession.SESSION_ID_PROPERTY, sessionID);

		EventFilter filter = new EventFilter(alarmTopic);
		eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, sessionID);
			session.connect();

			alarmReceived = false;

			generateAlarm(session);

			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);

			session.disconnect();

			alarmReceived = false;

		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Assert.fail(e1.getLocalizedMessage());
		}
	}

	@Override
	public void handleEvent(Event event) {
		// TODO check received event is the alarm we have just triggered
		alarmReceived = true;
		alarmCounter++;
		receivedAlarms.add((WonesysAlarm) event);
	}

	/**
	 * Tests that WonesysProtocolSession distinguishes between alarms and command responses.
	 */
	// @Test
	public void checkAlarmsAndCommandsTest() {

		// initBundles();

		String sessionID = "session1";

		String alarmTopic = WonesysProtocolSession.ALARM_RCVD_EVENT_TOPIC;
		Properties properties = new Properties();
		properties.put(WonesysProtocolSession.SESSION_ID_PROPERTY, sessionID);

		EventFilter filter = new EventFilter(alarmTopic);
		eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, sessionID);
			session.connect();

			alarmReceived = false;

			generateAlarm(session);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// check that the alarm is received & listeners are notified
			Assert.assertTrue(alarmReceived);

			alarmReceived = false;

			// generate command response
			// FIXME XOR is incorrect
			String commandResponse = "59100117FFFF0B02FFFFFFFF0100000100"; // Set channel resp (OK)
			Dictionary<String, Object> properties1 = new Hashtable<String, Object>();
			properties1.put(MESSAGE_PROPERTY_NAME, commandResponse);
			Event event = new Event(MSG_RCVD_EVENT_TOPIC, properties1);

			eventManager.publishEvent(event);
			log.debug("Command response generated");

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// check that no alarm has been received
			Assert.assertFalse(alarmReceived);

		} catch (ProtocolException e2) {
			e2.printStackTrace();
			Assert.fail(e2.getLocalizedMessage());
		}
	}

	// @Test
	// public void checkAllAlarmsAreSupportedTest() {
	//
	// WonesysAlarmsReader alarmReader = new WonesysAlarmsReader();
	//
	// // TODO create one alarm message of each known type
	// List<String> alarmMessages = new ArrayList<String>();
	//
	// // Check alarms are known
	// WonesysAlarm alarm;
	// for (String alarmMessage : alarmMessages) {
	// Properties properties = WonesysAlarmFactory.loadAlarmProperties(alarmMessage);
	// alarm = WonesysAlarmFactory.createAlarm(properties);
	//
	// boolean recognized = false;
	// for (ProteusOpticalSwitchCard.CardType cardType : ProteusOpticalSwitchCard.CardType.values()) {
	// recognized = !WonesysAlarmReader.AlarmMeaning.UNKNOWN.equals(
	// alarmReader.getAlarmMeaning(cardType, alarm));
	// if (recognized)
	// break;
	// }
	// Assert.assertTrue(recognized);
	// }
	//
	// // check an unknown alarm is created
	// String unknownAlarmMessage = "FFFF0000FFFFFFFFFFFF";
	// Properties properties = WonesysAlarmFactory.loadAlarmProperties(unknownAlarmMessage);
	// alarm = WonesysAlarmFactory.createAlarm(properties);
	//
	// boolean recognized = false;
	// for (ProteusOpticalSwitchCard.CardType cardType : ProteusOpticalSwitchCard.CardType.values()) {
	// recognized = !WonesysAlarmReader.AlarmMeaning.UNKNOWN.equals(
	// alarmReader.getAlarmMeaning(cardType, alarm));
	// if (recognized)
	// break;
	// }
	// Assert.assertFalse(recognized);
	// }

	/**
	 * Already tested in first test
	 */
	// @Test
	public void checkAlarmsNotifyEventTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// throw notify event
		// check that it is received by listeners
		// }

	}

	// @Test
	public void checkEventChannelConfigChangedTest() {
		String chassis = "01";
		String slot = "17";

		// initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// manually change chanelConfig in MockProteus
		// launch channelConfigChanged alarm
		// check model has been updated

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			session.connect();

			String chassisSlot = "0117";
			String message = "FFFF0000" + chassis + slot + "01FF80";

			generateAlarm(session, message);

			// TODO check model has been refreshed

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	// @Test
	public void checkAlarmsStoredInAlarmHistory() {
		String chassis = "01";
		String slot = "17";

		// initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// manually change chanelConfig in MockProteus
		// launch channelConfigChanged alarm
		// check model has been updated

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			session.connect();

			String alarmMessage = "FFFF0000" + chassis + slot + "01FF80";

			generateAlarm(session, alarmMessage);

			// TODO check alarm history for given device contains generated alarm

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	private void generateAlarm(WonesysProtocolSession session) {
		String alarmMessage = "FFFF0100011700FF0300"; // PSROADM ERROR
		generateAlarm(session, alarmMessage);
	}

	private void generateAlarm(WonesysProtocolSession session, String message) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MESSAGE_PROPERTY_NAME, message);
		properties.put(TRANSPORT_ID_PROPERTY_NAME, session.getWonesysTransport().getTransportID());
		Event event = new Event(MSG_RCVD_EVENT_TOPIC, properties);

		eventManager.publishEvent(event);
		log.debug("Alarm generated!");
	}

}
