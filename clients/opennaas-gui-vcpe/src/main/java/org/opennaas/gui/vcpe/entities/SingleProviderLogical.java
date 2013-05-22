package org.opennaas.gui.vcpe.entities;

/**
 * @author Jordi
 */
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

public class SingleProviderLogical extends LogicalInfrastructure {

	@Valid
	private PhysicalRouter	routerCore;
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
	public SingleProviderLogical() {
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
	 * @return the routerCore
	 */
	public PhysicalRouter getRouterCore() {
		return routerCore;
	}

	/**
	 * @param routerCore the routerCore to set
	 */
	public void setRouterCore(PhysicalRouter routerCore) {
		this.routerCore = routerCore;
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
