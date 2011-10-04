package net.i2cat.luminis.actionsets.wonesys.actions;

import java.util.Properties;

import net.i2cat.luminis.actionsets.wonesys.ActionConstants;
import net.i2cat.luminis.actionsets.wonesys.Activator;
import net.i2cat.luminis.protocols.wonesys.alarms.WonesysAlarm;
import net.i2cat.nexus.events.EventFilter;
import net.i2cat.nexus.events.IEventManager;
import net.i2cat.nexus.resources.ActivatorException;
import net.i2cat.nexus.resources.action.Action;
import net.i2cat.nexus.resources.action.ActionException;
import net.i2cat.nexus.resources.action.ActionResponse;
import net.i2cat.nexus.resources.protocol.IProtocolSession;
import net.i2cat.nexus.resources.protocol.IProtocolSessionManager;
import net.i2cat.nexus.resources.protocol.ProtocolException;

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
			filterProperties.put(WonesysAlarm.SESSION_ID_PROPERTY, session.getSessionId());
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
