package net.i2cat.luminis.protocols.wonesys.alarms;

import java.util.Date;
import java.util.Properties;

import com.wonesys.emsModule.alarms.Alarm;

public class WonesysAlarmFactory {

	// public static WonesysAlarm createAlarm(String alarmMessage) {
	// // TODO auto-generated stub method
	// return null;
	// }

	public static WonesysAlarm createAlarm(Properties properties) {
		return new WonesysAlarm(properties);
	}

	public static WonesysAlarm createAlarm(Alarm alarm) {
		// TODO auto-generated stub method
		return null;
	}

	/* HELPERS */

	public static Properties loadAlarmProperties(String message) {

		// Alarm format:
		// Reserved (2B) + data length (2B) + elementId (2B) + alarmType (1B) + Reserved (1B) + alarmId (1B) + Data (var)

		String dataLengthS = message.substring(4, 8);
		int dataLength = Integer.parseInt(convertLittleBigEndian(dataLengthS), 16);

		String elementId = message.substring(8, 12);
		int chasis = Integer.parseInt(elementId.substring(0, 2));
		int slot = Integer.parseInt(elementId.substring(2, 4));

		String alarmType = message.substring(12, 14);
		int severity = Integer.parseInt(alarmType, 16);

		String alarmId = message.substring(16, 18);

		String data = "";
		if (message.length() > 18)
			data = message.substring(18, 18 + dataLength * 2);

		Properties properties = new Properties();
		properties.put(WonesysAlarm.CHASSIS_PROPERTY, chasis);
		properties.put(WonesysAlarm.SLOT_PROPERTY, slot);
		properties.put(WonesysAlarm.ALARM_ID_PROPERTY, alarmId);
		properties.put(WonesysAlarm.SEVERITY_PROPERTY, severity);
		properties.put(WonesysAlarm.ALARM_DATA_PROPERTY, data);
		properties.put(WonesysAlarm.ARRIVAL_TIME, (new Date()).getTime());

		return properties;
	}

	/**
	 * changes the order of bytes in the given hex string. For given 0,1,2,...,n returns n,...,2,1,0
	 */
	private static String convertLittleBigEndian(String value) {

		StringBuilder builder = new StringBuilder();

		int totalBytes = value.length() / 2;
		for (int i = totalBytes; i > 0; i--) {
			builder.append(value.substring(i * 2 - 2, i * 2));
		}
		return builder.toString();
	}

}
