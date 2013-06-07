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
            return model.getTable().getOutputPort(route);
        }

        
        
        
        String ControllerIP = "opennaasNFV";
        String ControllerPort = "8080";
        String Url = "http://" + ControllerIP + ":" + ControllerPort + "/wm/staticflowentrypusher/json";
        String json = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-3\", \"priority\":\"32767\", \"ingress-port\":\"1\",\"active\":\"true\", \"actions\":\"output=2\"}";
         
        
        
       try{ 
           log.error("try to send");
       URL url = new URL(Url); 
            URLConnection conn = url.openConnection(); 
            conn.setDoOutput(true); 
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 

            wr = new OutputStreamWriter(conn.getOutputStream()); 
            json = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-2\", \"priority\":\"32767\", \"src-ip\":\"192.168.1.2\", \"dst-ip\":\"192.168.2.3\", \"ether-type\":\"0x800\", \"ingress-port\":\"2\",\"active\":\"true\", \"actions\":\"output=1\"}";
            wr.write(json);
            log.error("write");
            wr.flush(); 
            
            // Get the response 
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
            String line; 
            while ((line = rd.readLine()) != null) { 
                System.out.println(line); 
            } 
            wr.close();
            rd.close(); 
            
            
            conn = url.openConnection(); 
            conn.setDoOutput(true); 
            wr = new OutputStreamWriter(conn.getOutputStream()); 
            json = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-2\", \"priority\":\"32767\", \"src-ip\":\"192.168.1.2\", \"dst-ip\":\"192.168.2.3\", \"ether-type\":\"0x800\", \"ingress-port\":\"2\",\"active\":\"true\", \"actions\":\"output=1\"}";
            wr.write(json);
            log.error("write");
            wr.flush(); 
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream())); 
            line = ""; 
            while ((line = rd.readLine()) != null) { 
                System.out.println(line); 
            } 
wr.close();
            rd.close();
            
       }catch(IOException e){
           log.error("Error "+e.getMessage());
       }
        
        
        if(switchMac.equals("00:00:00:00:00:00:00:01")){
            //inform SW2
            
            json = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-1\", \"priority\":\"32767\", \"src-ip\":\"192.168.2.3\", \"dst-ip\":\"192.168.1.2\", \"ingress-port\":\"2\",\"active\":\"true\", \"actions\":\"output=1\"}";
//            response = client.post(json, String.class);
//log.error("1-JSON; "+response);            
            json = "{\"switch\": \"00:00:00:00:00:00:00:02\", \"name\":\"flow-mod-1\", \"priority\":\"32767\", \"src-ip\":\"192.168.1.2\", \"dst-ip\":\"192.168.2.3\", \"ingress-port\":\"2\",\"active\":\"true\", \"actions\":\"output=1\"}";
//            response = client.post(json, String.class);
//log.error("1-JSON; "+response);            
            json = "{\"switch\": \"00:00:00:00:00:00:00:02\", \"name\":\"flow-mod-2\", \"priority\":\"32767\", \"src-ip\":\"192.168.2.3\", \"dst-ip\":\"192.168.1.2\", \"ingress-port\":\"1\",\"active\":\"true\", \"actions\":\"output=2\"}";
//            response = client.post(json, String.class);
//log.error("1-JSON; "+response);            
        }
        if(switchMac.equals("00:00:00:00:00:00:00:02")){
            
        }
//        json = "{\"switch\": \"00:00:00:00:00:00:00:02\", \"name\":\"flow-mod-1\", \"priority\":\"32767\", \"src-ip\":\"192.168.1.2\", \"dst-ip\":\"192.168.2.3\", \"ingress-port\":\"2\",\"active\":\"true\", \"actions\":\"output=1\"}";
//        json = "{\"switch\": \"00:00:00:00:00:00:00:01\", \"name\":\"flow-mod-1\", \"priority\":\"32767\", \"ingress-port\":\"1\",\"active\":\"true\", \"actions\":\"output=2\"}";
//        String json = "";
        
//response = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).post(String.class, json);
//log.error(response);

        return "null";
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
        Route route = new Route(ipSource, ipDest, switchInfo);
        String response = model.getTable().addRoute(route);
        return response;
    }

    @Override
    public String getRegister() throws CapabilityException {
        return ((OfRoutingModel) resource.getModel()).getTable().getRegister().toString();
    }
}
