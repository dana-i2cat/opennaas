package org.opennaas.extensions.ofrouting.capability.routing;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.codehaus.jackson.map.ObjectMapper;
import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.action.IAction;
import org.opennaas.core.resources.action.IActionSet;
import org.opennaas.core.resources.capability.AbstractCapability;
import org.opennaas.core.resources.capability.CapabilityException;
import org.opennaas.core.resources.descriptor.CapabilityDescriptor;
import org.opennaas.core.resources.descriptor.ResourceDescriptorConstants;
import org.opennaas.extensions.ofrouting.model.OfRoutingModel;
import org.opennaas.extensions.ofrouting.model.Route;
import org.opennaas.extensions.ofrouting.model.Switch;
import org.opennaas.extensions.ofrouting.model.Table;
import org.opennaas.extensions.ofrouting.utils.Utils;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

/**
 * 
 * @author Elisabeth Rigol
 * 
 */
public class RoutingCapability extends AbstractCapability implements IRoutingCapability {

    public static String CAPABILITY_TYPE = "routing";
    Log log = LogFactory.getLog(RoutingCapability.class);
    private String resourceId = "";

    public RoutingCapability(CapabilityDescriptor descriptor, String resourceId) {

        super(descriptor);
        this.resourceId = resourceId;
        log.debug("Built new Routing Capability");
    }

    @Override
    public void activate() throws CapabilityException {
        registerService(Activator.getContext(), CAPABILITY_TYPE, getResourceType(), getResourceName(), IRoutingCapability.class.getName());
        super.activate();
    }

    @Override
    public void deactivate() throws CapabilityException {
        unregisterService();
        super.deactivate();
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
            return Activator.getRoutingActionSetService(name, version);
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
     * @param the ipSource / ipDest / SwitchInfo
     * @return the greeting message
     * 
     */
    @Override
    public String getPath(String ipSource, String ipDest, String switchMac, String inputPort) throws CapabilityException {
        String response = "";
        ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
        ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));

        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
            return "null";
        }
        log.error("Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchMac);
        Route route = new Route(ipSource, ipDest, switchInfo);
        if (model.getTable().RouteExists(route)) {
            log.error("Get OUTPUT PORT");
            model.getTable().addRegister("Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort + " " + new java.util.Date().getHours() + ":" + new java.util.Date().getMinutes() + ":" + new java.util.Date().getSeconds());
            response = model.getTable().getOutputPort(route);
        }

        //Next-hop router
        String controllerInfo = "";
        Switch destSwInfo = null;
        try{
        destSwInfo = model.getTable().getDestinationSwitch(ipSource, ipDest, switchMac);
            controllerInfo= model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e){
            
        }
        String ControllerIP = "opennaasNFV";
        String ControllerPort = "8080";
        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
                /*"http://" + ControllerIP + ":" + ControllerPort + "/wm/staticflowentrypusher/json";*/
        String json[] = new String[4];
json[0] = "{\"switch\": \""+destSwInfo.getMacAddress()+"\", \"name\":\"flow-mod-"+(int)(Math.random() * ((1000 - 1) + 1))+"\", \"priority\":\"32767\", \"src-ip\":\""+ipSource+"\", \"dst-ip\":\""+ipDest+"\", \"ether-type\":\"0x800\", \"ingress-port\":\""+destSwInfo.getInputPort()+"\",\"active\":\"true\", \"actions\":\"output="+destSwInfo.getOutputPort()+"\"}";
json[1] = "{\"switch\": \""+destSwInfo.getMacAddress()+"\", \"name\":\"flow-mod-"+(int)(Math.random() * ((1000 - 1) + 1))+"\", \"priority\":\"32767\", \"src-ip\":\""+ipDest+"\", \"dst-ip\":\""+ipSource+"\", \"ether-type\":\"0x800\", \"ingress-port\":\""+destSwInfo.getOutputPort()+"\",\"active\":\"true\", \"actions\":\"output="+destSwInfo.getInputPort()+"\"}";
json[2] = "{\"switch\": \""+destSwInfo.getMacAddress()+"\", \"name\":\"flow-mod-"+(int)(Math.random() * ((1000 - 1) + 1))+"\", \"priority\":\"32767\", \"src-ip\":\""+ipSource+"\", \"dst-ip\":\""+ipDest+"\", \"ether-type\":\"0x806\", \"ingress-port\":\""+destSwInfo.getInputPort()+"\",\"active\":\"true\", \"actions\":\"output="+destSwInfo.getOutputPort()+"\"}";
json[3] = "{\"switch\": \""+destSwInfo.getMacAddress()+"\", \"name\":\"flow-mod-"+(int)(Math.random() * ((1000 - 1) + 1))+"\", \"priority\":\"32767\", \"src-ip\":\""+ipDest+"\", \"dst-ip\":\""+ipSource+"\", \"ether-type\":\"0x806\", \"ingress-port\":\""+destSwInfo.getOutputPort()+"\",\"active\":\"true\", \"actions\":\"output="+destSwInfo.getInputPort()+"\"}";



for ( int i = 0; i<4; i++ ){
    new MyThread(i, Url, json).start();
}

log.error("Return response");
        return response;
    }

public class MyThread extends Thread {

    private int i;
    private String Url;
    private String[] json;
    
   public MyThread(int i, String Url, String[] json) {
       this.i = i;
       this.Url = Url;
       this.json = json;
   }

   public void run() {
       try {
                        log.error("try to send "+i);
                        URL url = new URL(Url);
                        URLConnection conn = url.openConnection();
                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                        wr = new OutputStreamWriter(conn.getOutputStream());
                        wr.write(json[i]);
                        log.error("write");
                        wr.flush();

                        // Get the response 
/*                        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String line;
                        while ((line = rd.readLine()) != null) {
                            System.out.println(line);
                        }
*/                        
                        wr.close();
//                        rd.close();
                    } catch (IOException ex) {
                        Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
                    }
   }
}

    /**
     * @param
     * @return the greeting message
     * 
     */
    @Override
    public String getRouteTable() throws CapabilityException {
        log.info("Get Route Table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }

        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getTable());
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    /**
     * @param the ipSource / ipDest / SwitchInfo
     * @return the greeting message
     * 
     */
    @Override
    public String putRoute(String ipSource, String ipDest, String switchMac, String inputPort, String outputPort) throws CapabilityException {
        log.info("Put Route into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }

        Switch switchInfo = new Switch(inputPort, inputPort, outputPort, switchMac);
        Route route = new Route(model.getTable().getRoute().size(), ipSource, ipDest, switchInfo);
        String response = model.getTable().addRoute(route);
        return response;
    }

    @Override
    public String getRegister() throws CapabilityException {
        return ((OfRoutingModel) resource.getModel()).getTable().getRegister().toString();
    }
    
    @Override
    public Response putSwitchController(String ipController, String portController, String switchMac) throws CapabilityException {
        log.info("Put Switch-Controller info into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }
        model.getSwitchController().put(switchMac, ipController+":"+portController);
        return Response.ok().build();
    }
}
