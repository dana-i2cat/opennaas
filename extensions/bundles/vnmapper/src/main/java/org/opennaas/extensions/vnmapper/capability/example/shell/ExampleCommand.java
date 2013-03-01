package org.opennaas.extensions.vnmapper.capability.example.shell;

import org.opennaas.core.resources.capability.ICapability;
import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.ResourceManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;


import org.opennaas.extensions.vnmapper.capability.example.*;

import org.opennaas.core.resources.ResourceManager;
import org.opennaas.extensions.network.model.topology.Link;
import org.opennaas.extensions.network.model.topology.Device;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.NetworkModel;
import java.util.List;
import java.io.*;
import java.util.*;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
@Command(scope = "example", name = "sayHello", description = "It will say hello.")
public class ExampleCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;

	@Argument(index = 1, name = "userName", description = "The name of the person we will greet.", required = true, multiValued = false)
	private String	username;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("sayHello");
		try {

			

                /////////////
                   ResourceManager resourcemng=(ResourceManager)getResourceManager();
                   List<IResource> res1=resourcemng.listResourcesByType("network");
                    List<Link> links=NetworkModelHelper.getLinks((NetworkModel)res1.get(0).getModel());
                      // System.out.println(" network links num : " + links.size());
                      // for(int i=0;i<links.size();i++)
                      //   {
                      //      System.out.println(" link : " + links.get(i).getSource().getName());
                      //      System.out.println(" link : " + links.get(i).getSource().getDevice().getName());
                      //      
                     //    }

                     List<Device> devices=NetworkModelHelper.getDevices((NetworkModel)res1.get(0).getModel());
                    //   System.out.println(" devices num : " + devices.size());
                       //for(int i=0;i<devices.size();i++)
                       //  {
                       //     System.out.println(" device : " + devices.get(i).getName());
                       //    
                      //   }
                 
                ///// generate the network object           
                InPNetwork net=new InPNetwork(); 
               
                    for (int i = 0; i < devices.size(); i++) {
                            PNode n=new PNode();                           
                            n.id=i; 
                            n.pnodeID=devices.get(i).getName();
                            n.capacity=16;
                            n.pathNum=0;
                            net.nodes.add(n);   
                            net.nodeNum++;
                    }
               
              
                for (int i = 0; i < net.nodeNum; i++) {
                    net.connections.add(new ArrayList<PLink>());  
    	          for (int j = 0; j < net.nodeNum; j++) {
                        net.connections.get(i).add(new PLink()); 
    	           }
                }
                
  
                for(int i=0;i<links.size();i++)
                         {
                           
                            int node1=-1;
                            
                            int node2=-1;
                            for (int j = 0; j < devices.size()&&(node1==-1||node2==-1); j++) {
                               if(devices.get(j).getName().equals(links.get(i).getSource().getDevice().getName())) 
                                  node1=j;
                               if(devices.get(j).getName().equals(links.get(i).getSink().getDevice().getName()))
                                  node2=j;
                             }
                            
                            if((node1!=-1) && (node2!=-1))
                            {                         
                              net.connections.get(node1).get(node2).id=1;
                            
                              net.connections.get(node1).get(node2).node1Id=node1;
                            
                              net.connections.get(node1).get(node2).node2Id=node2;
                            
                              net.connections.get(node1).get(node2).capacity=100;                            
                            
                              net.connections.get(node1).get(node2).delay=1;
                                                 
                              net.links.add(net.connections.get(node1).get(node2));      
                             }else

                            System.out.println("Error: couldn't find end point of a link");
                         }

               net.printNetwork();
               
                /////////////


                        IResource resource = getResourceFromFriendlyName(resourceName);
			ExampleCapability capab = (ExampleCapability) resource.getCapabilityByType("example");
			String greeting = capab.sayHello(username,net);
			printInfo(resourceName + " says : " + greeting);


                      

		} catch (Exception e) {
			printError("Error greeting from resource " + resourceName);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}
}
