package net.i2cat.luminis.transports.wonesys;

public class MockTransport implements ITransport {

	ProteusMock	proteusMock	= new ProteusMock();

	@Override
	public Object sendMsg(Object message) throws Exception {
		if (!(message instanceof String))
			throw new Exception("Incorrect format in message");
		return proteusMock.execCommand((String) message);
	}

	@Override
	public void connect() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void disconnect() throws Exception {
		// TODO Auto-generated method stub

	}

}
