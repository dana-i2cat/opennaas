package net.i2cat.mantychore.engine.transports.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iaasframework.engines.transports.IStreamTransport;
import com.iaasframework.engines.transports.TransportException;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.Session;


public class SSHTransport implements IStreamTransport{
	public static final String SSH = "SSH";
	
	public static final String NETCONF_PROTOCOL = "netconf";
	public static final String XMLAGENT_PROTOCOL ="xmlagent";
	
	/** number of rows with the pty terminal works **/
	private final static int NUMROWS_FOR_TERMINAL = 10000;
	/** buffer for a message **/
	public final static int SIZE_BUFFER = 8129;

	/**
	 * Delimiter for the ssh connection. the method receives a message until to
	 * read the DELIMITER
	 **/
	public  static  String DELIMITER = "]]>]]>";

	/**
	 * Delimiter for EOF. If you don't want any delimiter, you have to specify
	 * this
	 **/
	public  static int TIMEOUT_MILISECS = 5000;
	
	public final static int TIMEOUT = ChannelCondition.TIMEOUT;
	public final static int EOF = ChannelCondition.EOF;
	public final static int STDERR_DATA = ChannelCondition.STDERR_DATA;
	public final static int STDOUT_DATA = ChannelCondition.STDOUT_DATA;
	
	
	/** Username for the ssh connection **/	
	protected String user;

	/** Password for the ssh connection **/
	protected String pass;
	
	/** IP for the ssh connection **/	
	protected String IP;
	
	/** Port for the ssh connection **/
	protected int port;
	
	/** Class to represent a connection **/	
	private Connection conn;

	/** Class to represent a session **/
	private Session sess;

	/** Specificy which subsystem works **/
	protected String subsystem;

	/** Stream to get response messages */
	private OutputStream stdIn;
	
	/** Stream to get response messages */
	private InputStream stdOut;
	/** Stream to get errors **/
	private InputStream stdError;
	
	/** Specify if the shell is activated or not **/
	protected boolean withShell = false;

	/* flags */
	protected boolean isShell = false;
	/** Specify if the session uses a shell **/
	protected boolean isSubsystem = false;
	/** Specify if the session uses a subsystem **/
	
	
	private boolean closed = true;

	/** Specify if the connection is closed **/
			


	
	/** TelnetTransport Logger */
	static private Logger log = LoggerFactory.getLogger(SSHTransport.class);
	
	/**
	 * SSH Constructor
	 * 
	 * @param user
	 *            user parameter for the connection
	 * @param pass
	 *            password parameter for the connection
	 * @param hostname
	 *            host to connect
	 * @param port
	 *            port to connect
	 */
	public SSHTransport(String user, String pass, String IP, int port) {
		this(user, pass, IP, port,null, DELIMITER,TIMEOUT_MILISECS);
	}

	/**
	 * SSH Constructor
	 * 
	 * @param user
	 *            user parameter for the connection
	 * @param pass
	 *            password parameter for the connection
	 * @param hostname
	 *            host to connect
	 * @param port
	 *            port to connect
	 * @param subsystem
	 *            specify the subssystem for the ssh connection
	 */
	public SSHTransport(String user, String pass, String IP, int port,
			String subsystem) {
		this(user, pass, IP, port, subsystem, DELIMITER,TIMEOUT_MILISECS);
	}

	/**
	 * Constructor SSHTransport with the different params
	 * 
	 * @param user
	 *            user parameter for the connection
	 * @param pass
	 *            password parameter for the connection
	 * @param hostname
	 *            host to connect
	 * @param port
	 *            port to connect
	 * @param subsystem
	 *            specify the subsystem for the ssh connection
	 * @param delimiter
	 *            specify a delimiter in the messages, if it is necessary
	 * @param timeout
	 *            specify a timeout for the connection.
	 */
	public SSHTransport(String user, String pass, String IP, int port,
			String subsystem, String delimiter, int timeout) {
		this.user = user;
		this.pass = pass;
		this.IP = IP;
		this.port = port;
		this.withShell = false;
		this.subsystem = subsystem;
		this.isSubsystem = (subsystem != null);
		this.isShell = withShell;
		TIMEOUT_MILISECS = timeout;
		DELIMITER = delimiter;
	}
	
	
	/**
	 * Create and authenticate a connection. The workflow is: Connect,
	 * Authenticate and create new session
	 */
	public void connect() throws TransportException {

		try {
			
			System.out.println("user: " + user);
			System.out.println("pass: " + pass);
			System.out.println("hostname: " + IP);
			
			//Restore the configuration of SSH connection
			isSubsystem = (subsystem != null);
			isShell = withShell;
			
			System.out.println("resetting configuration isShell, isSubsystem");
			System.out.println("Shell: " + withShell);
			System.out.println("Subsystem: " + withShell);

			conn = new Connection(IP, port);
			boolean isAuthenticated = false;
			conn.connect();
			System.out.println("starting authentication...");

			isAuthenticated = conn.authenticateWithPassword(user, pass);

			if (!isAuthenticated)
				throw new TransportException("it couldn't open the connection (Authentication Error)");

			/* Opening session */
			System.out.println("Opening session...");
			sess = conn.openSession();
			System.out.println("Session opened...");	
			stdOut  = sess.getStdout();
			stdError = sess.getStderr();
			stdIn = sess.getStdin();
			
			
			/* Prepare the connection managing */
			closed = false;
			DummyConnectionMonitor connectionMonitor = new DummyConnectionMonitor(this);
			conn.addConnectionMonitor(connectionMonitor);

		} catch (IOException e) {
			// e.printStackTrace();
			throw new TransportException("it couldn't open the connection (IOException: "+e.getMessage()+")");
		}
	}
	
	/**
	 * Close the connection
	 */
	public void disconnect() throws TransportException {

		System.out.println("Closing session...");
		System.out.println("ExitCode: " + sess.getExitStatus());
		sess.close();
		System.out.println("Session Closed");
		this.conn.close();
		closed = true;

	}

	
	
	/**
	 * Send a new operation with SSH. This message is parsed with StringBuilder,
	 * because it is more efficient
	 * 
	 * @param operation
	 *            Operation that it will be sent
	 * @throws TimeoutException
	 *             This exception is thrown when it is impossible get send an
	 *             operation
	 */
	private void send(StringBuilder operation) throws TransportException {

		try {

			/* activate flags */
			System.out.println("Session opened...");

			/* Create a shell */
			if (isShell) {
				System.out.println("Starting a shell... ");
				// I am using a large number of rows for get all the message
				sess.requestPTY("vt100", 80, NUMROWS_FOR_TERMINAL, 640, 480,
						null);
				sess.startShell();
				isShell = false;

			}

			/* Create a subsystem */
			if (this.isSubsystem) {
				System.out.println("Activating " + subsystem + "... ");
				
				if (subsystem.equals(NETCONF_PROTOCOL)) {
					sess.startSubSystem(NETCONF_PROTOCOL);
					
				} else if (subsystem.equals(XMLAGENT_PROTOCOL)) {
					sess.startSubSystem(XMLAGENT_PROTOCOL);
				}
				isSubsystem = false;
			}
			
			/* Finish getting connection */
			OutputStream out = sess.getStdin();
			PrintWriter writer = new PrintWriter(out);
			writer.println(operation.toString());
			writer.flush();
			System.out.println("Operation sent");

		} catch (IOException e) {
			//e.printStackTrace();
			throw new TransportException("it couldn't receive a response");
		}
	}
	
	

	/**
	 * Define if it is necessary work with a shell
	 * 
	 * @param withShell
	 */
	public void setWithShell(boolean withShell) {
		this.withShell = withShell;
		this.isShell = withShell;

	}
	

	/**
	 * Activate a subsystem for ssh (for example: sftp, netconf, scp)
	 * 
	 * @param subsystem
	 */
	public void activateSubsystem(String subsystem) {
		/* Enable mode subsystem */
		this.subsystem = subsystem;
		this.isSubsystem = true;
	}


	public Session getSess() {
		return sess;
	}

	/* Get/Sets for configuration */
	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getIP() {
		return IP;
	}

	public int getPort() {
		return port;
	}
	
	/**
	 * Mark if the connection is closed
	 * 
	 * @param closed
	 *            new status of the connection
	 */
	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean isClosed() {
		return closed;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public boolean isWithShell() {
		return withShell;
	}
	
	public static void setDELIMITER(String delimiter) {
		DELIMITER = delimiter;
	}
	
	/* little connection monitor */

	/**
	 * This class implements the Monitor pattern and it updates the status of
	 * the sshtransport
	 */
	class DummyConnectionMonitor implements ConnectionMonitor {
		SSHTransport sshtransport;

		public DummyConnectionMonitor(SSHTransport sshtransport) {
			this.sshtransport = sshtransport;
		}

		public void connectionLost(Throwable arg0) {
			sshtransport.setClosed(true);

		}
	}

	/**
	 * Get response message stream
	 */
	public InputStream getInputStream() throws TransportException {
		return stdOut;
	}
	
	/**
	 * Get error response message stream
	 * @return
	 * @throws TransportException
	 */
	public InputStream getInputStreamError() throws TransportException {
		return stdError;
	}
	
	@Override
	public OutputStream getOutputStream() throws TransportException {
		return stdIn;
	}

	@Override
	public void send(byte[] rawInput) throws TransportException {
		send(new StringBuilder(new String(rawInput)));
		
	}

	@Override
	public void send(char[] rawInput) throws TransportException {
		send(new StringBuilder(new String(rawInput)));
		
	}
	

}
