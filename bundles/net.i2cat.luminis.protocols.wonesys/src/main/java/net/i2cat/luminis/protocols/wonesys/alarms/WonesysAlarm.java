package net.i2cat.luminis.protocols.wonesys.alarms;

public class WonesysAlarm {

	public enum Type {
		UNKNOWN, OTHER;
	}

	private Type	typeID;

	public Type getTypeID() {
		return typeID;
	}

}
