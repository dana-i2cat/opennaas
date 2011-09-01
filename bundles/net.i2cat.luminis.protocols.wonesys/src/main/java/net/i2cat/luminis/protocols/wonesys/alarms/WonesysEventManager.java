package net.i2cat.luminis.protocols.wonesys.alarms;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolBundleActivator;
import net.i2cat.nexus.events.EventManager;

import org.osgi.framework.BundleContext;

public class WonesysEventManager extends EventManager {

	@Override
	public BundleContext getBundleContext() {
		return WonesysProtocolBundleActivator.getBundleContext();
	}

}
