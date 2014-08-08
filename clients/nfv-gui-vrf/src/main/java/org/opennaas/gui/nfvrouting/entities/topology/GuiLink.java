package org.opennaas.gui.nfvrouting.entities.topology;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiLink {

    private String id;
    private int source;
    private int target;
    private int srcPort;
    private int dstPort;
    private String type;

    public GuiLink(String id, int source, int target, int srcPort, int dstPort) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.type = "static";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
