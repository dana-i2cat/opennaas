package net.i2cat.luminis.protocols.wonesys.alarms;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.event.Event;

import com.wonesys.emsModule.alarms.Alarm;

public class WonesysAlarmEvent extends Event {
	
	public static final String TYPEID_ALARM_PROPERTY = "typeID";
	public static final String RECEPTIONDATE_ALARM_PROPERTY = "receptionDate";
	public static final String IP_ALARM_PROPERTY = "ip";
	public static final String SLOT_ALARM_PROPERTY = "slot";
	public static final String PORT_ALARM_PROPERTY = "port";
	public static final String DESCRIPTION_ALARM_PROPERTY = "description";
	public static final String SEVERITY_ALARM_PROPERTY = "severity";
	
	public static final String WONESYS_ALARM_EVENT_TOPIC = "com/wonesys/emsModule/alarms/RECEIVED";
		
	private Alarm alarm;

	
	public WonesysAlarmEvent(Alarm a) {
		
		super(WONESYS_ALARM_EVENT_TOPIC, getAlarmProperties(a));
		this.alarm = a;
	}
	
	private static Dictionary<String, Object> getAlarmProperties(Alarm a) {
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(TYPEID_ALARM_PROPERTY, a.getTypeID());
		properties.put(RECEPTIONDATE_ALARM_PROPERTY, a.getDataRecepcio());
		properties.put(IP_ALARM_PROPERTY, a.getIp());
		properties.put(SLOT_ALARM_PROPERTY, a.getSlot());
		properties.put(PORT_ALARM_PROPERTY, a.getPort());
		properties.put(DESCRIPTION_ALARM_PROPERTY, a.getRawIndo());
		properties.put(SEVERITY_ALARM_PROPERTY, a.getSeverity());
		
		return properties;
	}
	
	public Alarm getAlarm(){
		return this.alarm;
	}
}
