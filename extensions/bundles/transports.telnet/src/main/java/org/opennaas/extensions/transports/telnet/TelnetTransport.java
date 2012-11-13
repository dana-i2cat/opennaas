package org.opennaas.extensions.transports.telnet;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a Telnet transport client. It can connect to telnet enabled remote devices and send/receive
 * characters/bytes using the telnet protocol
 * @author edu
 *
 */
public class TelnetTransport implements IStreamTransport {
	
	public static final String TELNET = "telnet";
	
	/** TelnetTransport Logger */
	static private Logger logger = LoggerFactory.getLogger(TelnetTransport.class);
	
	/** The telnet client library **/
	private TelnetClient telnetClient = null;
	
	/** PrintWriter to write to the socket */
	private PrintWriter outPrint;
	
	/** The host to connect */
	private String host = null;
	
	/** The port to connect */
	private String port = null;
	
	/** The read time out **/
	private static final int READ_TIMEOUT= 300000;
	
	public TelnetTransport(String host, String port){
		this.host = host;
		this.port = port;
	}
	
	public TelnetTransport(String host){
		this.host = host;
		this.port = "23";
	}
	
	public void connect() throws TransportException {
		logger.info("Telnet Transport trying to connect...");
		
		//Create a new telnet client
		telnetClient = new TelnetClient();

		//VT100 terminal type will be subnegotiated
        TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
        //WILL SUPPRESS-GA, DO SUPPRESS-GA options
        SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
        //WON'T ECHO, DON'T ECHO
        EchoOptionHandler echoopt = new EchoOptionHandler();
        
        try{
        	//set telnet client options
        	telnetClient.addOptionHandler(ttopt);
        	telnetClient.addOptionHandler(gaopt);
        	telnetClient.addOptionHandler(echoopt);
 
        	//connect
        	telnetClient.connect(host, Integer.parseInt(port));
        	
        	//set the read timeout
        	telnetClient.setSoTimeout(READ_TIMEOUT);
        	
        	//Initialize the print writer
        	outPrint=new PrintWriter(telnetClient.getOutputStream(),true);
        }catch(Exception e){
        	e.printStackTrace();
			throw new TransportException("Could not connect, unable to open telnet session "+ e.getMessage());
        }
	}

	public void disconnect() throws TransportException {
		try{
			telnetClient.disconnect();
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when disconnecting: "+e.getMessage());
		}
	}
	
	public void send(byte[] rawInput) throws TransportException {
		try{
			logger.debug("Message to be sent: " + rawInput);
			outPrint.println(rawInput);
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when sending message: "+e.getMessage());
		}
	}

	public void send(char[] rawInput) throws TransportException {
		try{
			logger.debug("Message to be sent: " + rawInput.toString());
			outPrint.println(rawInput);
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when sending message: "+e.getMessage());
		}
	}

	public InputStream getInputStream() throws TransportException {
		return telnetClient.getInputStream();
	}

	public OutputStream getOutputStream() throws TransportException {
		return telnetClient.getOutputStream();
	}
}

