package org.opennaas.extensions.transport.virtual.tests;

import org.opennaas.extensions.transports.virtual.IVirtualTransportProvider;

public class MockVirtualTransportProvider implements IVirtualTransportProvider {
	
	public final static String response = "M OTWA3OME1 COMPLD";

	public Object getMessageTransportResponse(Object request) {
		return response;
	}

	public byte[] getStreamTransportReponse(byte[] request) {
		return response.getBytes();
	}
	
}
