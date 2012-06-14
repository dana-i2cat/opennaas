package org.opennaas.extensions.transports.sockets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.TransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class handles all the SSL Transport details to be able to send/receive
 * data from the switch.
 * <p>
 * Known Limitations: N/A
 * <p>
 * <b>History</b> <br>
 * <p>
 * 2006/04: Change in the transport to be integrated in the engine. <br>
 * Inocybe Technologies - Copyright &copy 2006 <br>
 * <p>
 * 2003/12: Initial file creation. <br>
 * Communications Research Centre and University of Ottawa - Copyright &copy 2003 <br>
 * <p>
 * @author Mathieu Lemay - Research Technologist, Communications Research Centre
 * @version 2.2
 */
public class SSLTransport implements IStreamTransport{
    
	public static final String SSL = "ssl";
	
	static private Logger logger = LoggerFactory.getLogger(SSLTransport.class);

    /** HostName for the transport */
    private String host;

    /** Port Number for the transport */
    private int port;

    /** KeyFile name */
    private String keyfile;

    /** password */
    private String pwd;

    /** SSL Socket */
    private SSLSocket sslSocket = null;

    private KeyStore serverKeyStore = null;

    private TrustManagerFactory tmf = null;

    private KeyManagerFactory kmf = null;

    private SecureRandom secureRandom = null;
  
    /** SSL PrintWriter to write to the socket */
    private PrintWriter outPrint;
    
    private boolean initialized = false;

    private static final int READ_TIMEOUT = 300000;
    public char[] msgtok = { '\n' };

    public SSLTransport(String host, String port, String keystore, String keystorePassword){
    	this.host = host;
    	this.port = Integer.parseInt(port);
    	this.keyfile = keystore;
    	this.pwd = keystorePassword;
    }
   
    /**
     * Connects to the Device
     * 
     * @throws IOException
     *             Throws IOException if it couldn't connection to host.. This
     *             can be either an UnknownHostException or a IO Read/Write
     *             Exception
     */
    public void connect() throws TransportException {
    	if (!initialized){
    		initialize();
    		initialized = true;
    	}
    	
        // connect to server
        // Get a SocketFactory object
        try{
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // TLS = Transport Layer Security - New name for SSL
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            sslSocket = (SSLSocket) factory.createSocket(host, port);
            sslSocket.setSoTimeout(READ_TIMEOUT);
            SSLSession session = sslSocket.getSession();
            if (session.getCipherSuite().equals("SSL_NULL_WITH_NULL_NULL")){
                throw new TransportException("Could not connect to SSL Server. Invalid cipher suite: "+session.getCipherSuite());
            }
            
            X509Certificate cert;
            logger.info("Connecting to " + host);
            outPrint = new PrintWriter(sslSocket.getOutputStream(), true);
            try {
                cert = (X509Certificate) session.getPeerCertificates()[0];
            }catch (SSLPeerUnverifiedException e) { 
            	// If no or invalid certificate
                logger.info(session.getPeerHost() + " No Valid Certificate Found");
                throw new TransportException("Could not connect to SSL Server. "
                		+session.getPeerHost() + " No Valid Certificate Found");
            }
            
            // Display details about the certificate
            logger.info(session.getPeerHost() + " has this certifcate:");
            logger.info("Subject: [" + cert.getSubjectDN().getName() + "]");
            logger.info("The signature is:");
            logger.info("Name :[" + cert.getIssuerDN().getName() + "]");
        }catch (Exception e){
            e.printStackTrace();
            throw new TransportException("Could not connect to SSL Server. "+e.getMessage());
        }
    }
    
    private void initialize() throws TransportException{
    	logger.info("Initializing SSL transport remote host: " + host + " remote port " + port);
    	//Initialize the keystore
    	try
        {
            getKeys(keyfile, pwd);
            
            tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(serverKeyStore);
            
            //Sets the key Manager to use X.509 protocol
            kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(serverKeyStore, pwd.toCharArray());
            
            secureRandom = new SecureRandom();
            secureRandom.nextInt();
        } catch (Exception e){
            e.printStackTrace();
            throw new TransportException(e.getMessage());
        }
    }
    
    
    /**
     * Load the keystore in memory
     * @param serverkeyfile
     * @param password
     * @throws GeneralSecurityException
     * @throws TransportException
     */
    private void getKeys(String serverkeyfile, String password) throws TransportException{
        logger.info("Loading keys");
    	FileInputStream fis;
    	
        try{
        	serverKeyStore = KeyStore.getInstance("JKS");
        	//Read the keystore file
        	fis = new FileInputStream(new File(serverkeyfile));
        } catch (FileNotFoundException e){
        	throw new TransportException("Unable to open keyStore at: " + serverkeyfile);
        } catch (KeyStoreException e) {
        	throw new TransportException(e.getMessage());
		}
        
        //Load the keystore in memory
        try{
            serverKeyStore.load(fis, password.toCharArray());
            logger.debug("There are "+serverKeyStore.size()+" entries in the keystore");
        }catch (NoSuchAlgorithmException e){
            throw new TransportException("Problems occurred while loading the SSL certificate from the keyStore. "+e.getMessage());
        }catch (CertificateException e){
            throw new TransportException("Problems occurred while loading the SSL certificate from the keyStore. "+e.getMessage());
        }catch (IOException e){
            throw new TransportException("Invalid keyStore password. "+e.getMessage());
        } catch (KeyStoreException e) {
        	throw new TransportException(e.getMessage());
		}
    }

    /** Disconnects from the switch */
    public void disconnect() throws TransportException{
        try {
            logger.debug("Disconnecting SSL Transport");
            sslSocket.close();
        }catch (Exception e) {
            e.printStackTrace();
            throw new TransportException("Problems disconnecting from SSL Server. "+e.getMessage());
        }
    }


	/** 
	 * Sends a raw array of bytes to the device
	 * @param rawInput Sends String to send
	 */
	public void send(byte[] rawInput) throws TransportException{
		logger.debug("Message to be sent: " + rawInput);
		try{
			outPrint.println(rawInput);
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems when disconnecting: "+ e.getMessage());
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
			throw new TransportException("Problems when disconnecting: "+ e.getMessage());
		}
	}
	
	public InputStream getInputStream() throws TransportException{
		try{
			return sslSocket.getInputStream();
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems getting the transport input stream: "+ e.getMessage());
		}
	}

	public OutputStream getOutputStream() throws TransportException{
		try{
			return sslSocket.getOutputStream();
		}catch(Exception e){
			e.printStackTrace();
			throw new TransportException("Problems getting the transport output stream: "+ e.getMessage());
		}
	}
}