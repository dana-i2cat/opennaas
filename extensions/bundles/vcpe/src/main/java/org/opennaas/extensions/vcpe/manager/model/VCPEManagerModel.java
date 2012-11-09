package org.opennaas.extensions.vcpe.manager.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IResource;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class VCPEManagerModel {

	private VCPEPhysicalInfrastructure	physicalInfrastructure;

	private List<IResource>				vcpeNetworks;

	public VCPEPhysicalInfrastructure getPhysicalInfrastructure() {
		return physicalInfrastructure;
	}

	public void setPhysicalInfrastructure(VCPEPhysicalInfrastructure physicalInfrastructure) {
		this.physicalInfrastructure = physicalInfrastructure;
	}

	public List<IResource> getVcpeNetworks() {
		return vcpeNetworks;
	}

	public void setVcpeNetworks(List<IResource> vcpeNetworks) {
		this.vcpeNetworks = vcpeNetworks;
	}
}
