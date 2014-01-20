package org.opennaas.extensions.ofertie.ncl.provisioner.api.wrapper;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.ofnetwork.model.NetOFFlow;

/**
 * List Wrapper class storing a {@link List} of {@link NetOFFlow}
 * 
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement(name = "NetOFFlows")
@XmlAccessorType(XmlAccessType.FIELD)
public class NetOFFlows {

	@XmlElement(name = "NetOFFlow")
	private List<NetOFFlow>	list;

	public NetOFFlows() {
	}

	public NetOFFlows(List<NetOFFlow> list) {
		this.list = list;
	}

	public List<NetOFFlow> getList() {
		return list;
	}

	public void setList(List<NetOFFlow> list) {
		this.list = list;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((list == null) ? 0 : list.hashCode());
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
		NetOFFlows other = (NetOFFlows) obj;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		return true;
	}

}
