package net.i2cat.mantychore.network.repository.tests;

import net.i2cat.mantychore.network.model.NetworkModel;
import net.i2cat.mantychore.network.model.domain.NetworkDomain;
import net.i2cat.mantychore.network.model.topology.Interface;
import net.i2cat.mantychore.network.repository.NetworkMapperDescriptorToModel;
import org.opennaas.core.resources.helpers.MockNetworkDescriptor;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.ResourceException;

public class NetworkMapperModelToDescriptorTest {

	@Test
	public void testMockWrapperNetworkElems () {
		try  {
			NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(MockNetworkDescriptor.newNetworkDescriptorWithNetworkDomain());
			Assert.assertNotNull(networkModel.getNetworkElements());
			Assert.assertEquals(networkModel.getNetworkElements().size(),1);			
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}
	}

	
	@Test
	public void testMockWrapperModel () {
		try {
			

			NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(MockNetworkDescriptor.newNetworkDescriptorWithNetworkDomain());
			NetworkDomain networkDomain =  (NetworkDomain) networkModel.getNetworkElements().get(0);
			
			Assert.assertNotNull(networkDomain.getHasDevice());
			Assert.assertEquals(networkDomain.getHasDevice().get(0).getName(),"router:R-AS2-1");			
			Assert.assertNotNull(networkDomain.getHasDevice().get(0).getInterfaces());
			Assert.assertEquals(networkDomain.getHasDevice().get(0).getInterfaces().size(),3);			
			Assert.assertEquals(networkDomain.getHasDevice().get(0).getInterfaces().get(0).getName(),"router:R-AS2-1:lt-1/2/0.51");
			Assert.assertEquals(networkDomain.getHasDevice().get(0).getInterfaces().get(1).getName(),"router:R-AS2-1:lt-1/2/0.100");
			Assert.assertEquals(networkDomain.getHasDevice().get(0).getInterfaces().get(2).getName(),"router:R-AS2-1:lo0.1");
			
			Assert.assertEquals(networkDomain.getHasDevice().get(1).getName(),"router:R-AS2-2");			
			Assert.assertNotNull(networkDomain.getHasDevice().get(1).getInterfaces());
			Assert.assertEquals(networkDomain.getHasDevice().get(1).getInterfaces().size(),3);			
			Assert.assertEquals(networkDomain.getHasDevice().get(1).getInterfaces().get(0).getName(),"router:R-AS2-2:lt-1/2/0.102");
			Assert.assertEquals(networkDomain.getHasDevice().get(1).getInterfaces().get(1).getName(),"router:R-AS2-2:lt-1/2/0.101");
			Assert.assertEquals(networkDomain.getHasDevice().get(1).getInterfaces().get(2).getName(),"router:R-AS2-2:lo0.3");

			Assert.assertEquals(networkDomain.getHasDevice().get(2).getName(),"router:R-AS2-3");
			Assert.assertNotNull(networkDomain.getHasDevice().get(2).getInterfaces());
			Assert.assertEquals(networkDomain.getHasDevice().get(2).getInterfaces().size(),2);			
			Assert.assertEquals(networkDomain.getHasDevice().get(2).getInterfaces().get(0).getName(),"router:R-AS2-3:lt-1/2/0.103");
			Assert.assertEquals(networkDomain.getHasDevice().get(2).getInterfaces().get(1).getName(),"router:R-AS2-3:lo0.4");
	
			
		} catch (ResourceException e) {
			Assert.fail(e.getMessage());
		}
		
	}

	
	
	@Test
	public void testMockWrapperNetworkDomain(){
			try {
				
				NetworkModel networkModel = NetworkMapperDescriptorToModel.descriptorToModel(MockNetworkDescriptor.newNetworkDescriptorWithNetworkDomain());
				NetworkDomain networkDomain =  (NetworkDomain) networkModel.getNetworkElements().get(0);
				Assert.assertEquals(networkDomain.getHasDevice().size(),3);
							
				
			} catch (ResourceException e) {
				Assert.fail(e.getMessage());
			}
		
	}
}
