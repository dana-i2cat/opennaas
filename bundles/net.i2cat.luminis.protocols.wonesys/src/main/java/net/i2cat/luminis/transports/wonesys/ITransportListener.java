package net.i2cat.luminis.transports.wonesys;

public interface ITransportListener {

	/**
	 * Event telling a message has been received.
	 * 
	 * @param message
	 */
	public void messageReceived(Object message);

	public void errorHappened(Exception e);
}
