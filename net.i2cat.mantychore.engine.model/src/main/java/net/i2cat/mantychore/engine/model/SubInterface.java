package net.i2cat.mantychore.engine.model;





public class SubInterface {


    protected String mtu;
    protected String description;
    protected String identifier;
    protected String speed;
    protected String encapsulation;
    protected String physicalInterfaceID;
    protected IPConfiguration iPconfiguration;
    protected String vlanID;
    protected String peerUnit;

    public String getMtu() {
        return mtu;
    }


    public void setMtu(String value) {
        this.mtu = value;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String value) {
        this.description = value;
    }


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String value) {
        this.identifier = value;
    }


    public String getSpeed() {
        return speed;
    }


    public void setSpeed(String value) {
        this.speed = value;
    }


    public String getEncapsulation() {
        return encapsulation;
    }


    public void setEncapsulation(String value) {
        this.encapsulation = value;
    }


    public String getPhysicalInterfaceID() {
        return physicalInterfaceID;
    }


    public void setPhysicalInterfaceID(String value) {
        this.physicalInterfaceID = value;
    }


    public IPConfiguration getIPconfiguration() {
        return iPconfiguration;
    }


    public void setIPconfiguration(IPConfiguration value) {
        this.iPconfiguration = value;
    }


    public String getVlanID() {
        return vlanID;
    }


    public void setVlanID(String value) {
        this.vlanID = value;
    }


    public String getPeerUnit() {
        return peerUnit;
    }


    public void setPeerUnit(String value) {
        this.peerUnit = value;
    }

}
