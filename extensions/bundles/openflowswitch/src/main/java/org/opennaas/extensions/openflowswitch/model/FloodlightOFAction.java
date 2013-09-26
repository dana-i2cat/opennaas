package org.opennaas.extensions.openflowswitch.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class FloodlightOFAction {

	public static final String	TYPE_OUTPUT				= "output";
	public static final String	TYPE_ALL				= "all";
	public static final String	TYPE_CONTROLLER			= "controller";
	public static final String	TYPE_LOCAL				= "local";
	public static final String	TYPE_INGRESS_PORT		= "ingress-port";
	public static final String	TYPE_NORMAL				= "normal";
	public static final String	TYPE_FLOOD				= "flood";
	public static final String	TYPE_ENQUEUE			= "enqueue";
	public static final String	TYPE_STRIP_VLAN			= "strip-vlan";
	public static final String	TYPE_SET_VLAN_ID		= "set-vlan-id";
	public static final String	TYPE_SET_VLAN_PRIORITY	= "set-vlan-priority";
	public static final String	TYPE_SET_SRC_MAC		= "set-src-mac";
	public static final String	TYPE_SET_DST_MAC		= "set-dst-mac";
	public static final String	TYPE_TOS_BITS			= "set-tos-bits";
	public static final String	TYPE_SET_SRC_IP			= "set-src-ip";
	public static final String	TYPE_SET_DST_IP			= "set-dst-ip";
	public static final String	TYPE_SET_SRC_PORT		= "set-src-port";
	public static final String	TYPE_SET_DST_PORT		= "set-dst-port";

	/**
	 * FloodlightOFAction type
	 * 
	 * @see <a href="http://docs.projectfloodlight.org/display/floodlightcontroller/Static+Flow+Pusher+API+%28New%29">Floodlight documentation: Static
	 *      Flow Pusher API (New)</a>
	 */
	protected String			type;

	protected String			value;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FloodlightOFAction other = (FloodlightOFAction) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
