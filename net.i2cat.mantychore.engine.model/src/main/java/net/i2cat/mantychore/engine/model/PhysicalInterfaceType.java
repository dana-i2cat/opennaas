package net.i2cat.mantychore.engine.model;


import java.util.ArrayList;
import java.util.List;



public class PhysicalInterfaceType {

    protected String status;
    protected String linkStatus;
    protected int keepalive;
    protected String macAddress;
    protected String type;
    protected String location;
    protected String linkMode;
    protected List<SubInterfaceType> subInterfaces;
    protected PhysicalInterfaceType physicalInterfaceParent;


    public String getStatus() {
        return status;
    }


    public void setStatus(String value) {
        this.status = value;
    }


    public String getLinkStatus() {
        return linkStatus;
    }


    public void setLinkStatus(String value) {
        this.linkStatus = value;
    }


    public int getKeepalive() {
        return keepalive;
    }


    public void setKeepalive(int value) {
        this.keepalive = value;
    }


    public String getMacAddress() {
        return macAddress;
    }


    public void setMacAddress(String value) {
        this.macAddress = value;
    }


    public String getType() {
        return type;
    }


    public void setType(String value) {
        this.type = value;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String value) {
        this.location = value;
    }


    public String getLinkMode() {
        return linkMode;
    }


    public void setLinkMode(String value) {
        this.linkMode = value;
    }


    public List<SubInterfaceType> getSubInterfaces() {
        if (subInterfaces == null) {
            subInterfaces = new ArrayList<SubInterfaceType>();
        }
        return this.subInterfaces;
    }


    public PhysicalInterfaceType getPhysicalInterfaceParent() {
        return physicalInterfaceParent;
    }


    public void setPhysicalInterfaceParent(PhysicalInterfaceType value) {
        this.physicalInterfaceParent = value;
    }

}
