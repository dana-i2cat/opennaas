/**
 * 
 */
package org.opennaas.gui.vcpe.bos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.entities.LogicalInfrastructure;
import org.opennaas.gui.vcpe.entities.PhysicalInfrastructure;
import org.opennaas.gui.vcpe.entities.SingleProviderLogical;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.opennaas.gui.vcpe.services.rest.vcpe.BuilderCapabilityService;
import org.opennaas.gui.vcpe.services.rest.vcpe.VCPENetworkService;
import org.opennaas.gui.vcpe.utils.model.OpennaasBeanUtils;
import org.opennaas.gui.vcpe.utils.model.VCPEBeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jordi
 */
public class VCPENetworkBO {

	private static final Logger			LOGGER	= Logger.getLogger(VCPENetworkBO.class);

	@Autowired
	private VCPENetworkService			vcpeNetworkService;

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
		String resourceId = vcpeNetworkService.createVCPENetwork(OpennaasBeanUtils.getVCPENetwork(logicalInfrastructure));
		// the resource is being created, but has not finished yet

		LOGGER.debug("Polling for building task to finish");
		while (!vcpeNetworkService.hasFinishedBuild(resourceId)) {
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				LOGGER.warn("Interrupted while waiting for VCPE build to finish", e);
				break;
			}
		}
		LOGGER.debug("Retrieving build result");
		vcpeNetworkService.getBuildResult(resourceId);

		return resourceId;
	}

	/**
	 * Update a VCPE Network
	 * 
	 * @param logicalInfrastructure
	 * @return id
	 * @throws RestServiceException
	 */
	public String update(LogicalInfrastructure logicalInfrastructure) throws RestServiceException {
		LOGGER.debug("update the VCPENetwork: " + logicalInfrastructure.getId());
		String resourceId = vcpeNetworkService.updateVCPENetwork(OpennaasBeanUtils.getVCPENetwork(logicalInfrastructure));
		// the resource is being updating, but has not finished yet

		LOGGER.debug("Polling for building task to finish");
		while (!vcpeNetworkService.hasFinishedBuild(resourceId)) {
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				LOGGER.warn("Interrupted while waiting for VCPE build to finish", e);
				break;
			}
		}
		LOGGER.debug("Retrieving build result");
		vcpeNetworkService.getBuildResult(resourceId);

		return resourceId;
	}

	/**
	 * Delete a VCPE Network
	 * 
	 * @param vcpeNetworkId
	 * @return true if the VCPE has been deleted
	 * @throws RestServiceException
	 */
	public boolean delete(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("remove the VCPENetwork: " + vcpeNetworkId);
		return vcpeNetworkService.destroyVCPENetwork(vcpeNetworkId);
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
		VCPENetworkModel openNaasModel = vcpeNetworkService.getVCPENetworkById(vcpeNetworkId);
		return VCPEBeanUtils.getLogicalInfrastructure(openNaasModel);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<LogicalInfrastructure>
	 * @throws RestServiceException
	 */
	public List<LogicalInfrastructure> getAllVCPENetworks() throws RestServiceException {
		LOGGER.debug("get all VCPENetwork");
		return getListVCPENetworkGUI(vcpeNetworkService.getAllVCPENetworks());
	}

	/**
	 * Get the physical infrastructure
	 * 
	 * @param templateType
	 * @return the physical infrastructure
	 * @throws RestServiceException
	 */
	public PhysicalInfrastructure getPhysicalInfrastructure(String templateType) throws RestServiceException {
		LOGGER.debug("get the physical infrastructure");
		return VCPEBeanUtils.getPhysicalInfrastructure(vcpeNetworkService.getPhysicalInfrastructure(templateType));
	}

	/**
	 * Get a suggest VCPENetwork
	 * 
	 * @param physical
	 * @return VCPENetwork
	 * @throws RestServiceException
	 */
	public LogicalInfrastructure getLogicalInfrastructure(PhysicalInfrastructure physical) throws RestServiceException {
		VCPENetworkModel physicalOpennaas = OpennaasBeanUtils.getPhysicalInfrastructure(physical);
		LogicalInfrastructure vcpeNetwork = VCPEBeanUtils.getLogicalInfrastructure(vcpeNetworkService.getLogicalInfrastructure(physicalOpennaas));
		vcpeNetwork.setTemplateType(physical.getTemplateType());
		return vcpeNetwork;
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

	/**
	 * Update the VRRP virtual ip address of the VCPENetwork
	 * 
	 * @param vcpeNetwork
	 * @return true if the Ip has been updated
	 * @throws RestServiceException
	 */
	public Boolean updateVRRPIp(SingleProviderLogical vcpeNetwork) throws RestServiceException {
		LOGGER.debug("update VRRP Ip of VCPENetwork");
		return builderService.updateVRRPIp(OpennaasBeanUtils.getVCPENetwork(vcpeNetwork));
	}

	/**
	 * Change the priority VRRP in the VCPENetwork
	 * 
	 * @param vcpeNetwork
	 * @return the new VCPENetwork configuration
	 * @throws RestServiceException
	 */
	public SingleProviderLogical changeVRRPPriority(SingleProviderLogical vcpeNetwork) throws RestServiceException {
		LOGGER.debug("change the Priority VRRP of VCPENetwork");
		VCPENetworkModel openNaasModel = builderService.changeVRRPPriority(OpennaasBeanUtils.getVCPENetwork(vcpeNetwork));
		return (SingleProviderLogical) VCPEBeanUtils.getLogicalInfrastructure(openNaasModel);
	}

	/**
	 * @param vcpeId
	 * @param router
	 * @param vlan
	 * @param ifaceName
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isVLANFree(String vcpeId, String router, String vlan, String ifaceName) throws RestServiceException {
		LOGGER.debug("Check if the VLAN: " + vlan + " is free in the ifaceName: " + ifaceName + ". The vcpeID: " + vcpeId);
		return vcpeNetworkService.isVLANFree(vcpeId, router, vlan, ifaceName);
	}

	/**
	 * @param vcpeId
	 * @param router
	 * @param ip
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isIPFree(String vcpeId, String router, String ip) throws RestServiceException {
		LOGGER.debug("check if the IP: " + ip + " is free. The vcpeID: " + vcpeId);
		return vcpeNetworkService.isIPFree(vcpeId, router, ip);
	}

	/**
	 * @param vcpeId
	 * @param router
	 * @param iface
	 * @param port
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isInterfaceFree(String vcpeId, String router, String iface, String port) throws RestServiceException {
		LOGGER.debug("check if the Interface: " + iface + "." + port + "is free. The vcpeID: " + vcpeId);
		return vcpeNetworkService.isInterfaceFree(vcpeId, router, iface, port);
	}

	/**
	 * @param allVCPENetworks
	 * @return
	 */
	private List<LogicalInfrastructure> getListVCPENetworkGUI(List<VCPENetworkModel> listModelIn) {
		List<LogicalInfrastructure> listModelOut = new ArrayList<LogicalInfrastructure>();
		for (int i = 0; i < listModelIn.size(); i++) {
			listModelOut.add(VCPEBeanUtils.getLogicalInfrastructure(listModelIn.get(i)));
		}
		return listModelOut;
	}

}
