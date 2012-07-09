package org.opennaas.extensions.protocols.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.TransportException;
import org.opennaas.extensions.protocols.cli.message.CLIInputMessage;
import org.opennaas.extensions.protocols.cli.message.CLIResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a CLI Session. When it is created, it logs in the 
 * remote agent. Then it sends messages to the agent, parses its responses and 
 * sends them back to the CLI Session client. A timer task wakes up every 90
 * seconds and sends a message specified by the client when initializing the session
 * (or a dummy message if no message is specified)
 * @author Eduard Grasa
 *
 */
public class CLIProtocolSession implements IProtocolSession{
	
	public static final String MODULE_NAME="CLI Protocol Session";
	public static final String MODULE_DESCRIPTION="CLI Protocol library";
	public static final String MODULE_VERSION="1.0.0";
	
	/** CLI Session Log */
    static private Logger logger = LoggerFactory.getLogger(CLIProtocolSession.class);
    
    /** Some constants **/
    public static final String HAS_GREETING = "Greeting";  //CLI has to wait for a greeting message from transport
    public static final String ENABLE_COMMAND = "enableCommand";
    public static final String ENABLE_USERNAME = "enableUsername";
    public static final String ENABLE_PASSWORD = "enablePassword";
    public static final String PROMPT = "prompt";
    public static final String INTERMEDIATE_PROMPT = "intermediatePrompt";
    public static final String INTERMEDIATE_PROMPT_COMMAND = "intermediatePromptCommand";
    public static final String ERROR_MESSAGE_PATTERNS= "errorMessagePatterns";
    public static final String KEEP_ALIVE_MESSAGE ="keepAliveMessage";
    
    /** The prompts used to detect the end of the message **/
    private List<String> prompts;
    
    /** Contains a pair of <intermediate_prompt, string to be send>, to make the remote 
     * agent continue sending the response message **/
    private Hashtable<String, String> intermediatePrompts;
    
    /** Contains some of the strings that appear when the response message is an error message **/
    private List<String> errorMessagePatterns;
    
    /** The class that will listen for response messages **/
    private CLIStreamReader cliStreamReader;
    
    /** Contains information about the protocol session configuration: transport, host, port, ... **/
    private ProtocolSessionContext protocolSessionContext = null;
    
    private String sessionID = null;
    
    private IStreamTransport transport = null;
    private Status									status					= null;
    private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;
    
    public CLIProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {
    	this.protocolSessionContext = protocolSessionContext;
    	this.sessionID = sessionID;
    	this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();
		this.status = Status.DISCONNECTED_BY_USER;
        setPromptsAndErrorPatterns();
    }
    
    private void setPromptsAndErrorPatterns() throws ProtocolException{
    	initializePromptsAndErrorPatterns();
    	extractPromptsAndErrorPatternsFromModuleProperties();
        checkMissingParameters();
    }
    
    private void initializePromptsAndErrorPatterns(){
    	prompts = new ArrayList<String>();
    	//TODO hardcoded since some of these parameters are not allowed in a URL
    	prompts.add("#");
    	prompts.add(":");
    	prompts.add(">");
        intermediatePrompts = new Hashtable<String, String>();
        errorMessagePatterns = new ArrayList<String>();
    }
    
    private void extractPromptsAndErrorPatternsFromModuleProperties(){
    	Iterator<Entry<String, Object>> sessionParametersIterator = this.protocolSessionContext.getSessionParameters().entrySet().iterator();
    	Entry<String, Object> currentEntry = null;
    	Entry<String, Object> currentEntry2 = null;
    	while(sessionParametersIterator.hasNext()){
    		currentEntry = sessionParametersIterator.next();
    		if (currentEntry.getKey().equals(PROMPT)){
    			String[] aux = ((String)currentEntry.getValue()).split("");
    			for(int i=1; i<aux.length; i++){
    				prompts.add(aux[i]);
    			}
        	}else if (currentEntry.getKey().equals(INTERMEDIATE_PROMPT)){
        		currentEntry2 = sessionParametersIterator.next();
        		if (currentEntry2.getKey().equals(INTERMEDIATE_PROMPT_COMMAND)){
        			intermediatePrompts.put((String)currentEntry.getValue(), (String)currentEntry2.getValue());
        		}
        	}else if (currentEntry.getKey().equals(ERROR_MESSAGE_PATTERNS)){
        		errorMessagePatterns.add((String)currentEntry.getValue());
        	}
    	}
    }
    
    private void checkMissingParameters() throws ProtocolException{
    	 //Check there is enough info to segment the messages and identify errors
        if (prompts.size() == 0){
        	throw new ProtocolException("No prompts received. CLI Session needs to know the prompt strings in order to " +
        			"segment the response messages.");
        }
        
        if (intermediatePrompts.isEmpty()){
        	logger.warn("No intermediate prompts received, strange.");
        }
    }
    
    public void wireTransport(ITransport transport) throws ProtocolException{
    	if  (!(transport instanceof IStreamTransport)){
    		throw new ProtocolException("CLI transports must be stream transports");
    	}
    	
    	this.transport = (IStreamTransport) transport;
    }
    
    @Override
	public void asyncSend(Object requestMessage) throws ProtocolException {
    	this.sendCmdNoWait((String) requestMessage);
    }
    
    @Override
	public Object sendReceive(Object requestMessage) throws ProtocolException {
    	return this.sendCmdWait((String)requestMessage);
    }
    
    @Override
	public void connect() throws ProtocolException {
    	startSession();
    }
    
    @Override
	public void disconnect() throws ProtocolException {
    	stopSession();
    }
    
    /**
     * Opens a connection to the specific host/port
     */
    private void startSession() throws ProtocolException{
    	logger.info("Starting protocol session");
    	try{
    		transport.connect();
    	}catch(TransportException ex){
    		throw new ProtocolException("Problems connecting to the managed device", ex);
    	}
    	createCLIStreamReader();
    	loginToDevice();
    }
    
    public void restartSession(){
    	try{
    		stopSession();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}

    	try{
    		transport.connect();
    		loginToDevice();
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }

    private void createCLIStreamReader() throws ProtocolException{
    	try{
    		cliStreamReader = new CLIStreamReader(getTransport().getInputStream(), getTransport().getOutputStream(), 
    				prompts, intermediatePrompts, errorMessagePatterns);
    	}catch(TransportException ex){
    		throw new ProtocolException("Problems getting the transport input stream", ex);
    	}
    }
    
    private IStreamTransport getTransport(){
    	return transport;
    }
    
    private void loginToDevice() throws ProtocolException{
    	if (this.protocolSessionContext.getSessionParameters().get(CLIProtocolSession.HAS_GREETING)!=null){
    		try{
    			cliStreamReader.getResponse("");//wait for greeting message
    		}catch(IOException e){
    			e.printStackTrace();
    			throw new ProtocolException("CLI cannot read greeting message: " + e.toString());
    		}
    	}
    	
    	String username = (String) this.protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.USERNAME);
    	if (username != null){
    		this.sendWaitResponse(username);
    	}
 		
    	String password = (String) this.protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PASSWORD);
    	if (password != null){
    		this.sendWaitResponse(password);
    	}
    }

    /**
     * Stop the protocol session: log out, stop the session keep-alive thread, disconnect the transport
     */
    public void stopSession() throws ProtocolException {
    	try{
    		getTransport().disconnect();
    	}catch(TransportException ex){
    		throw new ProtocolException("Problems disconnecting the transport", ex);
    	}
    }

	/**
     * Send a message without waiting for the response
     */
	public synchronized void sendDontWaitResponse(Object message) throws ProtocolException {
		if (message instanceof CLIInputMessage) {
			CLIInputMessage request = (CLIInputMessage) message;
			this.sendCmdNoWait(request.toString());
		}else{
			this.sendCmdNoWait((String) message);
		}
	}

	/**
	 * Sends an array of messages without waiting for the response
	 */
	public synchronized void sendDontWaitResponse(Object[] messageList) throws ProtocolException {
		for (int i = 0; i < messageList.length; i++) {
            sendDontWaitResponse(messageList[i]);
        }
	}

	/**
     * Sends out CLI command to the agent.
     * 
     * @param message
     *            Command to send in CLIInputMessage Format
     * @return CLIResponseMesssage
     * @throws FailedCmdException
     *             Exception thrown if command failed
     */
	public synchronized Object sendWaitResponse(Object message) throws ProtocolException {
		Object msg;
		if (message instanceof CLIInputMessage) {
			CLIInputMessage request = (CLIInputMessage) message;
			msg = (Object) this.sendCmdWait(request.toString());
		}else{
			msg = (Object) this.sendCmdWait((String) message);
		}
		return msg;
	}

	/**
     * Sends an array of CLI commands to the device
     */
	public synchronized Object[] sendWaitResponse(Object[] messageList) throws ProtocolException {
		Object[] msg = new Object[messageList.length];
        for (int i = 0; i < messageList.length; i++) {
            msg[i] = sendWaitResponse(messageList[i]);
        }
        return msg;
	}
	
	/**
	 * Sends a CLI message and waits for the response
	 * @param message
	 * @return
	 */
	private synchronized CLIResponseMessage sendCmdWait(String message) throws ProtocolException{
		//Send the message
		this.sendCmdNoWait(message);
		//Get the response
		try{
			return cliStreamReader.getResponse(message);
		}catch(IOException e){
			e.printStackTrace();
			restartSession();
			return this.sendCmdWait(message);
		}
	}
	
	 /**
     * Sends a command to the stream transport, without waiting for the request
     * @param request
     * @throws ProtocolException
     */
	private void sendCmdNoWait(String request) throws ProtocolException{
		try{
			getTransport().send(request.toCharArray());
		}catch(TransportException ex){
			throw new ProtocolException("Problems sending this message to the managed device: "+request, ex);
		}
	}
	
	@Override
	public ProtocolSessionContext getSessionContext() {
		return protocolSessionContext;
	}

	@Override
	public String getSessionId() {
		return sessionID;
	}

	@Override
	public Status getStatus() {

		return status;
	}

	@Override
	public void registerProtocolSessionListener(
			IProtocolSessionListener protocolSessionListener,
			IProtocolMessageFilter protocolMessageFilter, String idListener) {
		protocolMessageFilters.put(idListener, protocolMessageFilter);
		protocolListeners.put(idListener, protocolSessionListener);

	}

	@Override
	public void unregisterProtocolSessionListener(
			IProtocolSessionListener protocolSessionListener, String idListener) {
		protocolMessageFilters.remove(idListener);
		protocolListeners.remove(idListener);

	}

	@Override
	public void setSessionContext(ProtocolSessionContext context) {
		this.protocolSessionContext = context;
	}

	@Override
	public void setSessionId(String sessionId) {
		this.sessionID = sessionId;
	}
}