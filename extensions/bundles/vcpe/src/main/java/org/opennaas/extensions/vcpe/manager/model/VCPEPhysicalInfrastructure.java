package org.opennaas.extensions.vcpe.manager.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.opennaas.extensions.vcpe.model.Domain;
import org.opennaas.extensions.vcpe.model.Link;
import org.opennaas.extensions.vcpe.model.Router;
import org.opennaas.extensions.vcpe.model.VCPENetworkElement;

// TODO this class should be template independent. Hence, no structure can be pre-defined
// TODO this class should be replaced by a NetworkResource with its topology.
// This approach would allow users to:
// 1. create resources in opennaas
// 2. create a network with desired resources
// 3. create a VCPENetworkManager with desired network as a physical topology
// 4. create VCPENetworkResources for clients.
// and all that without touching a single configuration file.
// No need to say that Template and VCPENetworkBuilder logic will need to change significantly to support that :S
// But that's the way to go, IMO.
@XmlAccessorType(XmlAccessType.FIELD)
public class VCPEPhysicalInfrastructure {

	private Router		phyRouterCore;
	private Router		phyRouterMaster;
	private Router		phyRouterBackup;

	private Domain		phyBoD;

	private List<Link>	phyLinks;

	public Router getPhyRouterCore() {
		return phyRouterCore;
	}

	public void setPhyRouterCore(Router phyRouterCore) {
		this.phyRouterCore = phyRouterCore;
	}

	public Router getPhyRouterMaster() {
		return phyRouterMaster;
	}

	public void setPhyRouterMaster(Router phyRouterMaster) {
		this.phyRouterMaster = phyRouterMaster;
	}

	public Router getPhyRouterBackup() {
		return phyRouterBackup;
	}

	public void setPhyRouterBackup(Router phyRouterBackup) {
		this.phyRouterBackup = phyRouterBackup;
	}

	public Domain getPhyBoD() {
		return phyBoD;
	}

	public void setPhyBoD(Domain phyBoD) {
		this.phyBoD = phyBoD;
	}

	public List<Link> getPhyLinks() {
		return phyLinks;
	}

	public void setPhyLinks(List<Link> phyLinks) {
		this.phyLinks = phyLinks;
	}

	/**
	 * Creates a list with all VCPENetworkElements forming the physicalInfrastructure. Adding elements to returned list does NOT include the elements
	 * in this PhysicalInfrastructure .
	 * 
	 * @return a list with all VCPENetworkElements forming the physicalInfrastructure
	 */
	public List<VCPENetworkElement> getAllElements() {

		List<VCPENetworkElement> all = new ArrayList<VCPENetworkElement>();
		all.add(phyRouterCore);
		all.add(phyRouterMaster);
		all.add(phyRouterBackup);
		all.add(phyBoD);
		all.addAll(phyLinks);

		all.addAll(phyRouterCore.getInterfaces());
		all.addAll(phyRouterMaster.getInterfaces());
		all.addAll(phyRouterBackup.getInterfaces());
		all.addAll(phyBoD.getInterfaces());

		return all;
	}

}
