package org.opennaas.gui.nfvrouting.services.rest.routing;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.GenericRestService;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class NFVRoutingService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingService.class);

    /**
     * Call a rest service to get the Route Table of the virtualized router
     * 
     * @param type of IP version
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String getRouteTable(int type) throws RestServiceException {
        String response = null;
        
        try {
            LOGGER.info("Calling get Route Table service");
            String url = getURL("vrf/routing/routes/"+type);
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Route table: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
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
     * @param id
     * @param version
     * @return 
     */
        public String deleteRoute(int id, int version){
        String response = null;
        try {
            LOGGER.info("Remove route");
            LOGGER.error("Remove route");
            String url = getURL("vrf/routing/route");
            Client client = Client.create();
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
        
    public String getControllerStatus(String ip) {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("vrf/routing/getControllerStatus/"+ip);
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Controller status: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    public String getSwInfo(String dpid) {
        String response = null;
        try {
            LOGGER.info("Serivice: Get Information about the controller "+dpid);
            String url = getURL("vrf/routing/getSwInfo/"+dpid);
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }
    
    //---------------------DEMO
    
    public String getLog() {
        String response = null;
        try {
            LOGGER.info("Get log of OpenNaaS");
            String url = getURL("vrf/routing/log");
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Log....: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }
}
