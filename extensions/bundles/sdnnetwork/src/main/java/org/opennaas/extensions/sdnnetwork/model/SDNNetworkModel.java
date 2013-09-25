package org.opennaas.extensions.sdnnetwork.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SDNNetworkModel implements IModel {
	
	private Collection<SDNNetworkOFFlow> flows;
	
	/**
	 * @return the flows
	 */
	public Collection<SDNNetworkOFFlow> getFlows() {
		return flows;
	}

	/**
	 * @param flows the flows to set
	 */
	public void setFlows(Collection<SDNNetworkOFFlow> flows) {
		this.flows = flows;
	}

	/**
	 * Auto-generated serial version number
	 */
	private static final long serialVersionUID = -3223373735906486372L;

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>(0);
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

}
