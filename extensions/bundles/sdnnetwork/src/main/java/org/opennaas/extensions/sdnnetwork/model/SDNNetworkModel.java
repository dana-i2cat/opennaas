package org.opennaas.extensions.sdnnetwork.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.core.resources.IModel;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * @author Julio Carlos Barrera
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SDNNetworkModel implements IModel {

	/**
	 * Auto-generated serial version number
	 */
	private static final long				serialVersionUID	= -3223373735906486372L;

	private Collection<SDNNetworkOFFlow>	flows;

	/**
	 * Maps device ID in SDNNetworkModel and resource ID in OpenNaaS
	 */
	private Map<String, String>				deviceResourceMap;

	/**
	 * @return the flows
	 */
	public Collection<SDNNetworkOFFlow> getFlows() {
		return flows;
	}

	/**
	 * @param flows
	 *            the flows to set
	 */
	public void setFlows(Collection<SDNNetworkOFFlow> flows) {
		this.flows = flows;
	}

	public Map<String, String> getDeviceResourceMap() {
		if (deviceResourceMap == null) {
			deviceResourceMap = new HashMap<String, String>();
		}
		return deviceResourceMap;
	}

	public void setDeviceResourceMap(Map<String, String> deviceResourceMap) {
		this.deviceResourceMap = deviceResourceMap;
	}

	@Override
	public List<String> getChildren() {
		return new ArrayList<String>(0);
	}

	@Override
	public String toXml() throws SerializationException {
		return ObjectSerializer.toXml(this);
	}

}
