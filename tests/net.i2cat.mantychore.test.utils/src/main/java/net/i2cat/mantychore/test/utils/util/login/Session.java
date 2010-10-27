package cat.i2cat.manticore.test.util.login;

import org.ietf.jgss.GSSCredential;



/**
 * This class stores the data of the current RMC Session: User data, 
 * the user credentials and the EPR to the UserManagement Resource
 * @author edu
 *
 */
public class Session {
	private GSSCredential credentials = null;
	private String serverLocation = null;
	private static Session instance = null;
	
	private Session(){
	}
	
	public static Session getInstance(){
		if (instance == null)
			instance = new Session();
		
		return instance;
	}

	public GSSCredential getCredentials() {
		return credentials;
	}

	public void setCredentials(GSSCredential credentials) {
		this.credentials = credentials;
	}

	public String getServerLocation() {
		return serverLocation;
	}

	public void setServerLocation(String serverLocation) {
		this.serverLocation = "https://" + serverLocation + ":8443";
	}

	public static void setInstance(Session instance) {
		Session.instance = instance;
	}
}
