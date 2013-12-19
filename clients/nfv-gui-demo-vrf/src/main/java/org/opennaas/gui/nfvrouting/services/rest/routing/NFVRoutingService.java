package org.opennaas.gui.nfvrouting.services.rest.routing;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.api.representation.Form;
import java.net.ConnectException;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.GenericRestService;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.opennaas.gui.nfvrouting.utils.Constants;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class NFVRoutingService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingService.class);
    private static final String sdn = Constants.SDN_RESOURCE;
    private static final String user = "admin";
    private static final String password = "123456";

    /**
     * Call a rest service to get the Route Table of the virtualized router
     *
     * @param type of IP version
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String getRouteTable(int type) throws RestServiceException {
        ClientResponse response = null;

        try{
            LOGGER.info("Calling get Route Table service");
            String url = getURL("vrf/routing/routes/" + type);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(ClientResponse.class);
            LOGGER.info("Route table: " + response);
        }catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
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
            String url = getURL("vrf/routing/route");
            Form fm = new Form();
            fm.add("ipSource", route.getSourceAddress());
            fm.add("ipDest", route.getDestinationAddress());
            fm.add("switchDPID", route.getSwitchInfo().getMacAddress());
            fm.add("inputPort", route.getSwitchInfo().getInputPort());
            fm.add("outputPort", route.getSwitchInfo().getOutputPort());
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter(user, password));
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
            LOGGER.info("Remove route");
            String url = getURL("vrf/routing/route");
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter(user, password));
            WebResource webResource = client.resource(url);
            webResource.queryParam("id", Integer.toString(id)).queryParam("version", Integer.toString(version));
            response = webResource.delete(String.class);
            LOGGER.info("Removed route: " + response);
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
            client.addFilter(new HTTPBasicAuthFilter(user, password));
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
            client.addFilter(new HTTPBasicAuthFilter(user, password));
            WebResource webResource = client.resource(url);
            response = webResource.get(String.class);
            LOGGER.info("Controller status: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (com.sun.jersey.api.client.UniformInterfaceException e){
            LOGGER.error("Unauthorized");
            response = "s1";
        }
        return response;
    }

/*    public String getControllerStatus(String ip) {
        String response = null;
        String resourceName = "";
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("openflowswitch/" + resourceName + "/offorwarding/getOFForwardingRules");
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter(user, password));
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.APPLICATION_XML).get(String.class);
            LOGGER.info("Controller status: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;

    }
*/    
    //---------------------DEMO
    public String getLog() {
        String response;
        try {
            LOGGER.info("Get log of OpenNaaS");
            String url = getURL("vrf/routing/log");
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            return "OpenNaaS not started";
        }
        return response;
    }
}
