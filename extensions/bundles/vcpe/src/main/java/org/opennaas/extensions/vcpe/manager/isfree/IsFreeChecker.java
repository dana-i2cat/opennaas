/**
 * 
 */
package org.opennaas.extensions.vcpe.manager.isfree;

import static com.google.common.collect.Iterables.filter;

import java.util.List;

import org.opennaas.core.resources.ActivatorException;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.extensions.vcpe.Activator;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManager;
import org.opennaas.extensions.vcpe.manager.VCPENetworkManagerException;
import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Interface;
import org.opennaas.extensions.vcpe.model.LogicalRouter;
import org.opennaas.extensions.vcpe.model.VCPENetworkModel;

/**
 * @author Jordi
 */
public class IsFreeChecker {

	/**
	 * Check if a VLAN in an interface is free in VCPE. <br>
	 * Check in all Domains and check in all Logical Routers
	 * 
	 * @param vcpeId
	 *            of the VLAN
	 * @param routerName
	 *            of the VLAN
	 * @param vlan
	 *            to check
	 * @param ifaceName
	 *            of the VLAN
	 * @return true if the VLAN is free, otherwise false
	 * @throws VCPENetworkManagerException
	 */
	public static Boolean isVLANFree(String vcpeId, String router, String vlan, String ifaceName) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			// check with all vcpe resources
			List<IResource> vcpes = manager.listResourcesByType(VCPENetworkManager.RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				// check only if is not busy by other vcpe
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					if (((VCPENetworkModel) vcpe.getModel()).isCreated()) {
						if (isFree = isVLANFreeInDomains(vcpe, ifaceName, vlan)) {
							isFree = isVLANFreeInLRs(vcpe, router, ifaceName, vlan);
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the interface: " + ifaceName);
		}
		return isFree;
	}

	/**
	 * Check if a VLAN is free in the Logical Routers of a VCPE
	 * 
	 * @param vcpe
	 *            who has the LR's
	 * @param routerName
	 *            of the VLAN to check
	 * @param ifaceName
	 *            of the VLAN to check
	 * @param vlan
	 *            to check
	 * @return true if the VLAN is free, otherwise false
	 */
	public static boolean isVLANFreeInLRs(IResource vcpe, String routerName, String ifaceName, String vlan) {
		boolean isFree = true;
		for (LogicalRouter logicalRouter : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), LogicalRouter.class)) {
			// check in the same router only.
			// same vlan and interface in different logical router isn't an error
			if (logicalRouter.getPhysicalRouter().getName().equals(routerName)) {
				for (Interface iface : filter(logicalRouter.getInterfaces(), Interface.class)) {
					if (ifaceName.equals(iface.getPhysicalInterfaceName())
							&& vlan.equals(String.valueOf(iface.getVlan()))) {
						isFree = false;
					}
				}
			}
		}
		return isFree;
	}

	/**
	 * Check if a VLAN is free in the Domains of a VCPE
	 * 
	 * @param vcpe
	 *            who has the Domains
	 * @param ifaceName
	 *            of the VLAN to check
	 * @param vlan
	 *            to check
	 * @return true if the VLAN is free, otherwise false
	 */
	public static boolean isVLANFreeInDomains(IResource vcpe, String ifaceName, String vlan) {
		boolean isFree = true;
		for (Domain domain : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), Domain.class)) {
			for (Interface iface : filter(domain.getInterfaces(), Interface.class)) {
				if (ifaceName.equals(iface.getPhysicalInterfaceName())
						&& vlan.equals(String.valueOf(iface.getVlan()))) {
					isFree = false;
				}
			}
		}
		return isFree;
	}

	/**
	 * Check if a Interface is free in VCPE. <br>
	 * Check in all Domains and check in all Logical Routers
	 * 
	 * @param vcpeId
	 *            of the Interface
	 * @param routerName
	 *            of the Interface
	 * @param ifaceName
	 *            interface to check
	 * @return true if the interface is free, otherwise false
	 * @throws VCPENetworkManagerException
	 */
	public static Boolean isInterfaceFree(String vcpeId, String router, String ifaceName) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			// check with all vcpe resources
			List<IResource> vcpes = manager.listResourcesByType(VCPENetworkManager.RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				// check only if is not busy by other vcpe
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					if (((VCPENetworkModel) vcpe.getModel()).isCreated()) {
						if (isFree = isInterfaceFreeInDomains(vcpe, ifaceName)) {
							isFree = isInterfaceFreeInLRs(vcpe, router, ifaceName);
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the interface: " + ifaceName);
		}
		return isFree;
	}

	/**
	 * Check if an Interface is free in the Logical Routers of a VCPE
	 * 
	 * @param vcpe
	 *            who has the LR's
	 * @param routerName
	 *            of the Interface to check
	 * @param Interface
	 *            to check
	 * @return true if the VLAN is free, otherwise false
	 */
	public static boolean isInterfaceFreeInLRs(IResource vcpe, String routerName, String ifaceName) {
		boolean isFree = true;
		for (LogicalRouter logicalRouter : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), LogicalRouter.class)) {
			// check in the same router only.
			// same interface in different logical router isn't an error
			if (logicalRouter.getPhysicalRouter().getName().equals(routerName)) {
				for (Interface iface : filter(logicalRouter.getInterfaces(), Interface.class)) {
					if (ifaceName.equals(iface.getName())) {
						isFree = false;
					}
				}
			}
		}
		return isFree;
	}

	/**
	 * Check if a Interface is free in the Domains of a VCPE
	 * 
	 * @param vcpe
	 *            who has the Domains
	 * @param ifaceName
	 *            interface to check
	 * @return true if the interface is free, otherwise false
	 */
	public static boolean isInterfaceFreeInDomains(IResource vcpe, String ifaceName) {
		boolean isFree = true;
		for (Domain domain : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), Domain.class)) {
			for (Interface iface : filter(domain.getInterfaces(), Interface.class)) {
				if (ifaceName.equals(iface.getName())) {
					isFree = false;
				}
			}
		}
		return isFree;
	}

	/**
	 * Check if an IP is free in VCPE. <br>
	 * Check in all Logical Routers
	 * 
	 * @param vcpeId
	 *            of the IP to check
	 * @param routerName
	 *            of the IP to check
	 * @param ip
	 *            to check
	 * @return true if the ip is free, otherwise false
	 * @throws VCPENetworkManagerException
	 */
	public static Boolean isIPFree(String vcpeId, String routerName, String ip) throws VCPENetworkManagerException {
		boolean isFree = true;
		try {
			IResourceManager manager = Activator.getResourceManagerService();
			// get all vcpe resources
			List<IResource> vcpes = manager.listResourcesByType(VCPENetworkManager.RESOURCE_VCPENET_TYPE);
			for (IResource vcpe : vcpes) {
				// check only if is not busy by other vcpe
				if (!vcpe.getResourceIdentifier().getId().equals(vcpeId)) {
					if (((VCPENetworkModel) vcpe.getModel()).isCreated()) {
						// get all routers
						for (LogicalRouter logicalRouter : filter(((VCPENetworkModel) vcpe.getModel()).getElements(), LogicalRouter.class)) {
							// check in the same router only
							if (logicalRouter.getPhysicalRouter().getName().equals(routerName)) {
								for (Interface iface : filter(logicalRouter.getInterfaces(), Interface.class)) {
									if (ip.equals(iface.getIpAddress())) {
										isFree = false;
									}
								}
							}
						}
					}
				}
			}
		} catch (ActivatorException e) {
			throw new VCPENetworkManagerException("Can't check the IP: " + ip);
		}
		return isFree;
	}
}
