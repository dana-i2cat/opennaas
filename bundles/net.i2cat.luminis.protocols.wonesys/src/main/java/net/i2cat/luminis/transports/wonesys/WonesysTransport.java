package net.i2cat.luminis.transports.wonesys;

import java.io.IOException;

import net.i2cat.luminis.protocols.wonesys.WonesysProtocolSessionContextUtils;
import net.i2cat.nexus.resources.protocol.ProtocolException;
import net.i2cat.nexus.resources.protocol.ProtocolSessionContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wonesys.emsModule.hwd.HwdOp;

public class WonesysTransport implements ITransport {

	/** The logger **/
	Log				log				= LogFactory.getLog(WonesysTransport.class);

	/**
	 * Device identifier needed to call EMSModule
	 */
	private String	hostIp			= null;
	private Integer	port			= null;

	/**
	 * W-onesys library used to communicate with devices
	 */
	private HwdOp	hwdcontroller	= null;

	public WonesysTransport(ProtocolSessionContext protocolSessionContext) throws net.i2cat.nexus.resources.protocol.ProtocolException {

		this.hostIp = WonesysProtocolSessionContextUtils.getHost(protocolSessionContext);
		this.port = WonesysProtocolSessionContextUtils.getPort(protocolSessionContext);

		if (this.hostIp == null || this.port == -1)
			throw new ProtocolException("Could not extract required data from given ProtocolSessionContext");

		hwdcontroller = new HwdOp();
	}

	@Override
	public Object sendMsg(Object message) throws WonesysTransportException {

		String toSend = (String) message;

		Object response = null;
		try {
			log.debug("Sending message to " + hostIp + ":" + port);
			response = hwdcontroller.sendOp(toSend);
		} catch (IOException ioe) {
			throw new WonesysTransportException(ioe);
		}
		return response;
	}

	public void connect() throws WonesysTransportException {

		try {
			hwdcontroller.connect(hostIp, port);
		} catch (IOException e) {
			throw new WonesysTransportException(e);
		}
	}

	public void disconnect() throws WonesysTransportException {
		try {
			hwdcontroller.disconnect();
		} catch (IOException e) {
			throw new WonesysTransportException(e);
		}
	}

}
