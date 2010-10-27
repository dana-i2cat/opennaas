package cat.i2cat.manticore.test.util.login;

/***************************************************************************************************
 * Copyright (c) 2005,2007 i2CAT Foundation and Communications Research Centre Canada. All rights
 * reserved. This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Eduard Grasa (i2CAT), and Scott Campbell (CRC) - initial API and implementation
 **************************************************************************************************/
/**
 * 
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.log4j.Logger;
import org.globus.myproxy.MyProxy;
import org.globus.myproxy.MyProxyException;
import org.ietf.jgss.GSSCredential;

import cat.i2cat.manticore.test.util.ssh.SSHTransport;


/**
 * non-modal "job" to do the login.
 * 
 * @author Xavi Barrera
 * 
 */
public class LoginJob
{
	private static Logger log = Logger.getLogger(LoginJob.class);
	
	/** Constant values **/
	public static final int MYPROXY_PORT = 7512;
	public static final String LOCAL_CA_LOCATION = ".globus" + File.separator + "certificates";
	public static final String CA_SUFFIX = "-CAcert.0";
	private String serverHost = null;
	private String loginId = null;
	private String password = null;
	private Session session = null;
	private X509Certificate cert;
	private File fullCertPath;
	private Proxy proxy;
	private EndpointReferenceType epr = null;


	public LoginJob(String host, String uname, String pw)
	{
		this.serverHost = host;
		this.loginId = uname;
		this.password = pw;
	}

	//public User doJob()
	public boolean doJob()
	{
		session = Session.getInstance();
		proxy = Proxy.NO_PROXY;
		
		log.debug("Proxy Type: " + proxy.type());
		if (proxy.address() != null)
			log.debug(" Address: " + proxy.address().toString());
		
		try {
			// Check the CA Certificate
			// Make sure the CA Cert for this host has been downloaded locally
			if (!checkCACert(serverHost, proxy)) {
				// Delete the certificate if it was rejected
				fullCertPath.delete();
			}

			log.debug("Trying to log into server " + serverHost);
			MyProxy myProxy = new org.globus.myproxy.MyProxy(serverHost,
					MYPROXY_PORT);
			try {
				myProxy.setAuthorization(new org.globus.gsi.gssapi.auth.NoAuthorization());
				GSSCredential newCred = myProxy.get(null, loginId, password, 26000);
				session.setCredentials(newCred);

			}
			catch (MyProxyException e) {
				e.printStackTrace();
				throw new InvocationTargetException(e, e.getMessage());
			}

			log.info("Logged in");

			return true;
		}catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return false;
		//return null;
	}

	private boolean checkCACert(String host, Proxy proxy) throws InvocationTargetException
	{
		// Setup the path locations for the ca certificate
		String userHome = System.getProperty("user.home");

		File localCertPath = new File(userHome + File.separator + LOCAL_CA_LOCATION);
		String localCertName = host + CA_SUFFIX;
		String remoteCertPath = "http://" + host + "/" + host + CA_SUFFIX;
		fullCertPath = new File(localCertPath, localCertName);

		log.debug("User Home Path is: " + userHome);
		log.debug("Cert path is " + localCertPath.getAbsolutePath());
		log.debug("Cert filename is " + localCertName);
		log.debug("Remote Cert path is " + remoteCertPath);

		// First check to see if the CA Certificate has been downloaded
		if (!fullCertPath.exists()) {
			log.debug("Cert does not exist, must download it");

			// create the directory
			localCertPath.mkdirs();
			try {
				FileDownload.download(remoteCertPath, fullCertPath, proxy);
			}
			catch (IOException e) {
				log.debug("Error getting CA from " + remoteCertPath);
				throw new InvocationTargetException(e, "Error getting CA from " + remoteCertPath);
			}
			try {
				// Read the certificate and display a dialog to accept it.
				getLocalCertificate(fullCertPath);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new InvocationTargetException(e, "There was an error reading the CA Certificate from the local system");
			}
		}
		else {
			log.debug("Cert already exists");
		}

		return true;
	}

	private X509Certificate getLocalCertificate(File path) throws IOException, CertificateException
	{
		InputStream inStream = new FileInputStream(path);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		cert = (X509Certificate) cf.generateCertificate(inStream);
		inStream.close();

		return cert;
	}
}
