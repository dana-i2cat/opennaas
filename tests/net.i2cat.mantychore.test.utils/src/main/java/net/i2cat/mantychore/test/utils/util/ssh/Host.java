package cat.i2cat.manticore.test.util.ssh;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cat.i2cat.manticore.test.BGP;
import cat.i2cat.manticore.test.util.login.LoginJob;

public class Host {

	private static Logger log = Logger.getLogger(BGP.class);
	
	private static HashMap<String,SSHTransport> transports = new HashMap<String, SSHTransport>();
	
	private String host;
	
	private int	   portSSH;
	private String userSSH;
	private String passwordSSH;
	
	private String userManticore;
	private String passwordManticore;
	
	private String userDB;
	private String passwordDB;
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @return the port
	 */
	public int getPortSSH() {
		return portSSH;
	}
	
	/**
	 * @param port the port to set
	 */
	public void setPortSSH(int port) {
		this.portSSH = port;
	}
	
	/**
	 * @return the user
	 */
	public String getUserManticore() {
		return userManticore;
	}
	
	/**
	 * @param user the user to set
	 */
	public void setUserManticore(String user) {
		this.userManticore = user;
	}
	
	/**
	 * @return the password
	 */
	public String getPasswordManticore() {
		return passwordManticore;
	}
	
	/**
	 * @param password the password to set
	 */
	public void setPasswordManticore(String password) {
		this.passwordManticore = password;
	}
	
	/**
	 * @return the userDB
	 */
	public String getUserDB() {
		return userDB;
	}
	
	/**
	 * @param userDB the userDB to set
	 */
	public void setUserDB(String userDB) {
		this.userDB = userDB;
	}
	
	/**
	 * @return the passwordDB
	 */
	public String getPasswordDB() {
		return passwordDB;
	}
	
	/**
	 * @param passwordDB the passwordDB to set
	 */
	public void setPasswordDB(String passwordDB) {
		this.passwordDB = passwordDB;
	}
	
	/**
	 * @return the userSSH
	 */
	public String getUserSSH() {
		return userSSH;
	}

	/**
	 * @param userSSH the userSSH to set
	 */
	public void setUserSSH(String userSSH) {
		this.userSSH = userSSH;
	}

	/**
	 * @return the passwordSSH
	 */
	public String getPasswordSSH() {
		return passwordSSH;
	}

	/**
	 * @param passwordSSH the passwordSSH to set
	 */
	public void setPasswordSSH(String passwordSSH) {
		this.passwordSSH = passwordSSH;
	}
	
	/**
	 * Login to the server
	 */
	public boolean login()
	{
		return ( new LoginJob(host,userManticore,passwordManticore) ).doJob();
	}
	
	/**
	 * Loads a SQL script on the remote server's database. Most probably a database dump/restore.
	 * 
	 * @param host
	 * @param schema
	 * @return whether it was successful or not.
	 * @throws Exception
	 */
	public boolean loadDatabase( String schema ) throws Exception
	{
		SSHTransport transport;
		
		log.info("Getting transport...");
		transport = getConnectedSSHTransport();
		
		// create temporary dir
		transport.exec("[ -d /tmp/.manticore-testing/db/ ] || mkdir -p /tmp/.manticore-testing/db/");
		// copy $schema file to the server
		putFileToDir(schema, "/tmp/.manticore-testing/db/");
		// restore db
		transport.exec("psql -U postgres < /tmp/.manticore-testing/" + schema);
		
		return true;
	}
	
	public SSHTransport getConnectedSSHTransport() throws IOException
	{
		if(transports.get(host)==null)
		{
			log.debug("Cache miss");
			SSHTransport transport = new SSHTransport( this );
			transport.setWithShell(true);
			
			log.info("Conecting ssh transport to: " + host + ":" + portSSH);
			transport.connect();
			
			transports.put(host, transport);
		}
		
		return transports.get(host);
	}
	
	public void putFileToDir(String local, String remoteDir) throws IOException
	{
		log.debug("uploading " + local + " to " + host + ":" + remoteDir);
		getConnectedSSHTransport().getConnection().createSCPClient().put(local, remoteDir);
	}
	
	/**
	 * Utility method to remove a transport from the cache when it has been closed.
	 * Called from ConnectionMonitor's handler.
	 * @param transport
	 */
	public static void removeTransport(SSHTransport transport)
	{
		transports.remove(transport);
	}
}
