package org.opennaas.gui.nfvrouting.bos;

import org.apache.log4j.Logger;
import org.opennaas.gui.nfvrouting.entities.ControllerInfo;
import org.opennaas.gui.nfvrouting.entities.Route;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.opennaas.gui.nfvrouting.services.rest.routing.NFVRoutingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Josep
 */
public class NFVRoutingBO {

    private static final Logger LOGGER = Logger.getLogger(NFVRoutingBO.class);
    @Autowired
    private NFVRoutingService nfvRoutingService;

    /**
     * Obtain a route table given the IP type
     * @param resourceName
     * @param type
     * @return json that contains the specified route table
     * @throws RestServiceException 
     */
    public String getRouteTable(String resourceName, int type) throws RestServiceException {
        LOGGER.debug("Get the Route Table: " + resourceName);
        return nfvRoutingService.getRouteTable(resourceName, type);
    }

    /**
     * Insert new route in the OpenNaaS model
     * @param resourceName
     * @param route
     * @return status
     */
    public String insertRoute(String resourceName, Route route) {
        return nfvRoutingService.insertRoute(resourceName, route);
    }
    
    /**
     * Insert relation between controller and switch
     * @param resourceName
     * @param ctrl
     * @return status
     */
    public String insertCtrlInfo(String resourceName, ControllerInfo ctrl) {
         return nfvRoutingService.insertControllerInfo(resourceName, ctrl);
    }

    /**
     * Obtain information about the relation between all the controllers <-> switches
     * @param resourceName
     * @return 
     */
    public String getInfoControllers(String resourceName) {
        return nfvRoutingService.getInfoControllers(resourceName);
    }
    
    /**
     * Request the status of specific controller
     * @param resourceName
     * @param ip
     * @return Offline or Online
     */
    public String getControllerStatus(String resourceName, String ip) {
        return nfvRoutingService.getControllerStatus(resourceName, ip);
    }

    /**
     * Remove route given the id
     * @param resourceName
     * @param id
     * @return status
     */
    public String deleteRoute(String resourceName, int id, int version){
        return nfvRoutingService.deleteRoute(resourceName, id, version);
    }

    /**
     * Obtain the log of the OpenNaaS console. Used only in Demos...
     * @param resourceName
     * @return 
     */
    public String getLog(String resourceName) {
         return nfvRoutingService.getLog(resourceName);
    }
}
