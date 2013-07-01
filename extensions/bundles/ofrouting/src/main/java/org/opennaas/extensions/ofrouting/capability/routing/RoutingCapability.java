package org.opennaas.extensions.ofrouting.capability.routing;

import com.google.common.net.InetAddresses;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;

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
    public int getPath(String ipSource, String ipDest, String switchMac, int inputPort) throws CapabilityException {
        long initialTime = System.currentTimeMillis();
        log.error("Start time... " + initialTime);
        int response = 0;
        int version = 4;
        ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
        ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));

        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
            return 0;
        }
        log.info("Get Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchMac);
        Route route = null;
        try {
            route = new Route(InetAddress.getByName(ipSource), InetAddress.getByName(ipDest), switchInfo);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model.getTable().RouteExists(route, version)) {
            response = model.getTable().getOutputPort(route, version);
        }

        //Next-hop router
        String controllerInfo = "";
        Switch destSwInfo = null;
        try {
            destSwInfo = model.getTable().getDestinationSwitch(ipSource, ipDest, switchMac, version);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e) {
            log.error("Null Switch Info");
            return 0;
        }

        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
        String json[] = new String[4];

        json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getOutputPort() + "\"}";
        json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getOutputPort() + "\"}";
        json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipDest + "\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"0x806\", \"ingress-port\":\"" + destSwInfo.getOutputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
        json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"flow-mod-" + (int) (Math.random() * ((1000 - 1) + 1)) + "\", \"priority\":\"32767\", \"src-ip\":\"" + ipDest + "\", \"dst-ip\":\"" + ipSource + "\", \"ether-type\":\"0x800\", \"ingress-port\":\"" + destSwInfo.getOutputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";

        long midTime = System.currentTimeMillis() - (System.currentTimeMillis() - initialTime);
        log.error("Start time... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));
        try {
            httpRequest(Url, json[0]);
            midTime = System.currentTimeMillis() - midTime;
            log.error("GetResponse1... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));

            httpRequest(Url, json[1]);
            midTime = System.currentTimeMillis() - midTime;
            log.error("GetResponse2... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));
        } catch (Exception e) {
        }
        for (int i = 2; i < 4; i++) {
            new MyThread(i, Url, json).start();
        }

        long totalTime = System.currentTimeMillis() - initialTime;
        log.error("Return response, end exec: " + totalTime);
        return response;
    }

    private void httpRequest(String Url, String json) {
        try {
            log.error("URl "+Url);
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json);
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
        }catch (UnknownHostException e){
            log.error("Url is null. Maybe the controllers are not registred.");
        } catch (Exception ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public String getSubPath(String ipSource, String ipDest, String switchMac, int inputPort, boolean proactive) throws CapabilityException {
        long initialTime = System.currentTimeMillis();
        log.error("Start time... " + initialTime);
        String response = "";
        int version = 4;//IP version
	if(Utils.isIPv4Address(ipSource)){
	    ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
	    ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));
	}
        else{
            ipSource = Utils.tryToCompressIPv6(ipSource);
	    ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        }

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
            for (RouteSubnet subnet : model.getTable().getRouteSubnet()) {
		log.error(subnet.getSourceSubnet().getIpAddressString());
                if (matches(subnet.getSourceSubnet(), InetAddress.getByName(ipSource))) {
			log.error("entra");
                    for (RouteSubnet subnet1 : model.getTable().getRouteSubnet()) {
                        log.error("tmb entra");
                        if (matches(subnet1.getDestSubnet(), InetAddress.getByName(ipDest))) {
                            route = new RouteSubnet(subnet.getSourceSubnet(), subnet1.getDestSubnet(), switchInfo);
                            subDest = InetAddresses.toAddrString(subnet1.getDestSubnet().getIpAddress());
                            subSource = InetAddresses.toAddrString(subnet.getSourceSubnet().getIpAddress());
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.error("Subnets: " + subSource + " " + subDest);


        if (model.getTable().RouteExistsSubnet(route)) {
            response = Integer.toString(model.getTable().getOutputPort(route));
        }

        //Next-hop router
        String controllerInfo = "";
        Switch destSwInfo = null;
        try {
            destSwInfo = model.getTable().getDestinationSwitch(subSource, ipDest, switchMac, version);
            controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
        } catch (NullPointerException e) {
            log.error("Null Switch Info: "+e.getMessage());
            return null;
        }
        
        if(controllerInfo == null){
            log.error("No information about the controller");
            return null;
        }

        Route route1 = null;
        try {
            route1 = new Route(InetAddress.getByName(subSource), InetAddress.getByName(ipDest), destSwInfo);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        String outputPortSW2 = "";
        if (model.getTable().RouteExists(route1, version)) {
            outputPortSW2 = Integer.toString(model.getTable().getOutputPort(route1, version));
        }
        log.error("Out "+outputPortSW2);
        if(outputPortSW2 == null){
            log.error("NO output port");
        }

        String returnedSrcSub = subSource;
        String returnedDstSub = subDest;
        subSource = subSource + "/24";
        subDest = subDest + "/24";

        final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
        String json[] = new String[4];
        String ethertype = "";
        String ethertype2 = "";
        if(version == 4){
            ethertype = "0x806";
            ethertype2 = "0x800";
        }else if(version == 6){
            ethertype = "0x86DD";
        }
        
        if (!destSwInfo.getMacAddress().equals(switchMac) && proactive) {         
            json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arp-mod-" + subSource + ipDest + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\""+ethertype+"\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
            json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arp-mod-" + subDest + subSource + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\""+ethertype+"\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
            json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4-mod-" + subSource + ipDest + "\", \"priority\":\"32767\", \"src-ip\":\"" + subSource + "\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\""+ethertype2+"\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
            json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4-mod-" + subDest + subSource + "\", \"priority\":\"32767\", \"src-ip\":\"" + subDest + "\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\""+ethertype2+"\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
            try {
                httpRequest(Url, json[0]);
                log.error("write1: "+json[0]);
                httpRequest(Url, json[1]);
                log.error("write2: "+json[1]);
            } catch (Exception e) {
            }
            if(version == 4){
                for (int i = 2; i < 4; i++) {
                    new MyThread(i, Url, json).start();
                }
            }
        } else {
            response = outputPortSW2;
        }
        

        long totalTime = System.currentTimeMillis() - initialTime;
        log.error("Return response, end exec: " + totalTime);
        return response + ":" + returnedSrcSub + ":" + returnedDstSub;
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
            long initialTime = System.currentTimeMillis();
            log.error("try to send " + i);
            httpRequest(Url, json[i]);
            long midTime = System.currentTimeMillis() - initialTime;
            log.error("GetResponse" + i + "... " + midTime + "Initial: " + initialTime);

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
    public String putRoute(String ipSource, String ipDest, String switchMac, int inputPort, int outputPort) throws CapabilityException {
        log.info("Put Route into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }
        int version = 0;
        if(Utils.isIpAddress(ipSource) == 4 && Utils.isIpAddress(ipDest) == 4){
            log.error("Is version 4");
            version = 4;
        }else if(Utils.isIpAddress(ipSource)  == 6 && Utils.isIpAddress(ipDest)  == 6){
            log.error("Is version 6");
            version = 6;
        }else{
            log.error("Is version "+version);
            return "The IP version is not detected. Analyze the IP.";
        }
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && inputPort !=0 && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchMac);
            Route route = null;
            try {
                route = new Route(model.getTable().getRoute(version).size(), InetAddress.getByName(ipSource), InetAddress.getByName(ipDest), switchInfo);
            } catch (UnknownHostException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }
            String response = model.getTable().addRoute(route, version);
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
    public String putSubRoute(String ipSource, String ipDest, String switchMac, int inputPort, int outputPort) throws CapabilityException {
        log.info("Put Route into table");
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && inputPort !=0 && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchMac);
            RouteSubnet route;
            String response = null;
            try {
                route = new RouteSubnet(model.getTable().getRouteSubnet().size(), new Subnet(InetAddress.getByName(ipSource), 24), new Subnet(InetAddress.getByName(ipDest), 24), switchInfo);
                response = model.getTable().addRouteSub(route);
            } catch (UnknownHostException ex) {
                Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            }

            return response;
        }
        return "Some value is empty";
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

    @Override
    public String insertRouteFile(String filename) throws CapabilityException {
        try {
            log.info("Insert Routes from File");
            OfRoutingModel model = (OfRoutingModel) resource.getModel();
            if (model.getTable() == null) {
                model.setTable(new Table());
            }

            String response = "No content";
            
            JsonFactory f = new MappingJsonFactory();
            JsonParser jp = f.createJsonParser(new File(filename));
            JsonToken current = jp.nextToken();
            if (current != JsonToken.START_OBJECT) {
                log.error("Error: root should be object: quiting.");
            }

            while (jp.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jp.getCurrentName();
                // move from field name to field value
                current = jp.nextToken();
//                Table table = new Table();
                if (fieldName.equals("route")) {
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            // read the record into a tree model,
                            // this moves the parsing position to the end of it
                            JsonNode node = jp.readValueAsTree();
                            String field = jp.getCurrentName();
                            // And now we have random access to everything in the object
                            Route newRoute = new Route();
                            Switch newSwitch = new Switch();
                            newRoute.setSourceAddress(InetAddress.getByName(node.get("sourceAddress").getValueAsText()));
                            newRoute.setDestinationAddress(InetAddress.getByName(node.get("destinationAddress").getValueAsText()));
                            newSwitch.setInputPort(Integer.parseInt(node.get("switchInfo").getPath("inputPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("switchInfo").getPath("outputPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("switchInfo").getPath("macAddress").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable().addRoute(newRoute, 4);
                        }
                    } else {
                        log.error("Error: records should be an array: skipping.");
                        jp.skipChildren();
                    }
                } else {
                    log.error("Unprocessed property: " + fieldName);
                    jp.skipChildren();
                }
            }                
            return response;
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ok";
    }
    
    private boolean matches(Subnet subnet, InetAddress ipAddress) {
        int nMaskBits = subnet.getMask();
        int oddBits = nMaskBits % 8;
        int nMaskBytes = nMaskBits / 8 + (oddBits == 0 ? 0 : 1);
        byte[] mask = new byte[nMaskBytes];

        byte[] allowedIpAddress = subnet.getAddress();
        byte[] requestIpAddress = ipAddress.getAddress();

        // If IPs are not both IPv4 or IPv6, we can't compare
        if (allowedIpAddress.length != requestIpAddress.length) {
            return false;
        }
        Arrays.fill(mask, 0, oddBits == 0 ? mask.length : mask.length - 1, (byte) 0xFF);

        if (oddBits != 0) {
            int finalByte = (1 << oddBits) - 1;
            finalByte <<= 8 - oddBits;
            mask[mask.length - 1] = (byte) finalByte;
        }

        for (int i = 0; i < mask.length; i++) {
            if ((allowedIpAddress[i] & mask[i]) != (requestIpAddress[i] & mask[i])) {
                return false;
            }
        }

        return true;
    }
}
