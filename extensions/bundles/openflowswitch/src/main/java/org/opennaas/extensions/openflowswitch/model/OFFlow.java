package org.opennaas.extensions.openflowswitch.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OFFlow {

	/**
	 * <string> Name of the flow entry, this is the primary key, it MUST be unique
	 */
	protected String					name;
	/**
	 * <number> default is 32767. maximum value is 32767.
	 */
	protected String					priority;
	protected boolean					active;

	protected List<FloodlightOFAction>	actions;
	protected FloodlightOFMatch			match;

	/**
	 * Default constructor
	 */
	public OFFlow() {
	}

	/**
	 * Copy constructor
	 * 
	 * @param ofFlow
	 */
	public OFFlow(OFFlow ofFlow) {
		this.name = ofFlow.getName();
		this.priority = ofFlow.getPriority();
		this.active = ofFlow.isActive();
		this.actions = ofFlow.getActions();
		this.match = ofFlow.getMatch();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active
	 *            the active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * @return the actions
	 */
	public List<FloodlightOFAction> getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            the actions to set
	 */
	public void setActions(List<FloodlightOFAction> actions) {
		this.actions = actions;
	}

	/**
	 * @return the match
	 */
	public FloodlightOFMatch getMatch() {
		return match;
	}

	/**
	 * @param match
	 *            the match to set
	 */
	public void setMatch(FloodlightOFMatch match) {
		this.match = match;
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
		result = prime * result + ((actions == null) ? 0 : actions.hashCode());
		result = prime * result + (active ? 1231 : 1237);
		result = prime * result + ((match == null) ? 0 : match.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
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
		OFFlow other = (OFFlow) obj;
		if (actions == null) {
			if (other.actions != null)
				return false;
		} else if (!actions.equals(other.actions))
			return false;
		if (active != other.active)
			return false;
		if (match == null) {
			if (other.match != null)
				return false;
		} else if (!match.equals(other.match))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (priority == null) {
			if (other.priority != null)
				return false;
		} else if (!priority.equals(other.priority))
			return false;
		return true;
	}

}
