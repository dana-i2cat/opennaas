package org.opennaas.extensions.vnmapper.capability.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

import org.opennaas.core.resources.capability.ICapability;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.NetworkModel;
import java.util.*;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class ExampleCapability extends AbstractCapability implements IExampleCapability {

	public static String	CAPABILITY_TYPE	= "example";

	Log						log				= LogFactory.getLog(ExampleCapability.class);

	private String			resourceId		= "";

	public ExampleCapability(CapabilityDescriptor descriptor, String resourceId) {

		super(descriptor);
		this.resourceId = resourceId;
		log.debug("Built new Example Capability");
	}

	@Override
	public String getCapabilityName() {
		return CAPABILITY_TYPE;
	}

	@Override
	public IActionSet getActionSet() throws CapabilityException {

		String name = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_NAME);
		String version = this.descriptor.getPropertyValue(ResourceDescriptorConstants.ACTION_VERSION);

		try {
			return Activator.getExampleActionSetService(name, version);
		} catch (ActivatorException e) {
			throw new CapabilityException(e);
		}
	}

	@Override
	public void queueAction(IAction action) throws CapabilityException {
		getQueueManager(resourceId).queueAction(action);
	}

	/**
	 * 
	 * @return QueuemanagerService this capability is associated to.
	 * @throws CapabilityException
	 *             if desired queueManagerService could not be retrieved.
	 */
	private IQueueManagerCapability getQueueManager(String resourceId) throws CapabilityException {
		try {
			return Activator.getQueueManagerService(resourceId);
		} catch (ActivatorException e) {
			throw new CapabilityException("Failed to get QueueManagerService for resource " + resourceId, e);
		}
	}

	/**
	 * @param the
	 *            user name
	 * @return the greeting message
	 * 
	 */
	@Override
	public String sayHello(String VNRequest,InPNetwork net) throws CapabilityException {

                

                ////// run the matching and mapping/////
                //Global.rowNum=8;
                //Global.cellNum=8;
                Global.pathChoice=1;
                Global.maxPathLinksNum=5;
                //Global.staticNet=1;
                //Global.staticVNT=1;
                Global.stepsMax=100;
                ////
                //InPNetwork net=new InPNetwork();    
                //net=net.readPNetworkFromXMLFile("src\\marketplace\\network.xml");
                //net.printNetwork();
                ////
                VNTRequest vnt=new VNTRequest();
                vnt=vnt.readVNTRequestFromXMLFile(VNRequest);
                vnt.printVNTNetwork();
                
                try {
            
        
                    VNTMapper mapper=new VNTMapper();
                    MappingResult mres=new MappingResult();
                    ArrayList<ArrayList > matchingResult=new ArrayList<ArrayList >();
                    int matchingRes = mapper.matchVirtualNetwork(vnt, net, matchingResult); 
                    if(matchingRes==1)
                      {
                        System.out.println("successful Matching");
                        InPNetwork temp = (InPNetwork)ObjectCopier.deepCopy(net);
                        int result=mapper.VNTMapping(vnt, temp, 0, matchingResult, 3, mres); 
                        if(result==1)
                         {
                           System.out.println("successful Mapping");
                           mres.print();
                         }
                    
                     }
                }catch(Exception e){System.out.println("Exception");}
                

		return "Hello " + VNRequest;
	}

}
