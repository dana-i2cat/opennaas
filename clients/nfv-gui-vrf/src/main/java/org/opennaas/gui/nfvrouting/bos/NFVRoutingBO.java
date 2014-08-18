package org.opennaas.gui.nfvrouting.bos;

import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.gui.nfvrouting.entities.route.Route;
import org.opennaas.gui.nfvrouting.services.rest.RestServiceException;
import org.opennaas.gui.nfvrouting.services.rest.routing.NFVRoutingService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Josep
 */
public class NFVRoutingBO {

    @Autowired
    private NFVRoutingService nfvRoutingService;

    /**
     * Obtain a route table given the IP type
     *
     * @param type
     * @return json that contains the specified route table
     * @throws RestServiceException
     */
    public String getRouteTable(int type) throws RestServiceException {
        return nfvRoutingService.getRouteTable(type);
    }

    /**
     * Insert new route in the OpenNaaS model
     *
     * @param route
     * @return status
     */
    public String insertRoute(Route route) {
        return nfvRoutingService.insertRoute(route);
    }

    /**
     * Remove route given the id
     *
     * @param id
     * @param version
     * @return status
     */
    public String deleteRoute(int id, int version) {
        return nfvRoutingService.deleteRoute(id, version);
    }
    /**
     * Remove route given the id
     *
     * @return status
     */
    public String deleteAllRoutes() {
        return nfvRoutingService.deleteAllRoutes();
    }

    /**
     * Request the status of specific controller
     *
     * @param resourceName
     * @return Offline or Online
     */
    public String getFlowTable(String resourceName) {
        return nfvRoutingService.getFlowTable(resourceName);
    }

    public String getRoute(String ipSrc, String ipDst, String dpid, String inPort) {
        return nfvRoutingService.getRoute(ipSrc, ipDst, dpid, inPort);
    }

    public String insertRoute(String ipSrc, String ipDst, String dpid, String srcPort, String dstPort) {
        return nfvRoutingService.insertRoute(ipSrc, ipDst, dpid, srcPort, dstPort);
    }

    public String getRoute(String srcIP, String dstIP) {
        return nfvRoutingService.getRoute(srcIP, dstIP);
    }

    public String getONRouteMode() {
        return nfvRoutingService.getONRouteMode();
    }
    
    public String setONRouteMode(String mode) {
        return nfvRoutingService.setONRouteMode(mode);
    }

    /**
     * Get Topology defined in OpenNaaS
     *
     * @return status
     * @throws org.opennaas.gui.nfvrouting.services.rest.RestServiceException
     */
    public Topology getTopology() throws RestServiceException {
        return nfvRoutingService.getTopology();
    }

    public String setGenNetResource(String genNetResName) {
        return nfvRoutingService.setGenNetResource(genNetResName);
    }
}
