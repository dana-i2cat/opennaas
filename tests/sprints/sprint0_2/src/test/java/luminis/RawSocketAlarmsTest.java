package luminis;

import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;

import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;
import net.i2cat.luminis.actionsets.wonesys.WonesysAlarmsDriver;
import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSession;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmFactory;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;
import net.i2cat.mantychore.model.utils.OpticalSwitchFactory;
import net.i2cat.nexus.tests.IntegrationTestsHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.testing.AbstractIntegrationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.alarms.ResourceAlarm;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
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
	public static final String	ARRIVAL_TIME_PROPERTY_NAME	= RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME;

	private IEventManager		eventManager;

	@Inject
	BundleContext				bundleContext				= null;

	private final Object		lock						= new Object();

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

	public void initBundles() {

		IntegrationTestsHelper.waitForAllBundlesActive(bundleContext);

		eventManager = getOsgiService(IEventManager.class, 2000);
	}

	private TestInitInfo setUp() throws ProtocolException {

		initBundles();

		String sessionID = "session1";

		String alarmTopic = WonesysAlarm.TOPIC;
		Properties properties = new Properties();
		properties.put(WonesysAlarm.SESSION_ID_PROPERTY, sessionID);

		EventFilter filter = new EventFilter(alarmTopic);
		int regNum = eventManager.registerEventHandler(this, filter);

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;

		session = new WonesysProtocolSession(protocolSessionContext, sessionID);
		session.connect();

		TestInitInfo initInfo = new TestInitInfo();
		initInfo.session = session;
		initInfo.regNum = regNum;

		return initInfo;
	}

	private void tearDown(TestInitInfo initInfo) throws ProtocolException {
		eventManager.unregisterHandler(initInfo.regNum);
		initInfo.session.disconnect();
	}

	/**
	 * Test to group all tests in this class, loading container only once.
	 * 
	 * @throws ProtocolException
	 */
	@Test
	public void allRawSocketAlarmsTest() throws ProtocolException {

		TestInitInfo initInfo = setUp();

		try {
			alarmsReceivedTest(initInfo);
			checkAlarmsAndCommandsTest(initInfo);
			// checkAllAlarmsAreSupportedTest();
			// checkEventChannelConfigChangedTest();
			// checkAlarmsStoredInAlarmHistory();
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		} finally {
			tearDown(initInfo);
		}
	}

	/**
	 * Test that WonesysProtocol receives alarms correctly from transport and notifies listeners upon alarm reception.
	 */
	public void alarmsReceivedTest(TestInitInfo initInfo) {

		try {

			alarmReceived = false;

			generateAlarm(initInfo.session);

			synchronized (lock) {
				lock.wait(3000);
				// check that the alarm is received & listeners are notified
				Assert.assertTrue(alarmReceived);
			}

			alarmReceived = false;
			alarmCounter = 0;
			receivedAlarms.clear();

		} catch (InterruptedException e) {
			Assert.fail("Interrupted!");
		}
	}

	/**
	 * Tests that WonesysProtocolSession distinguishes between alarms and command responses, and only rises and alarm when alarms are received.
	 */
	public void checkAlarmsAndCommandsTest(TestInitInfo initInfo) {

		try {

			alarmReceived = false;
			alarmCounter = 0;
			receivedAlarms.clear();

			generateAlarm(initInfo.session);

			synchronized (lock) {
				lock.wait(3000);
				// check that the alarm is received & listeners are notified
				Assert.assertTrue(alarmReceived);
			}

			alarmReceived = false;

			// generate command response
			// FIXME XOR is incorrect
			String commandResponse = "59100117FFFF0B02FFFFFFFF0100000100"; // Set channel resp (OK)
			generateRawSocketEvent(initInfo.session.getWonesysTransport().getTransportID(), commandResponse);

			synchronized (lock) {
				lock.wait(3000);
				// check that no alarm has been received
				Assert.assertFalse(alarmReceived);
			}

			Assert.assertTrue(alarmCounter == 1);

			alarmReceived = false;
			alarmCounter = 0;
			receivedAlarms.clear();

		} catch (InterruptedException e) {
			Assert.fail("Interrupted!");
		}
	}

	@Test
	public void checkAllAlarmsAreSupportedTest() {
		String chassis = "01";
		String slot = "01";

		OpticalSwitchFactory factory = new OpticalSwitchFactory();
		IModel model = null;
		try {
			model = factory.newPedrosaProteusOpticalSwitch();
		} catch (Exception e) {
			Assert.fail(e.getLocalizedMessage());
		}

		// Create one alarm message of each known type
		List<String> alarmMessages = new ArrayList<String>();
		alarmMessages.add("FFFF0000" + chassis + slot + "01FF80"); // CPLANCHANGED

		// Check alarms are known
		WonesysAlarm alarm;
		for (String alarmMessage : alarmMessages) {
			Properties properties = WonesysAlarmFactory.loadAlarmProperties(alarmMessage);
			alarm = WonesysAlarmFactory.createAlarm(properties);

			boolean recognized = false;
			ResourceAlarm generatedAlarm = WonesysAlarmsDriver.wonesysAlarmToResourceAlarm(alarm, model, "testResource01");
			if (!generatedAlarm.getProperty(ResourceAlarm.ALARM_CODE_PROPERTY).equals("UNKNOWN")) {
				recognized = true;
			}
			Assert.assertTrue(recognized);
		}

		// check an unknown alarm is created
		String unknownAlarmMessage = "FFFF0000FFFFFFFFFFFF";
		Properties properties = WonesysAlarmFactory.loadAlarmProperties(unknownAlarmMessage);
		alarm = WonesysAlarmFactory.createAlarm(properties);

		boolean recognized = false;
		ResourceAlarm generatedAlarm = WonesysAlarmsDriver.wonesysAlarmToResourceAlarm(alarm, model, "testResource01");
		if (!generatedAlarm.getProperty(ResourceAlarm.ALARM_CODE_PROPERTY).equals("UNKNOWN")) {
			recognized = true;
		}
		Assert.assertFalse(recognized);
	}

	/**
	 * Already tested in first test
	 */
	// @Test //already tested
	public void checkAlarmsNotifyEventTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// throw notify event
		// check that it is received by listeners
		// }

	}

	// @Test //functionality not implemented (alarms don't trigger actions automatically)
	public void checkEventChannelConfigChangedTest() {
		String chassis = "01";
		String slot = "01";

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

			String message = "FFFF0000" + chassis + slot + "01FF80";

			generateRawSocketEvent(session.getWonesysTransport().getTransportID(), message);

			// TODO check model has been refreshed

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}
	}

	// @Test //uncompleted test
	public void checkAlarmsStoredInAlarmHistory() {
		String chassis = "01";
		String slot = "17";

		// initBundles();

		// create resource with chassis and slot
		// create protocol context
		// start resource
		// launch channelConfigChanged alarm
		// check alarm is stored

		ProtocolSessionContext protocolSessionContext = new ProtocolSessionContext();
		protocolSessionContext.addParameter("protocol.mock", "true");

		WonesysProtocolSession session;
		try {
			session = new WonesysProtocolSession(protocolSessionContext, "session1");

			session.connect();

			String alarmMessage = "FFFF0000" + chassis + slot + "01FF80";

			generateRawSocketEvent(session.getWonesysTransport().getTransportID(), alarmMessage);

			// TODO check alarm history for given device contains generated alarm

			session.disconnect();

		} catch (ProtocolException e) {
			e.printStackTrace();
			Assert.fail(e.getLocalizedMessage());
		}

	}

	@Override
	public void handleEvent(Event event) {
		// TODO check received event is the alarm we have just triggered
		synchronized (lock) {
			alarmReceived = true;
			alarmCounter++;
			receivedAlarms.add((WonesysAlarm) event);
			lock.notify();
		}
	}

	private void generateAlarm(WonesysProtocolSession session) {
		String alarmMessage = "FFFF0100011700FF0300"; // PSROADM ERROR
		generateRawSocketEvent(session.getWonesysTransport().getTransportID(), alarmMessage);
	}

	private void generateRawSocketEvent(String transportId, String message) {

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(MESSAGE_PROPERTY_NAME, message);
		properties.put(TRANSPORT_ID_PROPERTY_NAME, transportId);
		long creationTime = new Date().getTime();
		properties.put(ARRIVAL_TIME_PROPERTY_NAME, creationTime);

		Event event = new Event(MSG_RCVD_EVENT_TOPIC, properties);

		eventManager.publishEvent(event);
		log.debug("RawSocketTransport Event generated! " + message + " at " + creationTime);
	}

	class TestInitInfo {
		public WonesysProtocolSession	session;
		public int						regNum;
	}

}
