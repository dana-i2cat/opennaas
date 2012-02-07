package net.i2cat.luminis.protocols.wonesys.alarms;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolBundleActivator;
import org.opennaas.core.events.EventManager;

import org.osgi.framework.BundleContext;

public class WonesysEventManager extends EventManager {

	@Override
	public BundleContext getBundleContext() {
		return WonesysProtocolBundleActivator.getBundleContext();
	}

}
