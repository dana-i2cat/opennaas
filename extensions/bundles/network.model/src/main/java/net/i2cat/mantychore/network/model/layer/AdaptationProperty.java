package net.i2cat.mantychore.network.model.layer;

import net.i2cat.mantychore.network.model.topology.ConnectionPoint;

public class AdaptationProperty {

	ConnectionPoint clientInterface;
	ConnectionPoint serverInterface;

	public ConnectionPoint getClientInterface() {
		return clientInterface;
	}

	public void setClientInterface(ConnectionPoint clientInterface) {
		this.clientInterface = clientInterface;
	}

	public ConnectionPoint getServerInterface() {
		return serverInterface;
	}

	public void setServerInterface(ConnectionPoint serverInterface) {
		this.serverInterface = serverInterface;
	}
}
