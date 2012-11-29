package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

public class VCPENetwork {

	private String			id;
	@NotBlank(message = "{error.field.mandatory}")
	private String			name;
	@NotBlank(message = "{error.field.mandatory}")
	private String			template;
	@Valid
	private LogicalRouter	logicalRouter1;
	@Valid
	private LogicalRouter	logicalRouter2;
	@Pattern(regexp = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})/(\\d{1,3})", message = "{error.field.format.ipandmask}")
	private String			clientIpRange;
	@Valid
	private BGP				bgp;
	@Valid
	private BoD				bod;
	@Valid
	private VRRP			vrrp;
	private List<Link>		links;

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

	/**
	 * @return the bgp
	 */
	public BGP getBgp() {
		return bgp;
	}

	/**
	 * @param bgp
	 *            the bgp to set
	 */
	public void setBgp(BGP bgp) {
		this.bgp = bgp;
	}

	/**
	 * @return the bod
	 */
	public BoD getBod() {
		return bod;
	}

	/**
	 * @param bod
	 *            the bod to set
	 */
	public void setBod(BoD bod) {
		this.bod = bod;
	}

	/**
	 * @return the vrrp
	 */
	public VRRP getVrrp() {
		return vrrp;
	}

	/**
	 * @param vrrp
	 *            the vrrp to set
	 */
	public void setVrrp(VRRP vrrp) {
		this.vrrp = vrrp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VCPENetwork [id=" + id + ", name=" + name + ", template=" + template + ", logicalRouter1=" + logicalRouter1 + ", logicalRouter2=" + logicalRouter2 + ", links=" + links + ", clientIpRange=" + clientIpRange + ", bgp=" + bgp + ", bod=" + bod + "]";
	}

}
