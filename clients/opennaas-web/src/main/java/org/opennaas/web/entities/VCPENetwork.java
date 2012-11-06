package org.opennaas.web.entities;

/**
 * @author Jordi
 */
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class VCPENetwork {

	private String			id;

	@NotNull
	@Size(min = 1, max = 25)
	private String			name;

	@NotNull
	@Size(min = 1, max = 25)
	private String			template;

	private LogicalRouter	logicalRouter1;

	private LogicalRouter	logicalRouter2;

	private List<Link>		links;

	private String			clientIpRange;

	/**
	 * Default constructor
	 */
	public VCPENetwork() {
	}

	/**
	 * @param vcpeNetwork
	 */
	public VCPENetwork(VCPENetwork vcpeNetwork) {
		this.id = vcpeNetwork.getId();
		this.name = vcpeNetwork.getName();
		this.template = vcpeNetwork.getName();
		this.logicalRouter1 = vcpeNetwork.getLogicalRouter1();
		this.logicalRouter2 = vcpeNetwork.getLogicalRouter2();
		this.clientIpRange = vcpeNetwork.getClientIpRange();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
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
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the logicalRouter1
	 */
	public LogicalRouter getLogicalRouter1() {
		return logicalRouter1;
	}

	/**
	 * @param logicalRouter1
	 *            the logicalRouter1 to set
	 */
	public void setLogicalRouter1(LogicalRouter logicalRouter1) {
		this.logicalRouter1 = logicalRouter1;
	}

	/**
	 * @return the logicalRouter2
	 */
	public LogicalRouter getLogicalRouter2() {
		return logicalRouter2;
	}

	/**
	 * @param logicalRouter2
	 *            the logicalRouter2 to set
	 */
	public void setLogicalRouter2(LogicalRouter logicalRouter2) {
		this.logicalRouter2 = logicalRouter2;
	}

	/**
	 * @return the clientIpRange
	 */
	public String getClientIpRange() {
		return clientIpRange;
	}

	/**
	 * @param clientIpRange
	 *            the clientIpRange to set
	 */
	public void setClientIpRange(String clientIpRange) {
		this.clientIpRange = clientIpRange;
	}

	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * @param links
	 *            the links to set
	 */
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VCPENetwork [id=" + id + ", name=" + name + ", template=" + template + ", logicalRouter1=" + logicalRouter1 + ", logicalRouter2=" + logicalRouter2 + ", clientIpRange=" + clientIpRange + "]";
	}

}
