package mantychore;
public class CreateLogicalRouterTest {
	/*
	 * all types interfaces
	 */

	public void createLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check that the interface is included in the L1
	}

	public void listLogicalRoutersTest() {
		// chassis:listLogicalRouters
		// check if the command works
	}

	public void discoveryLogicalRoutersTest() {
		// resource:create
		// resource:start
		// resource:list
		// check logical routers are in the list
		// check resource initialized
		// check descriptors include IP capability

	}

	public void addAndRemoveInterfaceLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// check interface is included in the L1
		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// check interface is not included in the L1
		// restore configuration
	}

	public void failAddCreateLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// resource:start L1
		// chassis:addInterface R1 L1 fe-0/0/1.1
		// test fail, cannot add new interfaces when the L1 resource is started
		// restore configuration

	}

	public void failRemoveCreateLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// resource:start L1
		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// test fail, cannot add new interfaces when the L1 resource is started
		// restore configuration

	}

	public void logicalRouterConfigureInterfaceEthernetTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration
	}

	public void logicalRouterConfigureInterfaceLogicalTunnelTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration

	}

	public void logicalRouterConfigureInterfaceLoopbackTest() {
		// ipv4:setIP fe-0/0/1.1 192.168.1.2 255.255.255.0
		// check that command fails if interface doesn't exist
		// check updated interface if exists
		// restore configuration

	}

}