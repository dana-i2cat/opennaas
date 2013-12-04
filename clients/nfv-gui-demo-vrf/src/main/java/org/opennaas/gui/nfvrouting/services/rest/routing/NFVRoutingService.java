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
import org.opennaas.gui.nfvrouting.utils.Constants;

/**
 * @author Josep Batallé (josep.batalle@i2cat.net)
 */
public class NFVRoutingService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingService.class);
    private static String resourceType = Constants.RESOURCE_VRF_TYPE;
    private static String capabilityName = Constants.CAPABILITY_VRF;

    /**
     * Call a rest service to get the Route Table of the virtualized router
     * 
     * @param request
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String getRouteTable(String resourceName, int type) throws RestServiceException {
        String response = null;
        
        try {
            LOGGER.info("Calling get Route Table service");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/routes/"+type);
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
     * @param request
     * @return true if the environment has been created
     * @throws RestServiceException
     */
    public String insertRoute(String resourceName, Route route) {
        String response = null;
        try {
            LOGGER.info("Calling insert Route Table service");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/route");
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

    public String insertControllerInfo(String resourceName, ControllerInfo ctrl) {
        String response = null;
        try {
            LOGGER.info("Calling insert controller service");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/putSwitchController");
            Form fm = new Form();
            fm.add("ipController", ctrl.getControllerIp());
            fm.add("portController", ctrl.getControllerPort());
            fm.add("switchDPID", ctrl.getMacAddress());
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
     * Receive a json that contains a table with the controller-switch information.
     * @param resourceName
     * @return 
     */
    public String getInfoControllers(String resourceName) {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Information");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/getSwitchControllers");
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
    public String getControllerStatus(String resourceName, String ip) {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/getControllerStatus/"+ip);
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

    public String deleteRoute(String resourceName, int id, int version){
        String response = null;
        try {
            LOGGER.info("Remove route");
            LOGGER.error("Remove route");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/routes");
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

    public String getLog(String resourceName) {
        String response = null;
        try {
            LOGGER.info("Get log of OpenNaaS");
            String url = getURL(resourceType+"/" + resourceName + "/"+capabilityName+"/log");
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
