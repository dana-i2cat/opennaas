package net.i2cat.mantychore.protocols.netconf.message;

import java.util.List;

public class NetconfResponseMessage {

	private String rawMessage;
	private boolean hasErrors;
	private String processedMessage;

	public static NetconfResponseMessage parse(String rawMessage,
			List<String> stringsToRemove, List<String> errorPatterns) {
		
    	String processedMessage = rawMessage;
    	NetconfResponseMessage response = new NetconfResponseMessage();
    	
    	//Remove prompts and echoes
    	for(int i=0; i<stringsToRemove.size(); i++){
    		if (processedMessage.indexOf(stringsToRemove.get(i)) != -1){
    			processedMessage = processedMessage.replaceAll(stringsToRemove.get(i), "");
    		}
    	}
    	
    	//Look for errors
    	for(int i=0; i<errorPatterns.size(); i++){
    		if (processedMessage.indexOf(errorPatterns.get(i)) != -1){
    			response.hasErrors = true;
    		}
    	}
    	
    	response.rawMessage = rawMessage;
    	response.processedMessage = processedMessage;
    	
    	return response;
    }

	public String getProcessedMessage() {
		return processedMessage;
	}
	
	public boolean hasErrors(){
		return hasErrors;
	}
	
	

}
