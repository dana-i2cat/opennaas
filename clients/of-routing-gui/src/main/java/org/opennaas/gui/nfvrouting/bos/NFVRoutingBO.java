package org.opennaas.gui.nfvrouting.bos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.entities.LogicalInfrastructure;
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.entities.SingleProviderLogical;
import org.opennaas.gui.routing.services.rest.RestServiceException;
import org.opennaas.gui.routing.services.rest.vcpe.BuilderCapabilityService;
import org.opennaas.gui.routing.services.rest.vcpe.NFVRoutingService;
import org.opennaas.gui.vcpe.utils.model.OpennaasBeanUtils;
import org.opennaas.gui.vcpe.utils.model.VCPEBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author josep
 */
public class NFVRoutingBO {

	private static final Logger			LOGGER	= Logger.getLogger(NFVRoutingBO.class);

	@Autowired
	private NFVRoutingService			nfvRoutingService;

	@Autowired
	private BuilderCapabilityService	builderService;

	/**
	 * Create a VCPE Network
	 * 
	 * @param logicalInfrastructure
	 * @return
	 * @throws RestServiceException
	 */
	public String create(LogicalInfrastructure logicalInfrastructure) throws RestServiceException {
		LOGGER.debug("create a VCPENetwork: " + logicalInfrastructure);
		return nfvRoutingService.createVCPENetwork(OpennaasBeanUtils.getVCPENetwork(logicalInfrastructure));
	}

	/**
	 * Get a VCPE Network with id = vcpeNetworkId
	 * 
	 * @param vcpeNetworkId
	 * @return VCPENetwork
	 * @throws RestServiceException
	 */
	public LogicalInfrastructure getById(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		VCPENetworkModel openNaasModel = nfvRoutingService.getVCPENetworkById(vcpeNetworkId);
		return VCPEBeanUtils.getLogicalInfrastructure(openNaasModel);
	}

	
	/**
	 * Update the ip's of the VCPENetwork
	 * 
	 * @param vcpeNetwork
	 * @return true if the Ips have been updated
	 * @throws RestServiceException
	 */
	public Boolean updateIps(SingleProviderLogical vcpeNetwork) throws RestServiceException {
		LOGGER.debug("update Ip's of VCPENetwork");
		return builderService.updateIpsVCPENetwork(OpennaasBeanUtils.getVCPENetwork(vcpeNetwork));
	}

	
}
