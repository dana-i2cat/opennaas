package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import java.io.IOException;
import java.util.Properties;
import java.util.Timer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wonesys.emsModule.alarms.AlarmsControler;

/**
 *
 *
 * @author isart
 *
 */
public class WonesysAlarmConfigurator implements IWonesysAlarmConfigurator {

	/** The logger **/
	Log							log						= LogFactory.getLog(WonesysAlarmConfigurator.class);

	public static final int		DEFAULT_ALARMS_PORT		= 162;													// SNMP traps wellknown
																												// port
	public static final long	DEFAULT_ALARM_WAITTIME	= 20 * 1000;											// 20 sec

	public static final String	UNKNOWN_ALARM_STATUS	= "UNKNOWN";
	public static final String	CONFIGURED_ALARM_STATUS	= "CONFIGURED";
	public static final String	ACTIVE_ALARM_STATUS		= "ACTIVE";

	/**
	 * Local port to receive SNMP traps through.
	 */
	private Integer				alarmsPort;
	private Long				alarmWaitTime;
	private AlarmsControler		acontroller;

	private Timer				scheduler;

	private String				status					= "UNKNOWN";

	public WonesysAlarmConfigurator() {
		this.alarmsPort = DEFAULT_ALARMS_PORT;
		this.alarmWaitTime = DEFAULT_ALARM_WAITTIME;
		this.status = "CONFIGURED";
	}

	/**
	 * Allows alarms configuration through given properties.
	 */
	public void configureAlarms(Properties properties) {
		if (status.equals("ACTIVE")) {
			log.warn("Ignoring alarm configuration. Alarm harvesting is already active.");
		} else {
			String port_tmp = properties.getProperty(ALARM_PORT_PROPERTY_NAME);
			this.alarmsPort = Integer.parseInt(port_tmp);

			String alarmWaitTime_tmp = properties.getProperty(ALARM_WAITTIME_PROPERTY_NAME);
			this.alarmWaitTime = Long.parseLong(alarmWaitTime_tmp);
			status = "CONFIGURED";
		}
	}

	public void enableAlarms() throws IOException {
		if (status.equals("ACTIVE")) {
			log.debug("Ignoring alarm activation. Alarm harvesting is already active.");
		} else {
			// create alarm listener
			// (opens a socket to given port and listens there)
			this.acontroller = new AlarmsControler();
			this.acontroller.createAlarmListener(alarmsPort);

			// start alarm sync requester
			// periodically asks for alarms received in previously opened port
			this.scheduler = new Timer();
			WonesysAlarmRequester requester = new WonesysAlarmRequester(
					this.acontroller);
			this.scheduler.schedule(requester, 0, this.alarmWaitTime);
			status = "ACTIVE";
			log.info("Wonesys alarm harvesting active on port " + alarmsPort);
		}
	}

	public void disableAlarms() {
		scheduler.cancel();
		status = "CONFIGURED";
		log.debug("Wonesys alarm harvesting stoped");
	}

	public String getStatus() {
		return status;
	}

}
