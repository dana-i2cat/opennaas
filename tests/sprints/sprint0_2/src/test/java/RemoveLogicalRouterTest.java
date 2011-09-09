public class RemoveLogicalRouterTest {
	
	public void RemoveLogicalRouterAdminPhysical () {
		//chassis:deleteLogicalRouter
		//check chassis:listLogicalRouters from R1 does not includes L1
		
	}
	/** This test is deprecated  
	 * net/i2cat/nexus/resources/tests/ResourceManagerTest.java/testRemoveResource. This test is responsible for testing this story
	 * 
	 * **/
	@Deprecated
	public void RemoveLogicalRouterLogicalProvider () {
		//resource:remove L1
		//check that resource is not in the repo
		//check chassis:listLogicalRouters from R1 does includes L1
	}
	
	
	
}