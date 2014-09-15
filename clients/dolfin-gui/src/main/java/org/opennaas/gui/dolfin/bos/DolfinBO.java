package org.opennaas.gui.dolfin.bos;

import org.opennaas.extensions.genericnetwork.capability.nclprovisioner.api.CircuitCollection;
import org.opennaas.extensions.genericnetwork.model.topology.Topology;
import org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper.QoSPolicyRequestsWrapper;
import org.opennaas.gui.dolfin.services.rest.RestServiceException;
import org.opennaas.gui.dolfin.services.rest.dolfin.DolfinService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class DolfinBO {

    @Autowired
    private DolfinService dolfinService;

    /**
     * Get Collection of Allocated Circuits
     *
     * @return json that contains the specified route table
     * @throws org.opennaas.gui.dolfin.services.rest.RestServiceException
     */
    public CircuitCollection getAllocatedCircuits() throws RestServiceException{
        return dolfinService.getAllocatedCircuits();
    }

    /**
     * Get Topology defined in OpenNaaS
     *
     * @return status
     * @throws org.opennaas.gui.dolfin.services.rest.RestServiceException
     */
    public Topology getTopology() throws RestServiceException {
        return dolfinService.getTopology();
    }

    /**
     * Get 
     * @param switchName
     * @return 
     */
    public String getAllocatedFlows(String switchName) {
        return dolfinService.getAllocatedFlows(switchName);
    }

    public String allocateFlow(String flows) {
        return dolfinService.allocateFlow(flows);
    }

    public QoSPolicyRequestsWrapper getAllocatedFlow() {
        return dolfinService.getAllocatedFlow();
    }

    public String deallocatesFlow(String key) {
        return dolfinService.deallocateFlow(key);
    }

}
