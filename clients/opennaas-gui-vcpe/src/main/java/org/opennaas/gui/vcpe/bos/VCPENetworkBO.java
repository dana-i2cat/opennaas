/**
 * 
 */
package org.opennaas.gui.vcpe.bos;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;
import org.opennaas.gui.vcpe.entities.VCPENetwork;
import org.opennaas.gui.vcpe.services.rest.RestServiceException;
import org.opennaas.gui.vcpe.services.rest.vcpe.BuilderCapabilityService;
import org.opennaas.gui.vcpe.services.rest.vcpe.VCPENetworkService;
import org.opennaas.gui.vcpe.utils.model.OpennasBeanUtils;
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
	 * @param vcpeNetwork
	 * @return the id
	 * @throws RestServiceException
	 */
	public String create(VCPENetwork vcpeNetwork) throws RestServiceException {
		LOGGER.debug("create a VCPENetwork: " + vcpeNetwork);
		return vcpeNetworkService.createVCPENetwork(OpennasBeanUtils.getVCPENetwork(vcpeNetwork));
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
	public VCPENetwork getById(String vcpeNetworkId) throws RestServiceException {
		LOGGER.debug("get a VCPENetwork with id: " + vcpeNetworkId);
		VCPENetworkModel openNaasModel = vcpeNetworkService.getVCPENetworkById(vcpeNetworkId);
		return VCPEBeanUtils.getVCPENetwork(openNaasModel);
	}

	/**
	 * Get all VCPE Network
	 * 
	 * @return List<VCPENetwork>
	 * @throws RestServiceException
	 */
	public List<VCPENetwork> getAllVCPENetworks() throws RestServiceException {
		LOGGER.debug("get all VCPENetwork");
		return getListVCPENetworkGUI(vcpeNetworkService.getAllVCPENetworks());
	}

	/**
	 * Update the ip's of the VCPENetwork
	 * 
	 * @param vcpeNetwork
	 * @return true if the Ips have been updated
	 */
	public Boolean updateIps(VCPENetwork vcpeNetwork) throws RestServiceException {
		LOGGER.debug("update Ip's of VCPENetwork");
		builderService.updateIpsVCPENetwork(OpennasBeanUtils.getVCPENetwork(vcpeNetwork));
		return true;
	}

	/**
	 * @param vlan
	 * @param ifaceName
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isVLANFree(String vcpeId, String vlan, String ifaceName) throws RestServiceException {
		LOGGER.debug("Check if the VLAN: " + vlan + " is free in the ifaceName: " + ifaceName + ". The vcpeID: " + vcpeId);
		return vcpeNetworkService.isVLANFree(vcpeId, vlan, ifaceName);
	}

	/**
	 * @param ip
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isIPFree(String vcpeId, String ip) throws RestServiceException {
		LOGGER.debug("check if the IP: " + ip + " is free. The vcpeID: " + vcpeId);
		return vcpeNetworkService.isIPFree(vcpeId, ip);
	}

	/**
	 * @param iface
	 * @param port
	 * @return true if is free
	 * @throws RestServiceException
	 */
	public Boolean isInterfaceFree(String vcpeId, String iface, String port) throws RestServiceException {
		LOGGER.debug("check if the Interface: " + iface + "." + port + "is free. The vcpeID: " + vcpeId);
		return vcpeNetworkService.isInterfaceFree(vcpeId, iface, port);
	}

	/**
	 * @param allVCPENetworks
	 * @return
	 */
	private List<VCPENetwork> getListVCPENetworkGUI(List<VCPENetworkModel> listModelIn) {
		List<VCPENetwork> listModelOut = new ArrayList<VCPENetwork>();
		for (int i = 0; i < listModelIn.size(); i++) {
			listModelOut.add(VCPEBeanUtils.getVCPENetwork(listModelIn.get(i)));
		}
		return listModelOut;
	}

}
