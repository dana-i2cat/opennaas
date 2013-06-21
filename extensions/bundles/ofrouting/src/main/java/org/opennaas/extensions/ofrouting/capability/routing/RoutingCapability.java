package org.opennaas.extensions.ofrouting.capability.routing;

import java.io.BufferedReader;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
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
import org.opennaas.extensions.ofrouting.model.RouteSubnet;
import org.opennaas.extensions.ofrouting.model.Subnet;
import org.opennaas.extensions.ofrouting.model.Switch;
import org.opennaas.extensions.ofrouting.model.Table;
import org.opennaas.extensions.ofrouting.utils.Utils;
import org.opennaas.extensions.queuemanager.IQueueManagerCapability;

/**
 * 
 * @author Josep Batalle
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
        long initialTime = System.currentTimeMillis();
        log.error("Start time... " + initialTime);
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
        try {
            destSwInfo = model.getTable().getDestinationSwitch(ipSource, ipDest, switchMac);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e) {
        }
        String ControllerIP = "opennaasNFV";
        String ControllerPort = "8080";
        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
        /*"http://" + ControllerIP + ":" + ControllerPort + "/wm/staticflowentrypusher/json";*/
        String json[] = new String[4];

        json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getOutputPort() + "\"}";
        json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getOutputPort() + "\"}";
        json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipDest + "\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + destSwInfo.getOutputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
        json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipDest + "\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + destSwInfo.getOutputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
        log.error("Json0 " + json[0]);
long midTime = System.currentTimeMillis() - (System.currentTimeMillis() - initialTime);
log.error("Start time... " + midTime + " Initial: "+(System.currentTimeMillis() - initialTime));
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
midTime = System.currentTimeMillis() - midTime;
log.error("Before1 " + midTime + " Initial: "+(System.currentTimeMillis() - initialTime));
            wr.write(json[0]);
            log.error("write1");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
midTime = System.currentTimeMillis() - midTime;
log.error("GetResponse1... " + midTime + " Initial: "+(System.currentTimeMillis() - initialTime));
            url = new URL(Url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json[1]);
            log.error("write2");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
midTime = System.currentTimeMillis() - midTime;
log.error("GetResponse2... " + midTime + " Initial: "+(System.currentTimeMillis() - initialTime));
        } catch (Exception e) {
        }
        for (int i = 2; i < 4; i++) {
            new MyThread(i, Url, json).start();
        }

        long totalTime = System.currentTimeMillis() - initialTime;
        log.error("Return response, end exec: " + totalTime);
        return response;
    }
private boolean matches(Subnet subnet, InetAddress ipAddress) {

        int nMaskBits = subnet.getMask();
        int oddBits = nMaskBits % 8;
        int nMaskBytes = nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
        byte[] mask = new byte[nMaskBytes];

        byte[] allowedIpAddress = subnet.getAddress();
        byte[] requestIpAddress = ipAddress.getAddress();

        // If IPs are not both IPv4 or IPv6, we can't compare
        if (allowedIpAddress.length != requestIpAddress.length){
            return false;
        }

        Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

        if (oddBits != 0){
            int finalByte = (1 << oddBits) - 1;
            finalByte <<= 8 - oddBits;
            mask[mask.length - 1] = (byte) finalByte;
        }

        for (int i = 0; i < mask.length; i++){
            if ((allowedIpAddress[i] & mask[i]) != (requestIpAddress[i] & mask[i])){
                return false;
            }
        }

        return true;
    }
    @Override
    public String getSubPath(String ipSource, String ipDest, String switchMac, String inputPort) throws CapabilityException {
        long initialTime = System.currentTimeMillis();
        log.error("Start time... " + initialTime);
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
         
        
        RouteSubnet route = null;
        String subSource = "";
        String subDest = "";
        try {
            for (RouteSubnet subnet : model.getTable().getRouteSubnet()){
                if (matches(subnet.getSourceSubnet(), InetAddress.getByName(ipSource))){
                    for (RouteSubnet subnet1 : model.getTable().getRouteSubnet()){
                        log.error("t0");
                        if (matches(subnet1.getDestSubnet(), InetAddress.getByName(ipDest))){
                            route = new RouteSubnet(subnet.getSourceSubnet(), subnet1.getDestSubnet(), switchInfo);
                            subDest = subnet1.getDestSubnet().getIpAddressString();
                            subSource = subnet.getSourceSubnet().getIpAddressString();
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.error("Info: "+subSource+" "+subDest+" "+ipSource+" "+ipDest);
        
log.error("Subnetwork route");
        if (model.getTable().RouteExists(route)) {
            log.error("Get OUTPUT PORT");
            model.getTable().addRegister("Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort + " " + new java.util.Date().getHours() + ":" + new java.util.Date().getMinutes() + ":" + new java.util.Date().getSeconds());
            response = model.getTable().getOutputPort(route);
        }
        

        //Next-hop router
        String controllerInfo = "";
        String controllerInfo2 = "";
        Switch destSwInfo = null;
        try {
            destSwInfo = model.getTable().getDestinationSwitch(subSource, ipDest, switchMac);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
            controllerInfo2 = model.getSwitchController().get(switchMac);
        } catch (NullPointerException e) {
            log.error("Error "+e.getMessage());
        }
        
        Route route1 = new Route(subSource, ipDest, destSwInfo);
        String outputPortSW2 = "";
        if (model.getTable().RouteExists(route1)) {
            log.error("Get OUTPUT PORT");
            model.getTable().addRegister("Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort + " " + new java.util.Date().getHours() + ":" + new java.util.Date().getMinutes() + ":" + new java.util.Date().getSeconds());
            outputPortSW2 = model.getTable().getOutputPort(route1);
        }
        
        String returnedSrcSub = subSource;
        String returnedDstSub = subDest;
        subSource = subSource+"/24";
        subDest = subDest+"/24";
        String ControllerIP = "opennaasNFV";
        String ControllerPort = "8080";
        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
        final String Url2 = "http://" + controllerInfo2 + "/wm/staticflowentrypusher/json";
        /*"http://" + ControllerIP + ":" + ControllerPort + "/wm/staticflowentrypusher/json";*/
        String json[] = new String[4];
/*
        json[0] = "{\"switch\": \"" + switchMac + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + subDest + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + inputPort + "\",\"active\":\"true\", \"actions\":\"output=" + response + "\"}";
        json[1] = "{\"switch\": \"" + switchMac + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + subDest + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + inputPort + "\",\"active\":\"true\", \"actions\":\"output=" + response + "\"}";
        json[2] = "{\"switch\": \"" + switchMac + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + response + "\",\"active\":\"true\", \"actions\":\"output=" + inputPort + "\"}";
        json[3] = "{\"switch\": \"" + switchMac + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + response + "\",\"active\":\"true\", \"actions\":\"output=" + inputPort + "\"}";
*/        
        if(!destSwInfo.getMacAddress().equals( switchMac)){
        json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arp-mod-" + subSource+ipDest + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
        json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4-mod-" + subSource+ipDest + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
        json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arp-mod-" + subDest+subSource + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"0x806\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
        json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4-mod-" + subDest+subSource + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"0x800\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
        }
        else{
            response = outputPortSW2;
        }
        log.error("Json0 " + json[0]);
        try {
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json[0]);
            log.error("write1");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();

            url = new URL(Url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json[1]);
            log.error("write2");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
/*            
            url = new URL(Url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json[2]);
            log.error("write2");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
            
            url = new URL(Url2);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json[3]);
            log.error("write2");
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
*/
        } catch (Exception e) {
        }
        for (int i = 2; i < 4; i++) {
            new MyThread(i, Url, json).start();
        }

        long totalTime = System.currentTimeMillis() - initialTime;
        log.error("Return response, end exec: " + totalTime);
        return response+":"+returnedSrcSub+":"+returnedDstSub;
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

        @Override
        public void run() {
            try {
long initialTime = System.currentTimeMillis();
log.error("try to send " + i);
                URL url = new URL(Url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(json[i]);
                log.error("write " + i);
                wr.flush();

                // Get the response 
/*                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                System.out.println(line);
                }
                 */
                wr.close();
                conn.connect();
                conn.getResponseCode();
long midTime = System.currentTimeMillis() - initialTime;
log.error("GetResponse"+i+"... " + midTime + "Initial: "+initialTime);
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
if(!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && !inputPort.isEmpty() && !outputPort.isEmpty()){
        Switch switchInfo = new Switch(inputPort, inputPort, outputPort, switchMac);
        Route route = new Route(model.getTable().getRoute().size(), ipSource, ipDest, switchInfo);
        String response = model.getTable().addRoute(route);
        return response;
}
return "Some value is empty";
    }
    /**
     * @param the ipSource / ipDest / SwitchInfo
     * @return the greeting message
     * 
     */
    @Override
    public String putSubRoute(String ipSource, String ipDest, String switchMac, String inputPort, String outputPort) throws CapabilityException {
        log.info("Put Route into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }
if(!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && !inputPort.isEmpty() && !outputPort.isEmpty()){
        Switch switchInfo = new Switch(inputPort, inputPort, outputPort, switchMac);
        RouteSubnet route;
        String response = null;
            try {
                route = new RouteSubnet(model.getTable().getRoute().size(),new Subnet(InetAddress.getByName(ipSource), 24), new Subnet(InetAddress.getByName(ipDest), 24), switchInfo);
                response = model.getTable().addRouteSub(route);
            } catch (UnknownHostException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
        
        return response;
}
return "Some value is empty";
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
        model.getSwitchController().put(switchMac, ipController + ":" + portController);
        return Response.ok().build();
    }
    
    @Override
    public String getControllersInfo() throws CapabilityException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
