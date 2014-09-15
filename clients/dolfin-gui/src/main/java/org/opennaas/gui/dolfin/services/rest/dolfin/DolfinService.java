package org.opennaas.gui.dolfin.services.rest.dolfin;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import javax.ws.rs.core.MediaType;
import org.apache.log4j.Logger;
import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.gui.dolfin.services.rest.GenericRestService;
import org.opennaas.gui.dolfin.services.rest.RestServiceException;
import org.opennaas.gui.dolfin.utils.Constants;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;

/**
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class DolfinService extends GenericRestService {

    private static final Logger LOGGER = Logger.getLogger(DolfinService.class);
    private static final String sdn = Constants.SDN_RESOURCE;
    private static final String genericNetwork = Constants.GENERICNETWORK_RESOURCE;

    /**
     * 
     * @return 
     * @throws org.opennaas.gui.dolfin.services.rest.RestServiceException 
     */
    public CircuitCollection getAllocatedCircuits() throws RestServiceException {
        ClientResponse response;
        try {
            LOGGER.info("Calling get Allocated Circuits");
            String url = getURL("genericnetwork/"+genericNetwork+"/nclprovisioner");
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
            LOGGER.error("Circuits: " + response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
        return checkResponse(response) ? response.getEntity(CircuitCollection.class) : null;
    }
    
    /**
     * 
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
    
    /**
     * Information about the switch.
     * @param switchName
     * @return Flow table of the switch.
     */
    public String getAllocatedFlows(String switchName) {
        String response = null;
//        String resourceName = getSwitchResourceName(dpid);//request the resourceName
        try {
            LOGGER.info("Calling get Controller Status");
            LOGGER.error("Calling sw INFO");
            String url = getURL("openflowswitch/" + switchName + "/offorwarding/getOFForwardingRules");
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
    
    public String allocateFlow(String flow) {
        ClientResponse response = null;
        String result = "";
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("ofertie/ncl/flows");
//            url = "http://admin:123456@84.88.40.109:8888/opennaas/ofertie/ncl/flows";
            LOGGER.error(url);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.accept(MediaType.APPLICATION_XML).type(MediaType.APPLICATION_XML).post(ClientResponse.class, flow);
            LOGGER.info("Controller status: " + response);
            LOGGER.error("Resource ID: "+response);
            LOGGER.error("Status: "+response.getStatus());
            if(response.getStatus() == (500)){
                result = String.valueOf(response.getStatus());
            }else{
                result = response.getEntity(String.class);
            }
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (com.sun.jersey.api.client.UniformInterfaceException e) {
            LOGGER.error("Unauthorized");
            result = String.valueOf(response.getStatus());
        }
        LOGGER.error(result);
        return result;
    }
    
    public QoSPolicyRequestsWrapper getAllocatedFlow() {
        QoSPolicyRequestsWrapper response = null;
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("ofertie/ncl/flows");
            LOGGER.error(url);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.get(QoSPolicyRequestsWrapper.class);
            LOGGER.info("Controller status: " + response);
            LOGGER.error("Resource ID: "+response);
        } catch (ClientHandlerException e) {
            LOGGER.error(e.getMessage());
            throw e;
        } catch (com.sun.jersey.api.client.UniformInterfaceException e) {
            LOGGER.error("Unauthorized");
        }
        return response;
    }
    
    public String deallocateFlow(String keyId) {
        String response = null;
        try {
            LOGGER.info("Calling get Controller Status");
            String url = getURL("ofertie/ncl/flows/"+keyId);
            LOGGER.error(url);
            Client client = Client.create();
            addHTTPBasicAuthentication(client);
            WebResource webResource = client.resource(url);
            response = webResource.delete(String.class);
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
}
