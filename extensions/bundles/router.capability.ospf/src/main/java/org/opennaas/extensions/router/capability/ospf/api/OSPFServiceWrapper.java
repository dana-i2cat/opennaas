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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ospfAreas == null) ? 0 : ospfAreas.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OSPFServiceWrapper other = (OSPFServiceWrapper) obj;
		if (ospfAreas == null) {
			if (other.ospfAreas != null)
				return false;
		} else if (!ospfAreas.equals(other.ospfAreas))
			return false;
		return true;
	}

}
