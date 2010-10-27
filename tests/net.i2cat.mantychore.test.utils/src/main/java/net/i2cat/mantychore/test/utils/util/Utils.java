package cat.i2cat.manticore.test.util;

import org.apache.log4j.Logger;

import cat.i2cat.manticore.test.util.ssh.Host;
import cat.i2cat.manticore.test.util.ssh.SSHTransport;

public class Utils
{	
	private static Logger log = Logger.getLogger(Utils.class);
	

	
	public static boolean loadRemoteTestbed(String host, String local_path, String conf_file)
	{
		Host router = new Host();
		
		router.setHost(host);
		
		router.setPortSSH( 22 );
		router.setUserSSH("xavi");
		router.setPasswordSSH("xavi123");
		
		log.info("Login to router...");
		
		SSHTransport trans;

		log.info("Transporting the config file...");

		try {
			router.putFileToDir(local_path+conf_file, "/tmp");
			trans = router.getConnectedSSHTransport();
			trans.exec("edit ; load override " + "/tmp/"+conf_file + " ; commit");

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
}
