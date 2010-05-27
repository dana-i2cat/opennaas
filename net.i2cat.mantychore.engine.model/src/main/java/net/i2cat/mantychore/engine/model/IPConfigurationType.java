package net.i2cat.mantychore.engine.model;




import java.util.ArrayList;
import java.util.List;



public class IPConfigurationType {

    protected String ipSubNetwork;
    protected String broadcast;
    protected String fastSwitching;
    protected String nat;
    protected List<String> rule;
    protected String ipv4Address;
    protected String ipv4Mask;
    protected String ipv6Address;
    protected String ipv6Prefix;
    protected String ipVersion;


    public String getIpSubNetwork() {
        return ipSubNetwork;
    }


    public void setIpSubNetwork(String value) {
        this.ipSubNetwork = value;
    }

    public String getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(String value) {
        this.broadcast = value;
    }


    public String getFastSwitching() {
        return fastSwitching;
    }


    public void setFastSwitching(String value) {
        this.fastSwitching = value;
    }


    public String getNat() {
        return nat;
    }


    public void setNat(String value) {
        this.nat = value;
    }


    public List<String> getRule() {
        if (rule == null) {
            rule = new ArrayList<String>();
        }
        return this.rule;
    }


    public String getIpv4Address() {
        return ipv4Address;
    }


    public void setIpv4Address(String value) {
        this.ipv4Address = value;
    }


    public String getIpv4Mask() {
        return ipv4Mask;
    }


    public void setIpv4Mask(String value) {
        this.ipv4Mask = value;
    }


    public String getIpv6Address() {
        return ipv6Address;
    }


    public void setIpv6Address(String value) {
        this.ipv6Address = value;
    }


    public String getIpv6Prefix() {
        return ipv6Prefix;
    }


    public void setIpv6Prefix(String value) {
        this.ipv6Prefix = value;
    }


    public String getIpVersion() {
        return ipVersion;
    }


    public void setIpVersion(String value) {
        this.ipVersion = value;
    }

}
