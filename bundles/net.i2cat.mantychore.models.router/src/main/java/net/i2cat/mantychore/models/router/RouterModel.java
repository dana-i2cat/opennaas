package net.i2cat.mantychore.models.router;

import java.util.ArrayList;
import java.util.List;

import com.iaasframework.capabilities.model.IResourceModel;
import com.iaasframework.resources.core.ObjectSerializer;

public class RouterModel implements IResourceModel {

	protected int						routerIDs;

	protected String					routerName;

	protected String					hostName;
	protected String					routerModel;
	protected String					versionOS;

	protected String					logicalName;

	protected boolean					isOperation;

	protected boolean					allowsRouterInstanceCreation;

	protected boolean					isPhysical;

	protected Location					location;

	/**
	 * URL formed string (protocol://address:port/etc). The transport bundle is
	 * in charge of interpreting it
	 */
	protected String					transportIdentifier;

	/**
	 * @deprecated transportIdentifier should be used instead.
	 */
	protected AccessConfiguration		accessConfiguration;

	protected List<UserAccount>			userAccounts;
	protected List<PhysicalInterface>	physicalInterfaces;
	protected List<RouterModel>			children;
	protected String					parent;

	/* routing parameters */
	protected List<StaticRoute>			staticRoutes;

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

	public int getRouterIDs() {
		return routerIDs;
	}

	public void setRouterIDs(int value) {
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

	public void addPhysicalInterface(PhysicalInterface physicalInterface) {
		for (PhysicalInterface buclePhyInterf : physicalInterfaces) {
			if (buclePhyInterf.getLocation().equals(physicalInterface.getLocation())) {
				return;
			}
		}
		// not exist
		physicalInterfaces.add(physicalInterface);
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

	/* routing options */

	public List<StaticRoute> getStaticRoutes() {
		return staticRoutes;
	}

	public void setStaticRoutes(List<StaticRoute> staticRoutes) {
		this.staticRoutes = staticRoutes;
	}

	public String toXML() {
		return ObjectSerializer.toXml(this);
	}

}
