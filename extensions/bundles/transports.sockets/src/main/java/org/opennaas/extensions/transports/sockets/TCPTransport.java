package org.opennaas.extensions.transports.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class handles all the TCP Transport details to be able to send/receive data 
 * from the switch.
 * 
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <p>
 * 2006/04: Change in the transport to be integrated in the engine. <br>
 * Inocybe Technologies - Copyright &copy 2006 <br>
 * <p>
 * 2003/04: Initial file creation. <br>
 * Communications Research Centre and University of Ottawa - Copyright &copy 2003 <br>
 * <p>
 * @author Mathieu Lemay - Research Technologist, Communications Research Centre
 * @author Eduard Grasa - Fundaci� i2cat
 * @version 2.1
 */
public class TCPTransport implements IStreamTransport{
	
	public static final String TCP = "tcp";
	
	/** TCPTransport Logger */
	static private Logger logger = LoggerFactory.getLogger(TCPTransport.class);    
	
	/** TCP Socket */
	private Socket tcpSocket = null;
	
	/** TCP PrintWriter to write to the socket */
	private PrintWriter outPrint;
    
    private static final int READ_TIMEOUT= 300000;
	
	/** Default end of line token */    
	public char[] msgtok = { ';', '>', '<' };
	
	/** The host to connect */
	private String host = null;
	
	/** The port to connect */
	private String port = null;
	
	
	/** Creates a TCPTransport object with default host/port
	 * @param host Target host
	 * @param port Target port
	 */    
	public TCPTransport(String host, String port){
		this.host = host;
		this.port = port;
	}
	
	/** 
	 * Connects to the Remote agent
	 * @throws TransportException Throws TransportException if it couldn't connection to host.. This can
	 * be either an UnknownHostException or a IO Read/Write Exception
	 */
	public void connect() throws TransportException
	{
		logger.info("TCPTransport trying to connect...");
		try{
			tcpSocket=new Socket(host,Integer.parseInt(port));
			tcpSocket.setSoTimeout(READ_TIMEOUT);
			outPrint=new PrintWriter(tcpSocket.getOutputStream(),true);
		}catch (Exception e){
			e.printStackTrace();
			throw new TransportException("Could not connect, unable to open socket " + e.getMessage());
		}
	}
	
	/** Sends a raw array of bytes to the device
	 * @param rawInput Sends String to send
	 */
	public void send(byte[] rawInput) throws TransportException{
		logger.debug("Message to be sent: " + rawInput.toString());
		try{
			outPrint.println(rawInput);
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when sending message: "+ e.getMessage());
		}
	}
	
	/** Sends a raw array of chars to the device
	 * @param rawInput Sends String to send
	 */
	public void send(char[] rawInput) throws TransportException{
		logger.debug("Message to be sent: " + rawInput.toString());
		try{
			outPrint.println(rawInput);
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when sending message: "+ e.getMessage());
		}
	}
	
	
	/**
	 * Disconnects from the Remote Agent  
	 */
	public void disconnect() throws TransportException{
		try{
			tcpSocket.close();
		}catch(IOException e){
			e.printStackTrace();
			throw new TransportException("Problems when disconnecting: "+ e.getMessage());
		}
	}

	public InputStream getInputStream() throws TransportException{
		try{
			return tcpSocket.getInputStream();
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems getting the transport input stream: "+ e.getMessage());
		}
	}

	public OutputStream getOutputStream() throws TransportException{
		try{
			return tcpSocket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems getting the transport output stream: "+ e.getMessage());
		}
	}
}
