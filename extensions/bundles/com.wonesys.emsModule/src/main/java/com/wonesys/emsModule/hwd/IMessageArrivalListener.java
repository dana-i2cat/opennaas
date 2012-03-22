package com.wonesys.emsModule.hwd;

public interface IMessageArrivalListener {

	public void messageReceived(String message);

	public void errorHappened(Exception e);

}
