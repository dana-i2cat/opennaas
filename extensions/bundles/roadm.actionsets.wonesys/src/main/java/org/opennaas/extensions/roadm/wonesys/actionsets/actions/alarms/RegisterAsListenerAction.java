package org.opennaas.extensions.roadm.wonesys.actionsets.actions.alarms;

import java.util.Properties;

import org.opennaas.extensions.roadm.wonesys.actionsets.ActionConstants;
import org.opennaas.extensions.roadm.wonesys.actionsets.Activator;
import org.opennaas.extensions.roadm.wonesys.protocols.alarms.WonesysAlarm;
import org.opennaas.core.events.EventFilter;
import org.opennaas.core.events.IEventManager;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.Action;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.event.EventHandler;

public class RegisterAsListenerAction extends Action {

	static Log	log	= LogFactory.getLog(RegisterAsListenerAction.class);

	public RegisterAsListenerAction() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setActionID(ActionConstants.REGISTER);
	}

	@Override
	public ActionResponse execute(IProtocolSessionManager protocolSessionManager) throws ActionException {

		try {

			log.info("Registering as WonesysAlarm listener");
			Object[] newParams = (Object[]) params;
			EventHandler handler = (EventHandler) newParams[1];

			IProtocolSession session = protocolSessionManager.obtainSessionByProtocol("wonesys", false);

			// create filter to listen for WonesysAlarms coming from given wonesys session of given protocolSessionManager
			Properties filterProperties = new Properties();
			filterProperties.setProperty(WonesysAlarm.SESSION_ID_PROPERTY, session.getSessionId());
			EventFilter filter = new EventFilter(new String[] { WonesysAlarm.TOPIC }, filterProperties);

			// register alarmListener
			IEventManager eventManager = Activator.getEventManagerService();
			eventManager.registerEventHandler(handler, filter);

			log.info("Registered as WonesysAlarm listener!");

			return ActionResponse.okResponse(getActionID());

		} catch (ProtocolException e) {
			return ActionResponse.errorResponse(actionID, "Could not obtain wonesys session: " + e.getLocalizedMessage());
		} catch (ActivatorException e) {
			return ActionResponse.errorResponse(actionID, "Could not register handler: " + e.getLocalizedMessage());
		}

	}

	@Override
	public boolean checkParams(Object params) throws ActionException {

		if (params instanceof Object[]) {
			Object[] newParams = (Object[]) params;
			if (newParams.length == 2) {
				if (newParams[1] instanceof EventHandler)
					return true;
			}
		}

		return false;
	}

}
