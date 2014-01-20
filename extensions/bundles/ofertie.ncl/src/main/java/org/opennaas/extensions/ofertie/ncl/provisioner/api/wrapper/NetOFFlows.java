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

}
