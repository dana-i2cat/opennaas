package org.opennaas.extensions.protocols.tl1.message;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class parses the output message received on the TCP Connection.
 * 
 * @author Mathieu Lemay
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1OutputParser extends TL1OutputMsg {
    /** logger */
	static private Logger logger = LoggerFactory.getLogger(TL1OutputParser.class);

    /** Creates a new instance of TL1OutputParser */
    private TL1OutputParser() {
    }

    /**
     * Parses the message to create a specific output Message object
     * 
     * @param rawOutput
     *            Raw Message Output to be parsed
     * @return TL1OutputMsg a basic output message you have to getType() it to
     *         retrieve the right type and recreate the correct object pointer
     * @throws TL1ParserException
     *             When a parsing error occurs TL1ParserException is thrown
     */
    public static TL1OutputMsg parse(String rawOutput) throws TL1ParserException {
        StringTokenizer command, line;
        String msgType, sid;
        TL1Date date;
        TL1Time time;
        TL1Line[] payload;
        TL1OutputMsg message = null;
        logger.debug("Parsing Output Message");
        if (rawOutput == null)
            throw new TL1ParserException("raw Output is null");
        int index = rawOutput.length();
        index--;
        logger.debug("Raw Message: " + rawOutput);

        char termCode = rawOutput.charAt(index);
        logger.debug("term code:" + termCode);
        
        try {
            if (termCode == ';' || termCode == '>') {
                /* parser=new StringTokenizer(parsing," ",false); */
                command = new StringTokenizer(rawOutput, "\r\n");
                line = new StringTokenizer(command.nextToken(), " ^");
                if (line.countTokens() == 0)
                    line = new StringTokenizer(command.nextToken(), " ^");
                sid = line.nextToken().trim();
                date = new TL1Date(line.nextToken().trim());
                time = new TL1Time(line.nextToken().trim());

                line = new StringTokenizer(command.nextToken(), " ^");
                msgType = line.nextToken();
                logger.debug("Message type " + msgType.charAt(0));
                // Parse Response Message
                if (msgType.charAt(0) == 'M') {
                    TL1ResponseMsg tempMsg = new TL1ResponseMsg();
                    logger.debug("Reponse Message");
                    //scslog.debug(msgType);
                    tempMsg.setType(RESP_TYPE);
                    tempMsg.setSID(sid);
                    tempMsg.setTime(time);
                    tempMsg.setDate(date);
                    tempMsg.setCTAG(line.nextToken().trim());
                    if (tempMsg.getCTAG().equals("0")){
                    	//This is an ACK for the message sent by the keep-alive thread, discard it
                    	tempMsg.setType(PROMPT_TYPE);
                        return tempMsg;
                    }
                    tempMsg.setCmdCode(line.nextToken().trim());
                    tempMsg.setTermCode(termCode);
                    tempMsg.setRaw(rawOutput);
                    message = tempMsg;
                }
                // Parse Alarm Message
                else {
                    TL1AutonomousMsg tempMsg = new TL1AutonomousMsg();
                    logger.debug("Alarm");
                    tempMsg.setType(AUTO_TYPE);
                    tempMsg.setSID(sid);
                    tempMsg.setTime(time);
                    tempMsg.setDate(date);
                    tempMsg.setAlarmCode(msgType);
                    tempMsg.setTermCode(termCode);
                    tempMsg.setATAG(line.nextToken());
                    tempMsg.setVerb(line.nextToken());
                    if (line.hasMoreTokens())
                        tempMsg.setMD1(line.nextToken());
                    if (line.hasMoreTokens())
                        tempMsg.setMD2(line.nextToken());
                    message = tempMsg;
                }
                if (command.hasMoreTokens()) {
                    int pllines = command.countTokens();
                    payload = new TL1Line[pllines];
                    for (int i = 0; i < pllines; i++) {
                        payload[i] = new TL1Line(command.nextToken().trim());
                    }
                    message.setPayload(payload);
                }
            }
            else {
            	String ackCode = rawOutput.substring(0, 2);
            	logger.debug("Potential ACK code: "+ackCode);
                TL1AckMsg tempMsg = new TL1AckMsg();
                tempMsg.setType(ACK_TYPE);
                tempMsg.setTermCode(termCode);
                tempMsg.setAckCode(ackCode);
                if (!tempMsg.isValidAckCode()) {
                	//We have detected a prompt or a welcome message
                    tempMsg.setType(PROMPT_TYPE);
                    return tempMsg;
                }

                command = new StringTokenizer(rawOutput, "\r\n");
                line = new StringTokenizer(command.nextToken(), " ");
                line.nextToken();
                tempMsg.setCTAG(line.nextToken());
                message = tempMsg;
            }
        }
        catch (Exception e) {
        	logger.error(e.getMessage());
            throw new TL1ParserException(rawOutput);
        }

        return message;
    }
}