public class RawSocketAlarmsTest {

	public void alarmsReceivedTest() {
		// open socket to the device
		// generate alarm
		// check that the alarm is received
		// check listeners are notified
	}

	public void checkAlarmsAndCommandsTest() {
		// open session
		// simulate an alarm is received
		// check that the alarm hook is executed

		// simulate a response is received
		// check command response hook is executed
	}

	public void checkAllAlarmsAreSupportedTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// }
		// It includes unknown alarms

	}

	public void checkAlarmsNotifyEventTest() {
		// open session
		// while (Alarm alarmInLuminis: allAlarmsInLuminis) {
		// simulate alarm
		// check that the alarm is known
		// throw notify event
		// check that it is received by listeners
		// }

	}

	public void checkNotifyEventChannelConfigChangedTest() {
		// throw notify event for ChannelConfigChanged
		// check that the refreshed model is executed

	}

}
