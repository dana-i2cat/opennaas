package org.opennaas.extensions.ofrouting.capability.routing;

import com.google.common.net.InetAddresses;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
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
     * @throws CapabilityException if desired queueManagerService could not be
     * retrieved.
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
        log.info("Start time... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));
        try {
            httpRequest(Url, json[0]);
            midTime = System.currentTimeMillis() - midTime;
            log.info("Time Response1... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));

            httpRequest(Url, json[1]);
            midTime = System.currentTimeMillis() - midTime;
            log.error("GTime Response2... " + midTime + " Initial: " + (System.currentTimeMillis() - initialTime));
        } catch (Exception e) {
        }
        for (int i = 2; i < 4; i++) {
            new MyThread(i, Url, json).start();
        }

        long totalTime = System.currentTimeMillis() - initialTime;
        log.info("Return response, end exec: " + totalTime);
        return response;
    }

    private void httpRequest(String Url, String json) {
        try {
            log.info("Request to: " + Url);
            URL url = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(json);
            wr.flush();
            wr.close();
            conn.connect();
            conn.getResponseCode();
        } catch (UnknownHostException e) {
            log.error("Url is null. Maybe the controllers are not registred.");
        } catch (Exception ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getSubPath(String ipSource, String ipDest, String switchMac, int inputPort, boolean proactive) throws CapabilityException {
        long initialTime = System.currentTimeMillis();
        int outPortSW1 = 0;
        int version = 4;//IP version
        if (Utils.isIPv4Address(ipSource)) {
            ipSource = Utils.fromIPv4Address(Integer.parseInt(ipSource));
            ipDest = Utils.fromIPv4Address(Integer.parseInt(ipDest));
        } else {
            ipSource = Utils.tryToCompressIPv6(ipSource);
            ipDest = Utils.tryToCompressIPv6(ipDest);
            version = 6;
        }

        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
            return "null";
        }
        log.info("Path: " + ipSource + " " + ipDest + " " + switchMac + " " + inputPort);
        Switch switchInfo = new Switch(inputPort, switchMac);

        RouteSubnet route = null;
        String subSource = "";
        String subDest = "";
        try {
            for (RouteSubnet subnet : model.getTable().getRouteSubnet()) {
                log.info(subnet.getSourceSubnet().getIpAddressString());
                if (matches(subnet.getSourceSubnet(), InetAddress.getByName(ipSource))) {
                    for (RouteSubnet subnet1 : model.getTable().getRouteSubnet()) {
                        if (matches(subnet1.getDestSubnet(), InetAddress.getByName(ipDest))) {
                            route = new RouteSubnet(subnet.getSourceSubnet(), subnet1.getDestSubnet(), switchInfo);
                            subDest = InetAddresses.toAddrString(subnet1.getDestSubnet().getIpAddress());
                            subSource = InetAddresses.toAddrString(subnet.getSourceSubnet().getIpAddress());
//                            log.error(subnet.getSourceSubnet().getIpAddressString());
//                            log.error(subnet1.getDestSubnet().getIpAddressString());
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        log.info("Subnets: " + subSource + " " + subDest);
        Route srcRoute = null;
        try {
            srcRoute = new Route(InetAddress.getByName(subSource), InetAddress.getByName(ipDest), switchInfo);
        } catch (UnknownHostException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model.getTable().RouteExistsSubnet(route)) {
            outPortSW1 = model.getTable().getOutputPort(route);
        } else if (model.getTable().RouteExists(srcRoute, version)) {
            outPortSW1 = model.getTable().getOutputPort(srcRoute, version);
        } else {
            outPortSW1 = 0;
        }
        String returnedSrcSub = subSource;
        String returnedDstSub = subDest;
        //Next-hop router
        if (proactive) {
            log.info("Proactive Routing. Searching the last Switch of the Route...");
            String controllerInfo;
            Switch destSwInfo = null;
            try {
                destSwInfo = model.getTable().getDestinationSwitch(subSource, ipDest, switchMac, version);
                controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
            } catch (NullPointerException e) {
                log.error("Null Switch Info: " + e.getMessage());
                return null;
            }

            if (controllerInfo == null) {
                log.error("No information about the controller. Stop find route...");
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
            if (outputPortSW2 == null) {
                log.error("NO output port in the last Switch.");
            }
            log.info("Output port of the last Switch: " + outputPortSW2);

            subSource = subSource + "/24";
            subDest = subDest + "/24";

            final String Url = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
            String json[] = new String[4];
            String ethertype = "";
            String ethertype2 = "";
            if (version == 4) {
                ethertype = "0x806";
                ethertype2 = "0x800";
            } else if (version == 6) {
                ethertype = "0x86DD";
            }
            log.info("Origin Switch: " + switchMac+ ", Dest Switch: " + destSwInfo.getMacAddress());
            if (!destSwInfo.getMacAddress().equals(switchMac)) {
                json[0] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arpin-mod-" + ipDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"" + ethertype + "\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
                json[1] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"arpto-mod-" + subDest + subSource + "\", \"priority\":\"32767\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"" + ethertype + "\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
                json[2] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4in-mod-" + ipDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + ipDest + "\", \"ether-type\":\"" + ethertype2 + "\", \"ingress-port\":\"" + destSwInfo.getInputPort() + "\",\"active\":\"true\", \"actions\":\"output=" + outputPortSW2 + "\"}";
                json[3] = "{\"switch\": \"" + destSwInfo.getMacAddress() + "\", \"name\":\"ip4to-mod-" + subDest + subSource + "\", \"priority\":\"32767\", \"dst-ip\":\"" + subSource + "\", \"ether-type\":\"" + ethertype2 + "\", \"active\":\"true\", \"actions\":\"output=" + destSwInfo.getInputPort() + "\"}";
                try {
                    log.info("Inserting flows in the destination switch...");
                    for (int i = 0; i < json.length; i++) {
                        httpRequest(Url, json[i]);
                    }
                } catch (Exception e) {
                }
                /*              if(version == 4){
                 for (int i = 2; i < 4; i++) {
                 new MyThread(i, Url, json).start();
                 }
                 }
                 */            
            }

            //other switches
            log.info("Find routes in other Siwtches. Switches in the middle. Scenario 2 and 3");
            List<RouteSubnet> routeSubnetList = model.getTable().otherRouteExists(route, switchInfo, destSwInfo);
            if (routeSubnetList.size() > 1) {
                for (RouteSubnet r : routeSubnetList) {
                    destSwInfo = r.getSwitchInfo();
                    controllerInfo = model.getSwitchController().get(destSwInfo.getMacAddress());
                    subSource = r.getSourceSubnet().getIpAddressString() + "/24";
                    subDest = r.getDestSubnet().getIpAddressString() + "/24";
                    String Url2 = "http://" + controllerInfo + "/wm/staticflowentrypusher/json";
                    json[0] = "{\"switch\": \"" + r.getSwitchInfo().getMacAddress() + "\", \"name\":\"arpto-mod-" + subSource + subDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + r.getDestSubnet().getIpAddressString() + "/24\", \"ether-type\":\"" + ethertype + "\", \"active\":\"true\", \"actions\":\"output=" + r.getSwitchInfo().getOutputPort() + "\"}";
                    json[1] = "{\"switch\": \"" + r.getSwitchInfo().getMacAddress() + "\", \"name\":\"ip4to-mod-" + subSource + subDest + "\", \"priority\":\"32767\", \"dst-ip\":\"" + r.getDestSubnet().getIpAddressString() + "/24\", \"ether-type\":\"" + ethertype2 + "\", \"active\":\"true\", \"actions\":\"output=" + r.getSwitchInfo().getOutputPort() + "\"}";
                    try {
                        for (int i = 0; i < json.length; i++) {
                            httpRequest(Url2, json[i]);
                        }
                    } catch (Exception e) {
                    }
                    outputPortSW2 = Integer.toString(model.getTable().getOutputPort(route1, version));
                }
            }
        }

        long totalTime = System.currentTimeMillis() - initialTime;
        log.info("Return response, end exec time: " + totalTime);
        return Integer.toString(outPortSW1) + ":" + returnedSrcSub + ":" + returnedDstSub;
    }

    /*
     * Request to the controller page. If is offline, return the response.
     * Used only by the GUI.
     */
    @Override
    public String getControllerStatus(String ip) throws CapabilityException {
        ip = ip.replace("-", ".");
        ip = ip.replace("%3A", ":");
        log.info("Request Controller status witht he following IP: "+ip);
        try {
            String Url = "http://" + ip + "/wm/core/controller/switches/json";
            URLConnection connection = new URL(Url).openConnection();
            //connection.setRequestProperty("Accept-Charset", charset);
            connection.getInputStream();
            log.info("Status: Online");
            return "Online";
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
            log.info("Status: Offline");
            return "Offline";
        }
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
            log.info("try to send " + i);
            httpRequest(Url, json[i]);
            long midTime = System.currentTimeMillis() - initialTime;
            log.info("GetResponse" + i + "... " + midTime + "Initial: " + initialTime);

        }
    }

    /**
     * @param @return the greeting message
     *
     */
    @Override
    public String getRouteTable() throws CapabilityException {
        log.info("Get entire Route Table");
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
     * return the table requested. IPv4 -> 0, IPv6 -> 1, Subnet addresses -> 2
     *
     */
    @Override
    public String getRouteTable(int type) throws CapabilityException {
        log.info("Get entire Route Table of version "+type);
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }

        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (type == 0) {
                response = mapper.writeValueAsString(model.getTable().getRouteIPv4());
            } else if (type == 1) {
                response = mapper.writeValueAsString(model.getTable().getRouteIPv6());
            } else if (type == 2) {
                response = mapper.writeValueAsString(model.getTable().getRouteSubnet());
            }
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
        log.info("Put Route into table. Src: "+ipSource+" Dst: "+ipDest+" In: "+inputPort+" Out: "+outputPort);
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        if (model.getTable() == null) {
            model.setTable(new Table());
        }
        int version = 0;
        if (Utils.isIpAddress(ipSource) == 4 && Utils.isIpAddress(ipDest) == 4) {
            log.info("Is version 4");
            version = 4;
        } else if (Utils.isIpAddress(ipSource) == 6 && Utils.isIpAddress(ipDest) == 6) {
            log.info("Is version 6");
            version = 6;
        } else {
            log.error("IP version error. THe detected version is: " + version);
            return "The IP version is not detected. Analyze the IP.";
        }
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && inputPort != 0 && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchMac);
            Route route = null;
            try {
                route = new Route(InetAddress.getByName(ipSource), InetAddress.getByName(ipDest), switchInfo);
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
        if (!ipSource.isEmpty() && !ipDest.isEmpty() && !switchMac.isEmpty() && outputPort != 0) {
            Switch switchInfo = new Switch(Integer.toString(inputPort), inputPort, outputPort, switchMac);
            RouteSubnet route;
            String response = null;
            try {
                route = new RouteSubnet(new Subnet(InetAddress.getByName(ipSource), 24), new Subnet(InetAddress.getByName(ipDest), 24), switchInfo);
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
        OfRoutingModel model = (OfRoutingModel) resource.getModel();
        String response = "No content";
        ObjectMapper mapper = new ObjectMapper();
        try {
            response = mapper.writeValueAsString(model.getSwitchController());
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public String insertRouteFile(String filename) throws CapabilityException {
        try {
            log.info("Insert Routes from File");
            OfRoutingModel model = (OfRoutingModel) resource.getModel();
            if (model.getTable() == null) {
                model.setTable(new Table());
            }

            String response = "ok!";
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
                if (fieldName.equals("routeIPv4")) {
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
                            newRoute.setSourceAddress(InetAddress.getByName(node.get("srcAddr").getValueAsText()));
                            newRoute.setDestinationAddress(InetAddress.getByName(node.get("dstAddr").getValueAsText()));
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable().addRoute(newRoute, 4);
                        }
                    } else {
                        log.error("Error: records should be an array: skipping.");
                        response = "Error: records should be an array: skipping.";
                        jp.skipChildren();
                    }
                } else if (fieldName.equals("routeIPv6")) {
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
                            newRoute.setSourceAddress(InetAddress.getByName(node.get("srcAddr").getValueAsText()));
                            newRoute.setDestinationAddress(InetAddress.getByName(node.get("dstAddr").getValueAsText()));
                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRoute.setSwitchInfo(newSwitch);
                            model.getTable().addRoute(newRoute, 6);
                        }
                    } else {
                        log.error("Error: records should be an array: skipping.");
                        response = "Error: records should be an array: skipping.";
                        jp.skipChildren();
                    }
                } else if (fieldName.equals("routeSubnet")) {
                    if (current == JsonToken.START_ARRAY) {
                        // For each of the records in the array
                        while (jp.nextToken() != JsonToken.END_ARRAY) {
                            // read the record into a tree model,
                            // this moves the parsing position to the end of it
                            JsonNode node = jp.readValueAsTree();
                            String field = jp.getCurrentName();
                            // And now we have random access to everything in the object
                            RouteSubnet newRouteSubnet = new RouteSubnet();
                            Switch newSwitch = new Switch();
                            InetAddress srcAddr = InetAddress.getByName(node.get("srcSubnet").getPath("ip").getValueAsText());
                            int mask = Integer.parseInt(node.get("srcSubnet").getPath("mask").getValueAsText());
                            Subnet subnet = new Subnet(srcAddr, mask);
                            newRouteSubnet.setSourceSubnet(subnet);
                            srcAddr = InetAddress.getByName(node.get("dstSubnet").getPath("ip").getValueAsText());
                            mask = Integer.parseInt(node.get("dstSubnet").getPath("mask").getValueAsText());
                            subnet = new Subnet(srcAddr, mask);
                            newRouteSubnet.setDestSubnet(subnet);


                            newSwitch.setInputPort(Integer.parseInt(node.get("swInfo").getPath("inPort").getValueAsText()));
                            newSwitch.setOutputPort(Integer.parseInt(node.get("swInfo").getPath("outPort").getValueAsText()));
                            newSwitch.setMacAddress(node.get("swInfo").getPath("macAddr").getValueAsText());
                            newRouteSubnet.setSwitchInfo(newSwitch);
                            model.getTable().addRouteSub(newRouteSubnet);
                        }
                    } else {
                        log.error("Error: records should be an array: skipping.");
                        response = "Error: records should be an array: skipping.";
                        jp.skipChildren();
                    }
                } else {
                    log.error("Unprocessed property: " + fieldName);
                    response = "Unprocessed property: " + fieldName;
                    jp.skipChildren();
                }
            }
            return response;
        } catch (IOException ex) {
            Logger.getLogger(RoutingCapability.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Some error. Check it.";
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
