package net.i2cat.mantychore.commandsets.junos.tests;

import net.i2cat.mantychore.commandsets.junos.commands.CommitCommand;
import net.i2cat.mantychore.commons.Command;
import net.i2cat.mantychore.commons.CommandException;
import net.i2cat.mantychore.commons.Response;
import net.i2cat.mantychore.protocols.netconf.NetconfProtocolSessionFactory;
import net.i2cat.nexus.protocols.sessionmanager.IProtocolSession;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolException;
import net.i2cat.nexus.protocols.sessionmanager.ProtocolSessionContext;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandTestsHelper {
	private  Logger			log			= LoggerFactory.getLogger(CommandTestsHelper.class);
	ProtocolSessionContext protocolSessionContext;
	
	public CommandTestsHelper (ProtocolSessionContext protocolSessionContext) {
		this.protocolSessionContext = protocolSessionContext;
	}
	
	
	public  void prepareAndSendCommand (Command command, Object params, Object modelToUpdate) throws ProtocolException, CommandException {

		command.setParams(params);
		command.initialize();

			
		IProtocolSession protocolSession = getProtocolSession();
	
		//THE COMMAND MUST BE INITIALIZED, AND THE PARAMETERS HAVE TO BE ADDED
		Response response = sendCommandToProtocol(command,protocolSession,modelToUpdate);
		
		//check response
		//Correct operation
		
		/*  check response message */
		Assert.assertTrue(response.getStatus().equals(Response.Status.OK));		
		log.info("OK : message is OK!");
		if (response.getErrors()!=null && response.getErrors().size()>0) {
			log.info("Include error messages: ");			
			for (String errorMsg : response.getErrors()) log.info(errorMsg);
			
		}
		
		Response responseCommit = sendCommit(protocolSession);
		
		
		/*  check response commit */
		Assert.assertTrue(responseCommit.getStatus().equals(Response.Status.OK));
		log.info("OK : message commit is OK!");
		if (responseCommit.getErrors()!=null && responseCommit.getErrors().size()>0) {
			log.info("Include error messages: ");			
			for (String errorMsg : response.getErrors()) log.info(errorMsg);
			
		}
		
		
		
	}

	
	private  Response sendCommit (IProtocolSession protocolSession) throws ProtocolException, CommandException {
		Command commit = new CommitCommand();
		return sendCommandToProtocol(commit,protocolSession,null);
	}
	

	private  IProtocolSession getProtocolSession() throws ProtocolException {
		// TODO Auto-generated method stub
		String sessionId = "sessionid"; //HARDCODED 
		
		NetconfProtocolSessionFactory protocolSessionFactory = new NetconfProtocolSessionFactory();
		IProtocolSession protocolSession;
		protocolSession = protocolSessionFactory.createProtocolSession(sessionId, protocolSessionContext);

		
		return protocolSession;
	}
	
	
	private  Response sendCommandToProtocol(Command command, IProtocolSession protocol, Object modelToUpdate) throws ProtocolException, CommandException {
		protocol.connect();
		log.info("Connected");
		
		log.info("sending and parsing message");
		Object messageResp = protocol.sendReceive(command.message());
		
		protocol.disconnect();
		log.info("Disconnected");

		Response response = command.checkResponse(messageResp);
		// If it was not problems with the operation, we can update the model
		if (response.getStatus().equals(Response.Status.OK)) {
			command.parseResponse(messageResp, modelToUpdate);
		}
		return response;
	}
	
}
