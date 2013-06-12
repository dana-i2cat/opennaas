package org.opennaas.gui.nfvrouting.services.rest.routing;


import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.entities.ControllerInfo;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.GenericRestService;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;

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
    public String getRouteTable(String request) throws RestServiceException {
        String response = null;
        try {
            LOGGER.info("Calling get Route Table service");
            request = "VM-Routing1";
            String url = getURL("ofrouting/" + request + "/routing/getRouteTable");
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

    public String insertRoute(ControllerInfo ctrl) {
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

}
