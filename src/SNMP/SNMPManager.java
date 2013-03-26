package SNMP;

import java.io.IOException;
import java.util.Date;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import GIM.MeasuredLoad;
import GIM.PowerMonitorLog;

public class SNMPManager {
Snmp snmp = null;
String address = null;

int SNMPversion = 3; 

String ver3Username = "apc1";
String ver3AuthPasscode = "admin user phrase";

/**
* Constructor
* @param add
*/

public SNMPManager(String add)
{
address = add;
}

 

/**
* Start the Snmp session. If you forget the listen() method you will not
* get any answers because the communication is asynchronous
* and the listen() method listens for answers.
* @throws IOException
*/

public void start() throws IOException {
TransportMapping transport = new DefaultUdpTransportMapping();

if(SNMPversion ==3){
	USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
	SecurityModels.getInstance().addSecurityModel(usm);
}
snmp = new Snmp(transport);

if(SNMPversion == 3)
	snmp.getUSM().addUser(new OctetString(ver3Username), new UsmUser(new OctetString(ver3Username), AuthMD5.ID, new OctetString(ver3AuthPasscode), null, null));


// Do not forget this line!
transport.listen();
}

/**
* Method which takes a single OID and returns the response from the agent as a String.
* @param oid
* @return
* @throws IOException
*/

public String getAsString(OID oid) throws IOException {
	ResponseEvent event = get(new OID[] { oid });
	return event.getResponse().get(0).getVariable().toString();
}

public String setIntFromString(int value, OID oid) throws IOException {

	ResponseEvent event = set(new OID[] { oid }, value);
	
	return event.getResponse().get(0).getVariable().toString();
	
}


public ResponseEvent set(OID oids[], int value) throws IOException {
	
	
	PDU pdu;
	
	if(SNMPversion == 3)
		 pdu = new ScopedPDU();
	else  pdu = new PDU();
	
	 
	for (OID oid : oids) {
		pdu.add(new VariableBinding(oid,new Integer32(value)));
	}
	
	pdu.setType(PDU.SET);
	ResponseEvent event = null;
	
	try{
		if(SNMPversion == 3)
			event = snmp.send(pdu, getSNMPv3Target(), null);
		else event = snmp.send(pdu, getTarget(), null);	

	
	}catch(IOException ioe){
		System.out.println("Error SNMP SET");
	}
	
	if(event != null) {
		return event;
		}
		throw new RuntimeException("SET timed out");
}


/**
* This method is capable of handling multiple OIDs
* @param oids
* @return
* @throws IOException
*/

public ResponseEvent get(OID oids[]) throws IOException {
	PDU pdu;
	
	if(SNMPversion == 3)
		 pdu = new ScopedPDU();
	else  pdu = new PDU();

for (OID oid : oids) {
	pdu.add(new VariableBinding(oid));
}

pdu.setType(PDU.GET);

ResponseEvent response;

if(SNMPversion == 3)
	response = snmp.send(pdu, getSNMPv3Target());
else response = snmp.send(pdu, getTarget());

	
if(response != null) {
	PDU responsePDU = response.getResponse();
    if (responsePDU != null) {
        if (responsePDU.getErrorStatus() == PDU.noError) {
            return response;
        }
    }throw new RuntimeException("reposne was null");
}
throw new RuntimeException("GET timed out");
}

/**
* This method returns a Target, which contains information about
* where the data should be fetched and how.
* @return
*/

private Target getSNMPv3Target() {
	Address targetAddress = GenericAddress.parse(address);
	UserTarget target = new UserTarget();
	
	target.setAddress(targetAddress);
	
    target.setVersion(SnmpConstants.version3); //SnmpConstants.version3
    target.setRetries(2);
    target.setTimeout(2500);
    target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV); //SecurityLevel.AUTH_NOPRIV
    target.setSecurityName(new OctetString(ver3Username));
    
    return target;
}


private Target getTarget() {

Address targetAddress = GenericAddress.parse(address);
CommunityTarget target = new CommunityTarget();


target.setCommunity(new OctetString("public"));

target.setAddress(targetAddress);

target.setRetries(2);

target.setTimeout(1500);

target.setVersion(SnmpConstants.version2c);

return target;
}

}
