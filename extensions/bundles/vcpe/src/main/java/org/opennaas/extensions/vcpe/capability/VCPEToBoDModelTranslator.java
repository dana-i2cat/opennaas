package org.opennaas.extensions.vcpe.capability;

import org.opennaas.extensions.vcpe.model.Interface;

public class VCPEToBoDModelTranslator {

	public static org.opennaas.extensions.network.model.topology.Interface
			vCPEInterfaceToBoDInterface(Interface iface) {

		org.opennaas.extensions.network.model.topology.Interface netIface = new org.opennaas.extensions.network.model.topology.Interface();
		netIface.setName(iface.getName());

		return netIface;
	}

}
