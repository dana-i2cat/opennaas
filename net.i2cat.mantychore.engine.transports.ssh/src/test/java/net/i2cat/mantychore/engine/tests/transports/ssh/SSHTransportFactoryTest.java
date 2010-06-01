package net.i2cat.mantychore.engine.tests.transports.ssh;

import java.util.ArrayList;
import java.util.List;

import net.i2cat.mantychore.engine.transports.ssh.SSHTransport;
import net.i2cat.mantychore.engine.transports.ssh.SSHTransportFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.iaasframework.engines.core.descriptor.ModuleDescriptor;
import com.iaasframework.engines.core.descriptor.ModuleProperty;
import com.iaasframework.engines.transports.ITransport;
import com.iaasframework.engines.transports.ITransportConstants;
import com.iaasframework.engines.transports.ITransportFactory;
import com.iaasframework.engines.transports.TransportException;


public class SSHTransportFactoryTest   {

	private ITransportFactory transportFactory = null;
	
	private ModuleDescriptor badFormedModuleDescriptor = null;
	private ModuleDescriptor wellFormedSSHModuleDescriptor = null;
	private ModuleDescriptor badFormedSSHModuleDescriptor = null;
	
	@Before
	public void setUp(){

		transportFactory = new SSHTransportFactory();
		
		badFormedModuleDescriptor = new ModuleDescriptor();
		
		badFormedSSHModuleDescriptor = new ModuleDescriptor();
		List<ModuleProperty> moduleProperties = new ArrayList<ModuleProperty>();
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT, SSHTransport.SSH));
		badFormedSSHModuleDescriptor.setModuleProperties(moduleProperties);
		
		wellFormedSSHModuleDescriptor = new ModuleDescriptor();
		moduleProperties = new ArrayList<ModuleProperty>();
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT, SSHTransport.SSH));
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT_HOST, "194.68.13.29"));
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT_PORT, "22"));
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT_USERNAME, "i2CAT"));
		moduleProperties.add(createModuleProperty(ITransportConstants.TRANSPORT_PASSWORD, "Yct4KgYR4F3fdhRV"));
		wellFormedSSHModuleDescriptor.setModuleProperties(moduleProperties);

	}
	
	private ModuleProperty createModuleProperty(String name, String value){
		ModuleProperty moduleProperty = new ModuleProperty();
		moduleProperty.setName(name);
		moduleProperty.setValue(value);
		return moduleProperty;
	}
	
	@Test
	public void testBadFormedModuleDescriptor(){
		try{
			transportFactory.createTransportInstance(badFormedModuleDescriptor);
		}catch(TransportException ex){
			ex.printStackTrace();
			Assert.assertTrue(true);
			return;
		}
		
		Assert.assertTrue(false);
	}

	
	
	@Test
	public void testWellFormedTelnetModuleDescriptor(){
		ITransport transport = null;
		try{
			transport = transportFactory.createTransportInstance(wellFormedSSHModuleDescriptor);
		}catch(TransportException ex){
			ex.printStackTrace();
			Assert.assertTrue(false);
			return;
		}
		
		Assert.assertNotNull(transport);
	}
	
	
	
}
