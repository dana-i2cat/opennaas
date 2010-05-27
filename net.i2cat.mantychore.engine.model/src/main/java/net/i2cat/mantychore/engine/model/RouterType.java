package net.i2cat.mantychore.engine.model;


import java.util.ArrayList;
import java.util.List;


public class RouterType {

    protected String routerName;
    protected String hostName;
    protected String routerModel;
    protected String versionOS;
    protected String logicalName;
    protected boolean isOperation;
    protected boolean allowsRouterInstanceCreation;
    protected boolean isPhysical;
    protected String routerIDs;
    protected LocationType location;
    protected AccessConfigurationType accessConfiguration;
    protected List<UserAccountType> userAccounts;
    protected List<PhysicalInterfaceType> physicalInterfaces;
    protected List<RouterType> children;
    protected String parent;


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


    public LocationType getLocation() {
        return location;
    }


    public void setLocation(LocationType value) {
        this.location = value;
    }


    public AccessConfigurationType getAccessConfiguration() {
        return accessConfiguration;
    }


    public void setAccessConfiguration(AccessConfigurationType value) {
        this.accessConfiguration = value;
    }


    public List<UserAccountType> getUserAccounts() {
        if (userAccounts == null) {
            userAccounts = new ArrayList<UserAccountType>();
        }
        return this.userAccounts;
    }


    public List<PhysicalInterfaceType> getPhysicalInterfaces() {
        if (physicalInterfaces == null) {
            physicalInterfaces = new ArrayList<PhysicalInterfaceType>();
        }
        return this.physicalInterfaces;
    }


    public List<RouterType> getChildren() {
        if (children == null) {
            children = new ArrayList<RouterType>();
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
