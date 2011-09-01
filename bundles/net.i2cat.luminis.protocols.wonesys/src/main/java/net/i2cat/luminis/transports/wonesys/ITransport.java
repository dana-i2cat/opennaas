package net.i2cat.luminis.transports.wonesys;

public interface ITransport {

	public Object sendMsg(Object message) throws Exception;

	public void connect() throws Exception;

	public void disconnect() throws Exception;
}
