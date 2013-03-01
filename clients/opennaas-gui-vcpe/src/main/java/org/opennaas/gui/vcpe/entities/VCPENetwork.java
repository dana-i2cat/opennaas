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
	@NotBlank(message = "{message.error.field.mandatory}")
	private String			name;
	@NotBlank(message = "{message.error.field.mandatory}")
	private String			templateType;
	@Valid
	private LogicalRouter	logicalRouterMaster;
	@Valid
	private LogicalRouter	logicalRouterBackup;
	@Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])$", message = "{message.error.field.format.ipandmask}")
	private String			clientIpRange;
	@Pattern(regexp = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])/(\\d{1}|[0-2]{1}\\d{1}|3[0-2])$", message = "{message.error.field.format.ipandmask}")
	private String			nocIpRange;
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the templateType
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType the templateType to set
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return the logicalRouterMaster
	 */
	public LogicalRouter getLogicalRouterMaster() {
		return logicalRouterMaster;
	}

	/**
	 * @param logicalRouterMaster the logicalRouterMaster to set
	 */
	public void setLogicalRouterMaster(LogicalRouter logicalRouterMaster) {
		this.logicalRouterMaster = logicalRouterMaster;
	}

	/**
	 * @return the logicalRouterBackup
	 */
	public LogicalRouter getLogicalRouterBackup() {
		return logicalRouterBackup;
	}

	/**
	 * @param logicalRouterBackup the logicalRouterBackup to set
	 */
	public void setLogicalRouterBackup(LogicalRouter logicalRouterBackup) {
		this.logicalRouterBackup = logicalRouterBackup;
	}

	/**
	 * @return the clientIpRange
	 */
	public String getClientIpRange() {
		return clientIpRange;
	}

	/**
	 * @param clientIpRange the clientIpRange to set
	 */
	public void setClientIpRange(String clientIpRange) {
		this.clientIpRange = clientIpRange;
	}

	/**
	 * @return the nocIpRange
	 */
	public String getNocIpRange() {
		return nocIpRange;
	}

	/**
	 * @param nocIpRange the nocIpRange to set
	 */
	public void setNocIpRange(String nocIpRange) {
		this.nocIpRange = nocIpRange;
	}

	/**
	 * @return the links
	 */
	public List<Link> getLinks() {
		return links;
	}

	/**
	 * @param links the links to set
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
	 * @param bgp the bgp to set
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
	 * @param bod the bod to set
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
	 * @param vrrp the vrrp to set
	 */
	public void setVrrp(VRRP vrrp) {
		this.vrrp = vrrp;
	}

}
