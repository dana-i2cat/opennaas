package org.opennaas.extensions.roadm.wonesys.protocols.alarms;

import org.opennaas.core.events.EventManager;
import org.opennaas.extensions.roadm.wonesys.protocols.WonesysProtocolBundleActivator;
import org.osgi.framework.BundleContext;

public class WonesysEventManager extends EventManager {

	@Override
	public BundleContext getBundleContext() {
		return WonesysProtocolBundleActivator.getBundleContext();
	}

}
