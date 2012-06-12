package org.opennaas.extensions.protocols.tl1;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.opennaas.core.resources.protocol.IProtocolMessageFilter;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionListener;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.core.resources.transport.IStreamTransport;
import org.opennaas.core.resources.transport.ITransport;
import org.opennaas.core.resources.transport.TransportException;
import org.opennaas.extensions.protocols.tl1.message.TL1AckMsg;
import org.opennaas.extensions.protocols.tl1.message.TL1InputMsg;
import org.opennaas.extensions.protocols.tl1.message.TL1OutputMsg;
import org.opennaas.extensions.protocols.tl1.message.TL1ResponseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is used to open a TL1 session and send and receive message to and
 * from the switch.
 * 
 * @author Mathieu Lemay
 * @author Eduard Grasa
 * @version 1.0
 */
public class TL1ProtocolSession implements IProtocolSession{
	
    /** TL1 Session Log */
    static private Logger logger = LoggerFactory.getLogger(TL1ProtocolSession.class);
    
    /** The receive worker, gets all the chars from the communications channel,
     * assembles and parses TL1 messages and either sends a notification (autonomous messages) or
     * adds them to the syncResponseQueue
     */
    private TL1StreamReader tl1StreamReader;

    /** Number of retries when receiving the Bad Acknowledgments */
    public static final int NUMRETRIES = 1;
    
    /** The queue where parsed response message are sent **/
    private BlockingQueue<TL1OutputMsg> syncResponseMessageQueue;
    
    /** Contains information about the protocol session configuration: transport, host, port, ... **/
    private ProtocolSessionContext protocolSessionContext = null;
    
    private String sessionID = null;
    
    private IStreamTransport transport = null;
    private Status									status					= null;
    private Map<String, IProtocolSessionListener>	protocolListeners		= null;
	private Map<String, IProtocolMessageFilter>		protocolMessageFilters	= null;

    /**
     * Creates a new instance of a TL1 Session
     * @param capabilityDescriptor
     * @throws ProtocolException
     */
    public TL1ProtocolSession(ProtocolSessionContext protocolSessionContext, String sessionID) throws ProtocolException {
    	this.protocolSessionContext = protocolSessionContext;
    	this.sessionID = sessionID;
    	this.protocolListeners = new HashMap<String, IProtocolSessionListener>();
		this.protocolMessageFilters = new HashMap<String, IProtocolMessageFilter>();
		this.status = Status.DISCONNECTED_BY_USER;
        
        //Initialize the received message queue
        syncResponseMessageQueue = new LinkedBlockingQueue<TL1OutputMsg>();
    }
    
    public void wireTransport(ITransport transport) throws ProtocolException{
    	if  (!(transport instanceof IStreamTransport)){
    		throw new ProtocolException("TL1 transports must be stream transports");
    	}
    	
    	this.transport = (IStreamTransport) transport;
    }
    
    public BlockingQueue<TL1OutputMsg> getSyncResponseMessageQueue(){
    	return syncResponseMessageQueue;
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
     * If the TL1 Stream reader detects that the session has been interrupted, it will invoke this operation to
     * restart to the session with the managed device
     * @throws EngineException
     */
    public void restartSession(){
    	try{
    		stopTL1StreamReader();
    		disconnectTransport();
    		startSession();
    	}catch(ProtocolException ex){
    		ex.printStackTrace();
    	}
    }

    public void notify(Object message){
    	Iterator<IProtocolSessionListener> protocolSessionListeners = this.protocolListeners.values().iterator();
    	while(protocolSessionListeners.hasNext()){
    		protocolSessionListeners.next().messageReceived(message);
    	}
    }
    
    /**
     * Opens a connection to the specific host/port
     * 
     * @throws FailedCmdException
     *             Exception thrown if command failed
     */
    private void startSession() throws ProtocolException {
    	logger.info("Starting protocol session");
    	//Start the receiver thread it will continuously get and parse the messages from the communication channel
    	try{
    		transport.connect();
    		createAndStartTL1StreamReader();
    		loginToDevice();
    	}catch(TransportException ex){
    		throw new ProtocolException("Could not connect to the managed device", ex);
    	}catch(ProtocolException ex){
    		//Stop the tl1 stream reader
    		tl1StreamReader.die = true;
    		throw ex;
    	}
    }
    
    private void createAndStartTL1StreamReader() throws ProtocolException{
    	try{
    		tl1StreamReader = new TL1StreamReader(transport.getInputStream(), new char[]{ ';', '>', '<' }, 
    				syncResponseMessageQueue, this);
    		tl1StreamReader.start();
    	}catch(TransportException ex){
    		throw new ProtocolException("Problems getting the transport input stream", ex);
    	}
    }
    
    private void loginToDevice() throws ProtocolException{
    	String username = (String) this.protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.USERNAME);
    	String password = (String) this.protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.PASSWORD);
		sendWaitResponse("ACT-USER::"+username+":123::"+password+";");
    }
    
    /**
     * Closes the connection
     */
    private void stopSession() throws ProtocolException {
        logoutFromDevice();
        stopTL1StreamReader();
    	disconnectTransport();
    	logger.info("Protocol session stopped");
    }
    
    private void logoutFromDevice() throws ProtocolException{
    	String username = (String) this.protocolSessionContext.getSessionParameters().get(ProtocolSessionContext.USERNAME);
    	sendWaitResponse("CANC-USER::"+username+":178;");
    }
    
    private void stopTL1StreamReader(){
    	tl1StreamReader.interrupt();
    	tl1StreamReader.die = true;
    }
    
    private void disconnectTransport() throws ProtocolException{
    	try{
    		transport.disconnect();
    	}catch(TransportException ex){
    		throw new ProtocolException("Problems disconnecting the transport ", ex);
    	}
    }

    /**
     * Sends out TL1 command to the agent.
     * 
     * @param message
     *            Command to send in TL1InputMsg Format
     * @return TL1ResponseMsg
     * @throws FailedCmdException
     *             Exception thrown if command failed
     */
    public Object sendWaitResponse(Object message) throws ProtocolException{
        Object msg;

        if (message instanceof TL1InputMsg) {
        	TL1InputMsg request = (TL1InputMsg) message;
        	msg = (Object) this.sendCmdWait(request.toString());
        }
        else{
        	msg = (Object) this.sendCmdWait((String) message);
        }
        return msg;
    }

    /**
     * Sends an array of TL1 commands to the device
     */
    public Object[] sendWaitResponse(Object[] messageList) throws ProtocolException{
        Object[] msg = new Object[messageList.length];
        for (int i = 0; i < messageList.length; i++) {
            msg[i] = sendWaitResponse(messageList[i]);
        }
        return msg;
    }

    /**
     * Sends out TL1 command to the agent. 
     * 
     * If you are using the Virtual Transport
     * for testing, you will have to disable the CTAG Checking. This is because the
     * CTAG is generated before sending the command, but the CTAG returning from the
     * switch-simulation xml files is fixed and will not match. The affected code below 
     * is marked with comments. The SCS will have to be re-compiled and restarted after
     * the ctag checking is disabled. Don't forget to enable it again when you're finished
     * with your testing.
     * 
     * @param request
     *            TL1 raw command
     * @return TL1ResponseMsg
     * @throws FailedCmdException
     *             Exception thrown if command failed
     */
    private TL1ResponseMsg sendCmdWait(String request) throws ProtocolException {
    	TL1OutputMsg tl1ResponseMessage = null;
    	TL1ResponseMsg finalTL1ResponseMessage = null;
        boolean retry = true;  
    	
        String ctag = getCTAGFromMessage(request);
        
    	//Send the TL1 message
    	sendCmdNoWait(request);
    	
    	//wait for the response in the synchronous message queue
    	boolean exit = false;
    	while(!exit){
    		try{
    			logger.debug("Waiting for the message response");
    			tl1ResponseMessage = syncResponseMessageQueue.take();
    			logger.debug("I've got a possible response, let's check if it's for me");
    			//Here we got an ACK message that matches the CTAG of the message we sent
    			if (gotACKMessage(tl1ResponseMessage, ctag)) {
    				logger.debug("I have an ACK message for me");
    				//If we got a busy status, we'll try to retransmit the message
    				if (gotBusyStatus(tl1ResponseMessage)) {
    					//If we're allowed to retransmit the message, do it, but just once
    					if (retry) {
    						retry = false;
    						waitAndSendMessageAgain(request);
    					}else{
    						break;
    					}
    				}
    			}else if (gotResponseMessage(tl1ResponseMessage, ctag)) { 
    				logger.debug("I've got a response message for me");
    				if(tl1ResponseMessage.getTermCode() == '>') {
    					finalTL1ResponseMessage = createOrAppendTL1ReponseMessage(tl1ResponseMessage, finalTL1ResponseMessage);                      
    				}else if(tl1ResponseMessage.getTermCode() == ';') {
    					finalTL1ResponseMessage = createOrAppendTL1ReponseMessage(tl1ResponseMessage, finalTL1ResponseMessage);  
    					exit = true;
    				}
    			}
    		}catch(InterruptedException e){
    		}
    	}
        
    	 if (finalTL1ResponseMessage == null) {
             throw (new ProtocolException("Could not get response from switch to command:\n"+request));
         }

         return finalTL1ResponseMessage;
    }
    
    private String getCTAGFromMessage(String message) throws ProtocolException{
    	//Get the correlation tag (always in the fourth section of the command)
        String ctag = null;
        try{
        	ctag = message.split(":")[3].split(";")[0];
        }catch(Exception ex){
        	throw new ProtocolException("The TL1 message did not contain a valid CTAG. The CTAG is required.");
        }
        logger.debug("CTAG for request is " + ctag);
        
        if (ctag == null || ctag.equals("")){
        	throw new ProtocolException("The TL1 message did not contain a CTAG. The CTAG is required.");
        }
        
        return ctag;
    }
    
    private boolean gotACKMessage(TL1OutputMsg tl1ResponseMessage, String ctag){
    	return tl1ResponseMessage.getType() == TL1OutputMsg.ACK_TYPE
			&& ctag.equals(((TL1AckMsg) tl1ResponseMessage).getCTAG());
    }

    private boolean gotBusyStatus(TL1OutputMsg tl1ResponseMessage){
    	return ((TL1AckMsg) tl1ResponseMessage).getAckCode().equals(TL1AckMsg.RETRY_LATER)
    	|| ((TL1AckMsg) tl1ResponseMessage).getAckCode().equals(TL1AckMsg.NO_ACKNOWLEDGEMENT)
    	|| ((TL1AckMsg) tl1ResponseMessage).getAckCode().equals(TL1AckMsg.NO_GOOD);
    }
    
    private void waitAndSendMessageAgain(String message) throws ProtocolException{
    	logger.debug("Got a busy Status. Will try again in 5 seconds");
		try {
			Thread.sleep(5000);
		}catch (InterruptedException e) {
			logger.info(e.getMessage(), e);
		}
		sendCmdNoWait(message);
    }
    
    private boolean gotResponseMessage(TL1OutputMsg tl1ResponseMessage, String ctag){
    	return tl1ResponseMessage.getType() == TL1OutputMsg.RESP_TYPE
			&& ctag.equals(((TL1ResponseMsg) tl1ResponseMessage).getCTAG());
    }
    
    private TL1ResponseMsg createOrAppendTL1ReponseMessage(TL1OutputMsg tl1ResponseMessage, TL1ResponseMsg finalTL1ResponseMessage){
    	if(finalTL1ResponseMessage == null) {
			finalTL1ResponseMessage = (TL1ResponseMsg)tl1ResponseMessage;                                                                                    
		}else {
			finalTL1ResponseMessage.append((TL1ResponseMsg)tl1ResponseMessage);
		} 
    	
    	return finalTL1ResponseMessage;
    }

    /**
     * Sends a command to the stream transport, without waiting for the request
     * @param request
     * @throws ProtocolException
     */
    private void sendCmdNoWait(String request) throws ProtocolException{
    	logger.info("Sending message: " + request);
    	try{
    		transport.send(request.toCharArray());
    	}catch(Exception ex){
    		ex.printStackTrace();
    		throw new ProtocolException("Problems sending this message to the managed device: "+ request, ex);
    	}
    }
    

    /**
     * Send a message without waiting for the response
     */
	public void sendDontWaitResponse(Object message) throws ProtocolException {
		if (message instanceof TL1InputMsg) {
			TL1InputMsg request = (TL1InputMsg) message;
			this.sendCmdNoWait(request.toString());
		}
		else{
			this.sendCmdNoWait((String) message);
		}
	}

	/**
	 * Sends an array of messages without waiting for the response
	 */
	public void sendDontWaitResponse(Object[] messageList) throws ProtocolException {
        for (int i = 0; i < messageList.length; i++) {
            sendDontWaitResponse(messageList[i]);
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