package org.opennaas.extensions.genericnetwork.capability.nclprovisioner.components;

import java.net.URI;
import java.util.TimerTask;

/**
 * 
 * @author Adrián Roselló Rey (i2CAT)
 *
 */
public class NetworkStatisticsPoller extends TimerTask {

	private URI	slaManagerUri;

	public NetworkStatisticsPoller(URI slaManagerUri) {
		this.slaManagerUri = slaManagerUri;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
