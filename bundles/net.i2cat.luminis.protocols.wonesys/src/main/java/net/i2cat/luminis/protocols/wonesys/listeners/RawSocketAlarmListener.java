package net.i2cat.luminis.protocols.wonesys.listeners;

import java.util.Date;
import java.util.Properties;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolBundleActivator;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarmFactory;
import net.i2cat.luminis.transports.wonesys.rawsocket.RawSocketTransport;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.alarms.SessionAlarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

public class RawSocketAlarmListener implements EventHandler {

	public static Log	log	= LogFactory.getLog(RawSocketAlarmListener.class);

	private String		sessionId;
	
	private long creationTime;

	public RawSocketAlarmListener(String sessionID) {
		this.sessionId = sessionID;
		creationTime = new Date().getTime();
		
		log.info("Created RawSocketAlarmListener for session: " + sessionId);
	}

	@Override
	public void handleEvent(Event event) {
		// FIXME: remove log message
		log.info("AlarmListener received a message!");
		String message = (String) event.getProperty(RawSocketTransport.MESSAGE_PROPERTY_NAME);
		if (message != null) {
			if (isAlarm(message)) {
				
				boolean isOld = false;
				Long arrivalTime = (Long) event.getProperty(RawSocketTransport.ARRIVAL_TIME_PROPERTY_NAME);
				if (arrivalTime != null) {
					if (arrivalTime < creationTime)
						isOld = true;
				}
				
				//Only publish if event arrived after listener creation
				if (!isOld){
					try {
						log.info("Session received an alarm: " + message + " created in " + arrivalTime);
						createAndPublishAlarm(message);
					} catch (ProtocolException e) {
						log.error("Error publishing received alarm: " + message + ". Received alarm will be unavailable for the rest of the system", e);
					}
				} else {
					log.debug("Skipping old alarm: " + message);
				}
			}
		}
	}

	private boolean isAlarm(String message) {
		if (message.substring(0, 4).equalsIgnoreCase("ffff")
				&& message.substring(14, 16).equalsIgnoreCase("ff"))
			return true;

		return false;
	}

	private void createAndPublishAlarm(String alarmMessage) throws ProtocolException {

		Properties properties = WonesysAlarmFactory.loadAlarmProperties(alarmMessage);

		properties.setProperty(SessionAlarm.SESSION_ID_PROPERTY, sessionId);

		WonesysAlarm alarm = WonesysAlarmFactory.createAlarm(properties);
		publishEvent(alarm);
	}

	private void publishEvent(Event event) throws ProtocolException {
		try {
			WonesysProtocolBundleActivator.getEventManagerService().publishEvent(event);
		} catch (ActivatorException e) {
			throw new ProtocolException("Failed to publish alarm", e);
		}
	}

}
