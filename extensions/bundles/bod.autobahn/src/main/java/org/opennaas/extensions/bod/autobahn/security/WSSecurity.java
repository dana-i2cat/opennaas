package org.opennaas.extensions.bod.autobahn.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.log4j.Logger;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Enables safe ws communication and also edugain validation.
 * 
 * @author Akis Kalligeros
 */
public class WSSecurity {

	private final static Logger	log						= Logger.getLogger(WSSecurity.class);
	private URL					WSS4J_PROPS;
	private String				activatedStr, timestampStr, encryptStr, edugainAct, securityUser;
	public final String			PROPERTY_ACTIVATED		= "net.geant.autobahn.security.activated";
	public final String			PROPERTY_ENCRYPT		= "net.geant.autobahn.edugain.encrypt";
	public final String			PROPERTY_TIMESTAMP		= "net.geant.autobahn.edugain.timestamp";
	public final String			PROPERTY_EDUGAIN		= "net.geant.autobahn.edugain.activated";
	public final String			PROPERTY_USER			= "org.apache.ws.security.crypto.merlin.keystore.alias";
	public final String			WSS_X509_TOKENPROFILE	= "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
	public static final int		DEFAULT_TIMEOUT			= 1200 * 1000;
	public URL					edugain, securityUrl;
	public String				security;
	public XPathExpression		xpath;

	/**
	 * @throws XPathException
	 */
	public WSSecurity() throws XPathException {
		xpath = compileXpathExpression();
	}

	/**
	 * @param commonPath
	 * @throws XPathException
	 * @throws IOException 
	 */
	public WSSecurity(String commonPath) throws XPathException, IOException {

		xpath = compileXpathExpression();
		ClassLoader securityLoader = getClass().getClassLoader();

		this.edugain = securityLoader.getResource(commonPath + "/edugain/edugain.properties");
		this.securityUrl = securityLoader.getResource(commonPath + "/security.properties");
		this.WSS4J_PROPS = securityLoader.getResource(commonPath + "/security.properties");
		
		readProperties();
	}

	/**
	 * @return
	 */
	public Properties convertFileToProps() {

		Properties props = new Properties();
		String key = "org.apache.ws.security.crypto.merlin.file";

		try {
			props.load(WSS4J_PROPS.openStream());
		} catch (Exception e) {
			log.debug("Error in converting properties file to properties object. "
					+ e.getMessage());
		}

		if (props.getProperty(key) != null) {

			String s = props.getProperty(key);
			props.setProperty(key, s);
		}

		return props;
	}

	/**
	 * @return the activatedStr
	 */
	public String getActivatedStr() {
		return activatedStr;
	}

	/**
	 * @param activatedStr
	 *            the activatedStr to set
	 */
	public void setActivatedStr(String activatedStr) {
		this.activatedStr = activatedStr;
	}

	/**
	 * @return the timestampStr
	 */
	public String getTimestampStr() {
		return timestampStr;
	}

	/**
	 * @param timestampStr
	 *            the timestampStr to set
	 */
	public void setTimestampStr(String timestampStr) {
		this.timestampStr = timestampStr;
	}

	/**
	 * @return the encryptStr
	 */
	public String getEncryptStr() {
		return encryptStr;
	}

	/**
	 * @param encryptStr
	 *            the encryptStr to set
	 */
	public void setEncryptStr(String encryptStr) {
		this.encryptStr = encryptStr;
	}

	/**
	 * @return the edugainAct
	 */
	public String getEdugainAct() {
		return edugainAct;
	}

	/**
	 * @param edugainAct
	 *            the edugainAct to set
	 */
	public void setEdugainAct(String edugainAct) {
		this.edugainAct = edugainAct;
	}

	/**
	 * @return the securityUser
	 */
	public String getSecurityUser() {
		return securityUser;
	}

	/**
	 * @param securityUser
	 *            the securityUser to set
	 */
	public void setSecurityUser(String securityUser) {
		this.securityUser = securityUser;
	}

	public static void setClientTimeout(Object clientInterface) {
		setClientTimeout(clientInterface, DEFAULT_TIMEOUT);
	}

	public static void setClientTimeout(Object clientInterface, int millis) {
		Client client = ClientProxy.getClient(clientInterface);
		HTTPConduit http = (HTTPConduit) client.getConduit();
		HTTPClientPolicy policy = new HTTPClientPolicy();
		policy.setConnectionTimeout(millis);
		policy.setReceiveTimeout(millis);
		policy.setAllowChunking(false);
		http.setClient(policy);
	}

	/**
	 * Reads the edugain property file and creates a String that represents the security methods that will be used (Signature, Timestamp, Encryption)
	 * 
	 * @return
	 * @throws IOException
	 */
	public String readProperties() throws IOException {

		Properties securityProps = new Properties();

		try {
			securityProps.load(securityUrl.openStream());

			activatedStr = securityProps.getProperty(PROPERTY_ACTIVATED);
			timestampStr = securityProps.getProperty(PROPERTY_TIMESTAMP);
			encryptStr = securityProps.getProperty(PROPERTY_ENCRYPT);
			edugainAct = securityProps.getProperty(PROPERTY_EDUGAIN);
			securityUser = securityProps.getProperty(PROPERTY_USER);

		} catch (IOException e) {
			log.error("Couldn't load client properties: " + e.getMessage());
		}

		if (activatedStr != null && "true".equalsIgnoreCase(activatedStr)) {

			security = "Signature";

			if (timestampStr != null && "true".equalsIgnoreCase(timestampStr)) {

				security += " Timestamp";
			}

			if (encryptStr != null && "true".equalsIgnoreCase(encryptStr)) {

				security += " Encrypt";
			}

		} else
			security = "NoSecurity";

		return security;

	}

	/**
	 * Returns the string variable that shows if Edugain validation should be enabled or not
	 * 
	 * @return String
	 */
	public String getEdugainActive() {
		return this.edugainAct;
	}

	/**
	 * Retrieves the CXF endpoint from the client's interface object
	 * 
	 * @param clientInterface
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 * @throws Throwable
	 */
	public void configureEndpoint(Object clientInterface)
			throws FileNotFoundException, IOException, Exception, Throwable {

		configureEndpoint(clientInterface, true);
	}

	/**
	 * Retrieves the CXF endpoint from the client's interface object
	 * 
	 * @param clientInterface
	 * @param security
	 *            - if false, no security will be activated. If true, it will be activated according to properties
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws Exception
	 * @throws Throwable
	 */
	public void configureEndpoint(Object clientInterface, boolean security)
			throws FileNotFoundException, IOException, Exception, Throwable {

		Client client = ClientProxy.getClient(clientInterface);
		Endpoint endpoint = client.getEndpoint();
		if (security) {
			configureSecurity(endpoint);
		}
	}

	/**
	 * Configures the interceptors for enabling security. Also creates and adds a custom edugain validator
	 * 
	 * @param cxfEndpoint
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws XPathException
	 * @throws Exception
	 */
	public void configureSecurity(Endpoint cxfEndpoint)
			throws FileNotFoundException, IOException, XPathException, Exception {

		Map<String, Object> in = new HashMap<String, Object>();
		Map<String, Object> out = new HashMap<String, Object>();

		SecurityPasswordCallback securityPassword = new SecurityPasswordCallback(securityUrl);

		// Encrypt the SOAP body
		String bodyPart = "{Content}{}Body";

		out.put("properties", convertFileToProps());
		out.put(WSHandlerConstants.ENC_PROP_REF_ID, "properties");
		out.put(WSHandlerConstants.SIG_PROP_REF_ID, "properties");
		out.put(WSHandlerConstants.ACTION, readProperties());
		out.put(WSHandlerConstants.ENCRYPTION_USER, securityUser);
		out.put(WSHandlerConstants.USER, securityUser);
		out.put(WSHandlerConstants.PW_CALLBACK_REF, securityPassword);
		out.put(WSHandlerConstants.ENC_SYM_ALGO, WSConstants.TRIPLE_DES);
		out.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference");
		out.put(WSHandlerConstants.ENCRYPTION_PARTS, bodyPart);

		in.put("properties", convertFileToProps());
		in.put(WSHandlerConstants.ACTION, readProperties());
		in.put(WSHandlerConstants.PW_CALLBACK_REF, securityPassword);
		in.put(WSHandlerConstants.DEC_PROP_REF_ID, "properties");
		in.put(WSHandlerConstants.SIG_PROP_REF_ID, "properties");

		WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(out);
		cxfEndpoint.getOutInterceptors().add(wssOut);
		WSS4JInInterceptor wssIn = new WSS4JInInterceptor(in);
		cxfEndpoint.getInInterceptors().add(wssIn);

		if (!security.equals("NoSecurity") && edugainAct.equals("true")) {
			/*
			 * Properties edugainProps = new Properties(); edugainProps.load(edugain.openStream());
			 * 
			 * Edugain loader = new Edugain(edugain);
			 * 
			 * EdugainSupport edugainInInterceptor = new EdugainSupport(loader.getPropsLoaderForWGui(), edugainAct); EdugainSupport
			 * edugainOutInterceptor = new EdugainSupport(loader.getPropsLoaderForWGui(), edugainAct);
			 * cxfEndpoint.getInInterceptors().add(edugainInInterceptor); cxfEndpoint.getOutInterceptors().add(edugainOutInterceptor);
			 */
		}
	}

	private XPathExpression compileXpathExpression()
			throws XPathExpressionException {

		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext(new WSSENamespaceContext());
		return xpath.compile("//wsse:BinarySecurityToken");
	}

	public String extractBstFromSoapEnvelope(Document doc)
			throws XPathException, SecurityTokenNotFoundException {

		NodeList nl = (NodeList) this.xpath.evaluate(doc, XPathConstants.NODESET);

		for (int i = 0; i < nl.getLength(); i++) {
			Element e = (Element) nl.item(i);
			String valueType = e.getAttribute("ValueType");
			if (valueType != null && valueType.equals(WSS_X509_TOKENPROFILE)) {
				// token found
				return e.getTextContent();
			}
		}
		throw new SecurityTokenNotFoundException();
	}

	/**
	 * This class comes from the PerfSONAR sources.
	 */
	static public class WSSENamespaceContext implements NamespaceContext {

		public String getNamespaceURI(String prefix) {
			return "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
		}

		// This method isn't necessary for XPath processing either.
		public String getPrefix(String uri) {
			throw new UnsupportedOperationException();
		}

		// This method isn't necessary for XPath processing either.
		public Iterator<?> getPrefixes(String uri) {
			throw new UnsupportedOperationException();
		}
	}
}
