package org.opennaas.extensions.protocols.tl1.message;
/**
 * This class is used to create en Input message field by field instead of having to send the actual TL1 string syntax
 * it is used to simplify modifications on different sets of commands. (NOT IMPLEMENTED YET)
 * @author  Mathieu Lemay 
 * @author Research Technologist Communications Research Centre
 * @version 1.0.0a
 */
public class TL1InputMsg {
	/** Input message command code */
    private String commandCode;
    /** Target ID */
    private String tid;
    /** Access ID */
    private String aid;
    /** Command TAG */
    private String ctag;
    /** Payload Block */ 
    private TL1Line payloadBlock;
    /* Default Constructor
     * it will create a dummy InputMsg by default
     */
    /** Constructor for an Input Message
     */    
    public TL1InputMsg() {
    }
    
    /** Constructs the Input Message from a raw String command
     * @param command Raw String command
     */    
    public TL1InputMsg(String command){
        command=command.substring(0,command.lastIndexOf(";"));
        String[] cmdparser=command.split(":");
        commandCode=cmdparser[0];
        tid=cmdparser[1];
        aid=cmdparser[2];
        ctag=cmdparser[3];
        int index=0;
        for(int i=0;i<4;i++){
        index=command.indexOf(":",index+1);
        }
        if(cmdparser.length > 4){
            payloadBlock=new TL1Line(command.substring(index,command.length()));}
    }
    /**
	 * @return Returns the aid.
	 */
	public String getAid() {
		return aid;
	}
	/**
	 * @param aid The aid to set.
	 */
	public void setAid(String aid) {
		this.aid = aid;
	}
	/**
	 * @return Returns the commandCode.
	 */
	public String getCommandCode() {
		return commandCode;
	}
	/**
	 * @param commandCode The commandCode to set.
	 */
	public void setCommandCode(String commandCode) {
		this.commandCode = commandCode;
	}
	/**
	 * @return Returns the ctag.
	 */
	public String getCTAG() {
		return ctag;
	}
	/**
	 * @param ctag The ctag to set.
	 */
	public void setCTAG(String ctag) {
		this.ctag = ctag;
	}
	/**
	 * @return Returns the payloadBlock.
	 */
	public TL1Line getPayloadBlock() {
		return payloadBlock;
	}
	/**
	 * @param payloadBlock The payloadBlock to set.
	 */
	public void setPayloadBlock(TL1Line payloadBlock) {
		this.payloadBlock = payloadBlock;
	}
	/**
	 * @return Returns the tid.
	 */
	public String getTid() {
		return tid;
	}
	/**
	 * @param tid The tid to set.
	 */
	public void setTid(String tid) {
		this.tid = tid;
	}
	
    /** Converts TL1InputMsg to rawString
     * @return raw String
     */    
    public String toString(){
        //act-user::superuser:123::*********;
        if(payloadBlock!=null)
        return commandCode+":"+tid+":"+aid+":"+ctag+":"+payloadBlock.toString()+";";
        else
        return commandCode+":"+tid+":"+aid+":"+ctag+";";            
    }
}

