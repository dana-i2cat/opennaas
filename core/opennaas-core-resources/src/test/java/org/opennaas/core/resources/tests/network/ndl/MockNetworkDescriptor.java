package org.opennaas.core.resources.tests.network.ndl;

import java.util.ArrayList;
import java.util.List;

import org.opennaas.core.resources.descriptor.network.Device;
import org.opennaas.core.resources.descriptor.network.Interface;
import org.opennaas.core.resources.descriptor.network.InterfaceId;
import org.opennaas.core.resources.descriptor.network.Link;
import org.opennaas.core.resources.descriptor.network.NetworkTopology;

public class MockNetworkDescriptor {
	
	public static NetworkTopology newNDLNetworkDescriptor () {
		NetworkTopology rdfDescriptor = new NetworkTopology();
		
		
		/*  set new devices */
		List<Device> devices = new ArrayList<Device> ();

		/* new device */
		Device logicalRouter = new Device();
		logicalRouter.setName("router:R-AS2-1");
		
		/* interfaces */
		List<InterfaceId> hasInterfaces = new ArrayList<InterfaceId>();

		InterfaceId LR1interfaceId = new InterfaceId();
		LR1interfaceId.setResource("#router:R-AS2-1:lt-1/2/0.51");
		hasInterfaces.add(LR1interfaceId);
		
		InterfaceId LR1interfaceId2 = new InterfaceId();
		LR1interfaceId2.setResource("#router:R-AS2-1:lt-1/2/0.100");
		hasInterfaces.add(LR1interfaceId2);

		InterfaceId LR1interfaceId3 = new InterfaceId();
		LR1interfaceId3.setResource("#router:R-AS2-1:lo0.1");		
		hasInterfaces.add(LR1interfaceId3);
		
		logicalRouter.setHasInterfaces(hasInterfaces);

		devices.add(logicalRouter);


		Device logicalRouter2 = new Device();
		logicalRouter2.setName("router:R-AS2-2");
		
		/* interfaces */
		List<InterfaceId> hasInterfaces2 = new ArrayList<InterfaceId>();

		InterfaceId LR2interfaceId = new InterfaceId();
		LR2interfaceId.setResource("#router:R-AS2-2:lt-1/2/0.100");
		hasInterfaces2.add(LR2interfaceId);
		
		InterfaceId LR2interfaceId2 = new InterfaceId();
		LR2interfaceId2.setResource("#router:R-AS2-2:lt-1/2/0.101");
		hasInterfaces2.add(LR2interfaceId2);

		InterfaceId LR2interfaceId3 = new InterfaceId();
		LR2interfaceId3.setResource("#router:R-AS2-2:lo0.3");		
		hasInterfaces2.add(LR2interfaceId3);
		
		logicalRouter2.setHasInterfaces(hasInterfaces2);

		devices.add(logicalRouter2);
		

		Device logicalRouter3 = new Device();
		logicalRouter3.setName("router:R-AS2-3");
		/* interfaces */
		List<InterfaceId> hasInterfaces3 = new ArrayList<InterfaceId>();

		InterfaceId LR3interfaceId = new InterfaceId();
		LR3interfaceId.setResource("#router:R-AS2-3:lt-1/2/0.103");
		hasInterfaces3.add(LR3interfaceId);
		
		InterfaceId LR3interfaceId2 = new InterfaceId();
		LR3interfaceId2.setResource("#router:R-AS2-3:lo0.4");
		hasInterfaces3.add(LR3interfaceId2);

		
		logicalRouter3.setHasInterfaces(hasInterfaces3);

		
		devices.add(logicalRouter3);
		
		
		
		rdfDescriptor.setDevices(devices);
		

		
		
		List<Interface> interfaces = new ArrayList<Interface>();
		interfaces.add(newInterface("router:R-AS2-1:lt-1/2/0.51","#router:R1:lt-1/2/0.50","1.2E+9"));

		interfaces.add(newInterface("router:R-AS2-1:lt-1/2/0.100","#router:R-AS2-2:lt-1/2/0.101","1.2E+9"));
		interfaces.add(newInterface("router:R-AS2-2:lt-1/2/0.101","#router:R-AS2-1:lt-1/2/0.100","1.2E+9"));
		interfaces.add(newInterface("router:R-AS2-2:lt-1/2/0.102","#router:R-AS2-3:lt-1/2/0.103","1.2E+9"));
		
		interfaces.add(newInterface("router:R-AS2-3:lt-1/2/0.103","#router:R-AS2-2:lt-1/2/0.102","1.2E+9"));

		
		

		
		
		rdfDescriptor.setInterfaces(interfaces );
		
		
		return rdfDescriptor;		
	}
	
	public static Interface newInterface(String name, String linkName, String capacity) {
		Interface interf = new Interface();
		Link link = new Link();
		link.setName(linkName);
		interf.setLinkTo(link);
		interf.setName(name);
		interf.setCapacity(capacity);
		
		return interf;
	}

}
