package org.opennaas.gui.nfvrouting.services.rest.routing;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.vrf.utils.Utils;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.GenericRestService;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.opennaas.gui.nfvrouting.utils.Constants;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 * TODO: 
 * - remove SDN_Network
 * - remove or adapt getSwInfo(), requires switchName used in OpenNaaS
 * - obtain switchName before (in the controller/javascript????)
 * - extract from the NetTopology?
 */
public class NFVRoutingService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingService.class);
    private static final String sdn = Constants.SDN_RESOURCE;
    private static final String genericNetwork = Constants.GENERICNETWORK_RESOURCE;

    /**
     * Call a rest service to get the Route Table of the virtualized router
     *
     * @param type of IP version
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String getRouteTable(int type) throws RestServiceException {
        ClientResponse response;

        try {
            LOGGER.info("Calling get Route Table service");
            String url = getURL("vrf/staticrouting/routes/" + type);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
            LOGGER.info("Route table: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS is not started";
//            throw e;
        }
        return checkResponse(response) ? response.getEntity(String.class) : null;
    }

    /**
     * Call a rest service to insert a Route
     *
     * @param route
     * @return true if the environment has been created
     */
    public String insertRoute(Route route) {
        String response = null;
        try {
            LOGGER.info("Calling insert Route Table service");
            String url = getURL("vrf/staticrouting/route");
            Form fm = new Form();
            fm.add("ipSource", route.getSourceAddress());
            fm.add("ipDest", route.getDestinationAddress());
            fm.add("switchDPID", route.getSwitchInfo().getMacAddress());
            fm.add("inputPort", route.getSwitchInfo().getInputPort());
            fm.add("outputPort", route.getSwitchInfo().getOutputPort());
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).put(String.class, fm);
            LOGGER.info("Route table: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    /**
     * Remove Route given the id
     *
     * @param id
     * @param version
     * @return
     */
    public String deleteRoute(int id, int version) {
        String response = null;
        try {
            LOGGER.error("Remove route "+id+". Version IPv"+version+" "+Integer.toString(id));
            String url = getURL("vrf/staticrouting/route");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            webResource.queryParam("id", Integer.toString(id)).queryParam("version", Integer.toString(version)).delete();
            LOGGER.error("Removed route: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }
    
    /**
     * Remove Route given the id
     *
     * @return
     */
    public String deleteAllRoutes() {
        String response = null;
        try {
            LOGGER.error("Remove all route");
            String url = getURL("vrf/staticrouting/routes");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            webResource.accept(MediaType.TEXT_PLAIN).delete();
            LOGGER.error("Removed route: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    /**
     * Information about the switch.
     *
     * @param dpid
     * @return Flow table of the switch.
     */
    public String getSwInfo(String dpid) {
        String response = null;
        String resourceName = getSwitchResourceName(dpid);//request the resourceName
        try {
            LOGGER.info("Calling get Controller Status");
            LOGGER.error("Calling sw INFO");
            String url = getURL("openflowswitch/" + resourceName + "/offorwarding/getOFForwardingRules");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.APPLICATION_XML).get(String.class);
            LOGGER.info("Controller status: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    /**
     * Given the DPID of switch, return the Resource name stored in OpenNaaS
     *
     * @param dpid
     * @return
     */
    public String getSwitchResourceName(String dpid) {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("sdnnetwork/" + sdn + "/ofprovisionnet/getDeviceResourceName/" + dpid);
            LOGGER.error(url);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.get(String.class);
            LOGGER.info("Controller status: " + response);
            LOGGER.error("Resource ID: "+response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (com.sun.jersey.api.client.UniformInterfaceException e) {
            LOGGER.error("Unauthorized");
            response = "s1";
        }
        return response;
    }

    /**
     * Get specific route.
     * @param ipSrc
     * @param ipDst
     * @param dpid
     * @param inPort
     * @return 
     */
    public String getRoute(String ipSrc, String ipDst, String dpid, String inPort) {
        ClientResponse response;
        try {
            LOGGER.info("Get Route to OpenNaaS");
            String url = getURL("vrf/routing/route/" + Utils.StringIPv4toInt(ipSrc) + "/" + Utils.StringIPv4toInt(ipDst) + "/" + dpid + "/" + inPort);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
            LOGGER.info("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response.getEntity(String.class);
    }

    /**
     * Insert Route from javascript
     * 
     * @param ipSrc
     * @param ipDst
     * @param dpid
     * @param srcPort
     * @param dstPort
     * @return 
     */
    public String insertRoute(String ipSrc, String ipDst, String dpid, String srcPort, String dstPort) {
        String response = null;
        try {
            LOGGER.info("Calling insert Route Table service");
            String url = getURL("vrf/staticrouting/route");
            Form fm = new Form();
            fm.add("ipSource", ipSrc);
            fm.add("ipDest", ipDst);
            fm.add("switchDPID", dpid);
            fm.add("inputPort", srcPort);
            fm.add("outputPort", dstPort);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).put(String.class, fm);
            LOGGER.error("Inserted? : " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
//            throw e;
        }
        return response;
    }

    /**
     * Get route taken into account only the IP addresses
     * @param ipSrc
     * @param ipDst
     * @return 
     */
    public String getRoute(String ipSrc, String ipDst) {
        LOGGER.error("SERVICE GET ROUTE");
        ClientResponse response;
        try {
            String url = getURL("vrf/staticrouting/routes/4/" + Utils.StringIPv4toInt(ipSrc) + "/" + Utils.StringIPv4toInt(ipDst));
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
            LOGGER.error("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response.getEntity(String.class);
    }
    
    //---------------------DEMO -- to remove
    public String getLog() {
        String response;
        try {
            LOGGER.info("Get log of OpenNaaS");
            String url = getURL("vrf/staticrouting/log");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response;
    }
    public String getStream() {
        String response;
        try {
            LOGGER.info("Get stream info to OpenNaaS");
            String url = getURL("vrf/staticrouting/stream");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Stream....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response;
    }

    public String getONRouteMode() {
        String response;
        try {
            LOGGER.info("Get stream info to OpenNaaS");
            String url = getURL("vrf/routing/routeMode");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Stream....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response;
    }
    
    public String setONRouteMode(String mode) {
        String response;
        try {
            LOGGER.info("Get stream info to OpenNaaS");
            String url = getURL("vrf/routing/routeMode/"+mode);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Stream....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response;
    }
    
    /**
     * Obtain OpenNaaS generic network
     * @return
     * @throws RestServiceException 
     */
    public Topology getTopology() throws RestServiceException{
        ClientResponse response;
        try {
            LOGGER.info("Calling get Topology");
            String url = getURL("genericnetwork/"+genericNetwork+"/nettopology/topology");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
            LOGGER.error("Topology: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return checkResponse(response) ? response.getEntity(Topology.class) : null;
    }
}
