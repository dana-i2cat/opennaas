package org.opennaas.gui.nfvrouting.entities.topology;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class GuiLink {

    private String source;
    private String target;
    private String srcPort;
    private String dstPort;

    public GuiLink(String source, String target, String srcPort, String dstPort) {
        this.source = source;
        this.target = target;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
    }

}
