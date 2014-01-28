package org.opennaas.extensions.router.capability.ospf.api;

import java.io.Serializable;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ospfService")
@XmlAccessorType(XmlAccessType.FIELD)
public class OSPFServiceWrapper implements Serializable {

	private static final long			serialVersionUID	= 6379966189844614183L;

	@XmlElement(name = "ospfArea")
	private Collection<OSPFAreaWrapper>	ospfAreas;

	public Collection<OSPFAreaWrapper> getOspfAreas() {
		return ospfAreas;
	}

	public void setOspfArea(Collection<OSPFAreaWrapper> ospfAreas) {
		this.ospfAreas = ospfAreas;
	}

}
