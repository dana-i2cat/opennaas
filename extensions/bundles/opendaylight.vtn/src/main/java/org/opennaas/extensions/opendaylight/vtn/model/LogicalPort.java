package org.opennaas.extensions.opendaylight.vtn.model;

/**
 *
 * @author Josep Batallé <josep.batalle@i2cat.net>
 */
public class LogicalPort {

    private String port_name;
    private String operdown_criteria;
    private String logical_port_id;
    private String oper_status;
    private String member_ports;
    private String type;
    private String switch_id;

    public String getPort_name() {
        return port_name;
    }

    public void setPort_name(String port_name) {
        this.port_name = port_name;
    }

    public String getOperdown_criteria() {
        return operdown_criteria;
    }

    public void setOperdown_criteria(String operdown_criteria) {
        this.operdown_criteria = operdown_criteria;
    }

    public String getLogical_port_id() {
        return logical_port_id;
    }

    public void setLogical_port_id(String logical_port_id) {
        this.logical_port_id = logical_port_id;
    }

    public String getOper_status() {
        return oper_status;
    }

    public void setOper_status(String oper_status) {
        this.oper_status = oper_status;
    }

    public String getMember_ports() {
        return member_ports;
    }

    public void setMember_ports(String member_ports) {
        this.member_ports = member_ports;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSwitch_id() {
        return switch_id;
    }

    public void setSwitch_id(String switch_id) {
        this.switch_id = switch_id;
    }

}
