package org.opennaas.extensions.roadm.wonesys.transports;

public interface ITransport {

	public Object sendMsg(Object message) throws Exception;

	public void sendAsync(Object message) throws Exception;

	public void connect() throws Exception;

	public void disconnect() throws Exception;

	// public void registerListener(ITransportListener listener);
	//
	// public void unregisterListener(ITransportListener listener);

	public String getTransportID();
}
