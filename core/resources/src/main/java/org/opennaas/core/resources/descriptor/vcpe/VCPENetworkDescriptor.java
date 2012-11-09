package org.opennaas.core.resources.descriptor.vcpe;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.descriptor.ResourceDescriptor;

@XmlRootElement
@Entity
public class VCPENetworkDescriptor extends ResourceDescriptor {

	public static final String	RESOURCE_TYPE	= "vcpenet";

	@Basic(fetch = FetchType.LAZY)
	@Lob
	private String				vCPEModel;

	@XmlElement(name = "vCPEModel", namespace = "org.opennaas.core.resources.descriptor.vcpe")
	public String getvCPEModel() {
		return vCPEModel;
	}

	public void setvCPEModel(String vCPEModel) {
		this.vCPEModel = vCPEModel;
	}
}
