package net.i2cat.mantychore.models;

import java.util.ArrayList;
import java.util.List;

public class RouterModel {

	@NotBlank
	protected String					routerName;
	@NotBlank
	protected String					hostName;
	protected String					routerModel;
	protected String					versionOS;
	@NotBlank
	protected String					logicalName;
	@NotBlank
	protected boolean					isOperation;
	@NotBlank
	protected boolean					allowsRouterInstanceCreation;
	@NotBlank
	protected boolean					isPhysical;
	@NotBlank
	protected String					routerIDs;
	protected Location					location;
	@NotBlank
	protected AccessConfiguration		accessConfiguration;
	@NotBlank
	protected List<UserAccount>			userAccounts;
	protected List<PhysicalInterface>	physicalInterfaces;
	protected List<RouterModel>			children;
	protected String					parent;

	public String getRouterName() {
		return routerName;
	}

	public void setRouterName(String value) {
		this.routerName = value;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String value) {
		this.hostName = value;
	}

	public String getRouterModel() {
		return routerModel;
	}

	public void setRouterModel(String value) {
		this.routerModel = value;
	}

	public String getVersionOS() {
		return versionOS;
	}

	public void setVersionOS(String value) {
		this.versionOS = value;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String value) {
		this.logicalName = value;
	}

	public boolean isIsOperation() {
		return isOperation;
	}

	public void setIsOperation(boolean value) {
		this.isOperation = value;
	}

	public boolean isAllowsRouterInstanceCreation() {
		return allowsRouterInstanceCreation;
	}

	public void setAllowsRouterInstanceCreation(boolean value) {
		this.allowsRouterInstanceCreation = value;
	}

	public boolean isIsPhysical() {
		return isPhysical;
	}

	public void setIsPhysical(boolean value) {
		this.isPhysical = value;
	}

	public String getRouterIDs() {
		return routerIDs;
	}

	public void setRouterIDs(String value) {
		this.routerIDs = value;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location value) {
		this.location = value;
	}

	public AccessConfiguration getAccessConfiguration() {
		return accessConfiguration;
	}

	public void setAccessConfiguration(AccessConfiguration value) {
		this.accessConfiguration = value;
	}

	public List<UserAccount> getUserAccounts() {
		if (userAccounts == null) {
			userAccounts = new ArrayList<UserAccount>();
		}
		return this.userAccounts;
	}

	public List<PhysicalInterface> getPhysicalInterfaces() {
		if (physicalInterfaces == null) {
			physicalInterfaces = new ArrayList<PhysicalInterface>();
		}
		return this.physicalInterfaces;
	}

	public List<RouterModel> getChildren() {
		if (children == null) {
			children = new ArrayList<RouterModel>();
		}
		return this.children;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String value) {
		this.parent = value;
	}

}
