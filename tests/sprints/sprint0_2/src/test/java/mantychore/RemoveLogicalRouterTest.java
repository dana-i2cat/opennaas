package mantychore;

public class RemoveLogicalRouterTest {

	public void RemoveLogicalRouterAdminPhysical() {
		// chassis:deleteLogicalRouter
		// check chassis:listLogicalRouters from R1 does not includes L1

	}

	/**
	 * This test is deprecated net/i2cat/nexus/resources/tests/ResourceManagerTest.java/testRemoveResource. This test is responsible for testing this
	 * story
	 * 
	 * **/
	@Deprecated
	public void RemoveLogicalRouterLogicalProvider() {
		// resource:remove L1
		// check that resource is not in the repo
		// check chassis:listLogicalRouters from R1 does includes L1
	}

	public void failRemoveCreateLogicalRouterTest() {
		// chassis:createLogicalRouter R1 L1 fe-0/0/1.1 fe-0/0/1.3 fe-0/0/1.2
		// check logical router creation
		// resource:start L1
		// chassis:removeInterface R1 L1 fe-0/0/1.1
		// test fail, cannot add new interfaces when the L1 resource is started
		// restore configuration

	}

}