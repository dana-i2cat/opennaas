package net.i2cat.mantychore.commandsets.junos.digester;

import java.util.HashMap;

import net.i2cat.mantychore.model.EthernetPort;

public class DummyAdder {
	int								index				= 0;
	HashMap<String, EthernetPort>	hashMapInterfaces	= new HashMap<String, EthernetPort>();

	public void addLogicalPort(EthernetPort ethernetPort) {

		// String location = ethernetPort.getOtherPortType();
		// EthernetPort hashEthernetPort = hashMapInterfaces.get(location);
		// if (hashMapInterfaces.containsKey(location)) {
		// hashEthernetPort.merge(ethernetPort);
		// hashMapInterfaces.remove(location);
		// }
		hashMapInterfaces.put(String.valueOf(index++), ethernetPort);

	}

}
