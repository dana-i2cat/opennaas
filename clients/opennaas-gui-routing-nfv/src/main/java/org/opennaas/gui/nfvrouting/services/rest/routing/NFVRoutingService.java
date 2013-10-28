package org.opennaas.gui.nfvrouting.services.rest.routing;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.entities.ControllerInfo;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.GenericRestService;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;

/**
 * @author Josep
 */
public class NFVRoutingService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingService.class);

    /**
     * Call a rest service to get the Route Table of the virtualized router
     * 
     * @param request
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String getRouteTable(String request, String type) throws RestServiceException {
        String response = null;
        int typ = 0;
        if (type.equals("IPv4"))
                typ = 0;
        else if (type.equals("IPv6"))
               typ = 1;
        else if (type.equals("subnet"))
                typ = 2;
        try {
            LOGGER.info("Calling get Route Table service");
            request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/getRouteTable/"+typ);
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
     * Call a rest service to get the Route Table of the virtualized router
     * 
     * @param request
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String insertRoute(Route route) {
        String response = null;
        try {
            LOGGER.info("Calling get Route Table service");
            String request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/putRoute");
            Form fm = new Form();
            fm.add("ipSource", route.getSourceAddress());
            fm.add("ipDest", route.getDestinationAddress());
            fm.add("switchMac", route.getSwitchInfo().getMacAddress());
            fm.add("inputPort", route.getSwitchInfo().getInputPort());
            fm.add("outputPort", route.getSwitchInfo().getOutputPort());
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).post(String.class, fm);
            LOGGER.info("Route table: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    public String insertControllerInfo(ControllerInfo ctrl) {
        String response = null;
        try {
            LOGGER.info("Calling get Route Table service");
            String request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/putSwitchController");
            Form fm = new Form();
            fm.add("ipController", ctrl.getControllerIp());
            fm.add("portController", ctrl.getControllerPort());
            fm.add("switchMac", ctrl.getMacAddress());
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).post(String.class, fm);
            LOGGER.info("Route table: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }

    /*
     * Receive a json that contains a table with the controller-switch information.
     * 
     */
    public String getInfoControllers() {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Information");
            String request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/getSwitchControllers/");
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
            LOGGER.info("Controller info: " + response);
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
            String request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/getControllerStatus/"+ip);
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

    public String deleteRoute(int id){
        String response = null;
        try {
            LOGGER.info("Remove route");
            String request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/removeFlowById");
            Form fm = new Form();
            fm.add("id", id);
            Client client = Client.create();
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.TEXT_PLAIN).post(String.class, fm);
            LOGGER.info("Controller status: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return response;
    }
}
