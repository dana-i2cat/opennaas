package org.opennaas.gui.nfvrouting.bos;


import org.apache.log4j.Logger;
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
     * Get the Route Table
     * 
     * @param logicalInfrastructure
     * @return
     * @throws RestServiceException
     */
    public String getRouteTable(String routingName) throws RestServiceException {
        LOGGER.debug("Get the Route Table: " + routingName);
        return nfvRoutingService.getRouteTable(routingName);
    }

    /**
     * Insert route to a Route table
     * 
     * @param logicalInfrastructure
     * @return
     * @throws RestServiceException
     */
    public String insertRoute(Route route) {
        return nfvRoutingService.insertRoute(route);
    }

    //////////////////////////////////////////////

    
}
