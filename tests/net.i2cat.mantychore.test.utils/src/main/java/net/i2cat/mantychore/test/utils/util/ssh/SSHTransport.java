package cat.i2cat.manticore.test.util.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionMonitor;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

import cat.i2cat.manticore.common.constants.EngineConstants;
import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.util.Utils;

/**
 * SSH transport implementation. The library Ganymed-ssh2 that implements the
 * SSH transport and this class uses this library and provides the main
 * functions and features for an SSH implementation inside the Manticore
 * Project. Some features ,that it provides, are: different subsystem,
 * implementation of shells, different ports for the SSH connection The
 * information of the parameters is extracted of the ssh RFC. Reference for SSH
 * http://www.ietf.org/rfc/rfc4254.txt
 * 
 * @author Carlos Baez
 * @version 1.0
 * 
 */
public class SSHTransport implements ConnectionMonitor
{
	private static Logger log = Logger.getLogger(SSHTransport.class);

	/** number of rows with the pty terminal works **/
	private final static int NUMROWS_FOR_TERMINAL = 10000;
	/** buffer for a message **/
	private final static int SIZE_BUFFER = 8129;

	/**
	 * Delimiter for the ssh connection. the method receives a message until to
	 * read the DELIMITER
	 **/
	private static String DELIMITER = "]]>]]>";

	/**
	 * Delimiter for EOF. If you don't want any delimiter, you have to specify
	 * this
	 **/
	private static int TIMEOUT_MILISECS = 5000;

	/* Target host */
	private static Host host;

	/** Class to represent a connection **/
	private Connection conn;

	/** Class to represent a session **/
	private Session sess;

	/** Specificy which subsystem works **/
	protected String subsystem;

	/** Specify if the shell is activated or not **/
	protected boolean withShell = false;
	
	/* flags */
	/** Specify if the session uses a shell **/
	protected boolean isShell = false;
	/** Specify if the session uses a subsystem **/
	protected boolean isSubsystem = false;

	/** Specify if the connection is closed **/
	private boolean closed = true;
	
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
	public SSHTransport(Host host) {
		this(host, null, EngineConstants.DELIMITER, EngineConstants.TIMEOUT_MILISECS);
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
	public SSHTransport(Host host, String subsystem) {
		this(host, subsystem, EngineConstants.DELIMITER, EngineConstants.TIMEOUT_MILISECS);
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
	 *            specify the subssystem for the ssh connection
	 * @param delimiter
	 *            specify a delimiter in the messages, if it is necessary
	 * @param timeout
	 *            specify a timeout for the connection.
	 */
	public SSHTransport(Host host, String subsystem, String delimiter, int timeout) {
		this.host = host;
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
	public void connect() throws IOException {

		try {

			log.debug("user: " + host.getUserSSH() );
			log.debug("pass: " + host.getPasswordSSH() );
			log.debug("hostname: " + host.getHost() );
			
			//Restore the configuration of SSH connection
			isSubsystem = (subsystem != null);
			isShell = withShell;
			
			log.debug("resetting configuration isShell, isSubsystem");
			log.debug("Shell: " + withShell);
			log.debug("Subsystem: " + withShell);

			conn = new Connection(host.getHost(), host.getPortSSH());
			boolean isAuthenticated = false;
			conn.connect();
			log.debug("starting authentication...");

			isAuthenticated = conn.authenticateWithPassword(host.getUserSSH(), host.getPasswordSSH());

			if (!isAuthenticated)
				throw new IOException("it couldn't open the connection (Authentication Error)");

			/* Opening session */
			log.debug("Opening session...");
			sess = conn.openSession();
			log.debug("Session opened...");

			/* Prepare the connection managing */
			closed = false;
			conn.addConnectionMonitor(this);

		} catch (IOException e) {
			// e.printStackTrace();
			throw new IOException("it couldn't open the connection (IOException: "+e.getMessage()+")");
		}
	}

	/**
	 * Close the connection
	 */
	public void disconnect() throws Exception
	{
		log.info("Closing session...");
		log.info("ExitCode: " + sess.getExitStatus());
		sess.close();
		log.info("Session Closed");
		this.conn.close();
		closed = true;
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
	public String send(String operation) throws IOException {

		/* activate flags */
		log.debug("Session opened...");

		/* Create a shell */
		if (isShell) {
			log.debug("Starting a shell... ");
			// I am using a large number of rows for get all the message
			sess.requestPTY("vt100", 80, NUMROWS_FOR_TERMINAL, 640, 480, null);
			sess.startShell();
			isShell = false;
		}

		/* Create a subsystem */
		if (this.isSubsystem) {
			log.debug("Activating " + subsystem + "... ");
			
			//TODO more generic
			if (subsystem.equals(EngineConstants.NETCONF_PROTOCOL)) {
				sess.startSubSystem("netconf");
				
			} else if (subsystem.equals(EngineConstants.XMLAGENT_PROTOCOL)) {
				sess.startSubSystem("xmlagent");
			}
			isSubsystem = false;
		}
		
		/* Finish getting connection */
		OutputStream out = sess.getStdin();
		PrintWriter writer = new PrintWriter(out);
		writer.println( operation );
		writer.flush();
		log.debug("Operation sent");
		
		InputStream in = sess.getStdout();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

		StringBuffer sb = new StringBuffer();
		String line;
		while ( (line = reader.readLine()) != null ) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	/**
	 * Receive function. This function implements a loop that it is saving the
	 * message in a byte array. After, it searches the delimiter parameter in
	 * the byte array. This delimiter specifies the end of the message, and the
	 * function returns the message received
	 */
	public Object receive() throws Exception {
		StringBuilder result = new StringBuilder();

		if (sess == null)
			return result;

		InputStream stdout = sess.getStdout();
		InputStream stderr = sess.getStderr();

		byte[] buffer = new byte[SIZE_BUFFER];

		try {
			boolean continueBucle = true;
			int timeouts = 0;
			while (continueBucle) {
				if ((stdout.available() == 0) && (stderr.available() == 0)) {

					int conditions = sess.waitForCondition(
							ChannelCondition.STDOUT_DATA
									| ChannelCondition.STDERR_DATA
									| ChannelCondition.EOF, TIMEOUT_MILISECS);

					/* Wait no longer than 10 seconds (= 10000 milliseconds) */

					if ((conditions & ChannelCondition.TIMEOUT) != 0) {
						timeouts++;
						/* A timeout occured. */
						if (timeouts == 10) {
							throw new Exception("Timeout while waiting for data from peer.");
						}

					}

					/*
					 * Here we do not nebreak;ed to check separately for CLOSED,
					 * since CLOSED implies EOF
					 */

					if ((conditions & ChannelCondition.EOF) != 0) {
						/* The remote side won't send us further data... */

						if ((conditions & (ChannelCondition.STDOUT_DATA | ChannelCondition.STDERR_DATA)) == 0) {
							/* ... and we have consumed all data in the local
							 * arrival window.
							 */
							break;
						}
					}

				}

				while (stdout.available() > 0) {
					int len = stdout.read(buffer);
					if (len > 0) {
						// this check is somewhat paranoid
						result.append(new String(buffer, 0, len));
					}
					// It is final
	
					if (result.indexOf(DELIMITER) != -1)
						continueBucle = false;
				}

				while (stderr.available() > 0) {
					int len = stderr.read(buffer);
					if (len > 0) // this check is somewhat paranoid
						log.error(new String(buffer, 0, len));
				}
			}
		} catch (IOException e) {
			// e.printStackTrace();
			throw new Exception("The message couldn't be received");
		}

		String[] arrayResult = result.toString().split(DELIMITER);
		List<String> listStrings = Arrays.asList(arrayResult);


		return listStrings;
	}
	
	public int exec(String command) throws Exception
	{	
		Session session;
		int condition,
			ret;
		InputStream out;
		InputStream err;
		boolean keepWaiting = true;
		
		session = conn.openSession();
		out = session.getStdout();
		err = session.getStderr();
		
		log.debug("command: " +  command);
		session.execCommand(command);
		
		log.debug("wait for command to finnish");
		while( keepWaiting )			  
		{
			condition = session.waitForCondition( ChannelCondition.EXIT_STATUS
					    | ChannelCondition.STDOUT_DATA
					    | ChannelCondition.STDERR_DATA, 0);

//			log.debug("cond      : " + condition);
//			log.debug("cond   out: " + ChannelCondition.STDOUT_DATA);
//			log.debug("cond   err: " + ChannelCondition.STDERR_DATA);
//			log.debug("cond   ret: " + ChannelCondition.EXIT_STATUS);
//			log.debug("cond & clo: " + ((condition & ChannelCondition.CLOSED)!=0) );
//			log.debug("cond & eof: " + ((condition & ChannelCondition.EOF)!=0) );
//			log.debug("cond & sig: " + ((condition & ChannelCondition.EXIT_SIGNAL)!=0) );
//			log.debug("cond & ret: " + ((condition & ChannelCondition.EXIT_STATUS)!=0) );
//			log.debug("cond & err: " + ((condition & ChannelCondition.STDERR_DATA)!=0) );
//			log.debug("cond & out: " + ((condition & ChannelCondition.STDOUT_DATA)!=0) );
//			log.debug("cond & tmo: " + ((condition & ChannelCondition.TIMEOUT)!=0) );
			
			if( (condition & ChannelCondition.STDOUT_DATA) != 0 )
			{
				BufferedReader stdOut = new BufferedReader(new InputStreamReader(out));
				String s=null;
				String result=null;

				while((s=stdOut.readLine()) !=null){
		               result = s+"\n";
		               System.out.println(result);
		           }
			}
			if( (condition & ChannelCondition.STDERR_DATA) != 0 )
			{
				//log.debug("skipping stderr data...");
				//err.skip( err.available() );
				
				BufferedReader stderr = new BufferedReader(new InputStreamReader(err));
				String s=null;
				String result=null;

				while((s=stderr.readLine()) !=null){
		               result = s+"\n";
		               System.out.println(result);
		           }
			}
			if( (condition & ChannelCondition.EXIT_STATUS) != 0 )
			{
				ret = session.getExitStatus();
				session.close();
				
				log.debug("returning with status: " + ret);
				return ret;
			}
		}
		
		throw new Exception("Unable to execute command");
	}
	
	public void putFileToDir(String local, String remoteDir) throws IOException
	{
		conn.createSCPClient().put(local, remoteDir);
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

	public boolean isClosed() {
		return closed;
	}

	public boolean isWithShell() {
		return withShell;
	}
	
	public static void setDELIMITER(String delimiter) {
		DELIMITER = delimiter;
	}
	
	/* From ConnectionMonitor */
	public void connectionLost(Throwable arg0) {
		setClosed(true);
		// remove transport from list of connected transports
		Host.removeTransport(this);

	}

	public Connection getConnection() {
		return conn;
	}
}
