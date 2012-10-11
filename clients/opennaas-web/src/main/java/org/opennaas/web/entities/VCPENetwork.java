package org.opennaas.web.entities;

/**
 * @author Jordi
 */
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class VCPENetwork {

	private String			id;

	@NotNull
	@Size(min = 1, max = 25)
	private String			name;

	@NotNull
	@Size(min = 1, max = 25)
	private String			template;

	private LogicalRouter	logicalRouter1;

	private LogicalRouter	logicalRouter2;

	/**
	 * 
	 */
	public VCPENetwork() {
		logicalRouter1 = new LogicalRouter();
		List<Interface> interfaces = new ArrayList<Interface>();

		Interface interface1 = new Interface();
		Interface interface2 = new Interface();
		Interface interface3 = new Interface();

		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);

		interface1.setName("fe-0/3/2");
		interface1.setPort("1");
		interface1.setVlan(1);
		interface1.setLabelName("Inter Interface");

		interface2.setName("ge-0/2/0");
		interface2.setPort("1");
		interface2.setVlan(1);
		interface2.setLabelName("Down Interface");

		interface3.setName("lt-0/1/2");
		interface3.setPort("1");
		interface3.setLabelName("Up Interface");

		logicalRouter1.setInterfaces(interfaces);

		logicalRouter2 = new LogicalRouter();
		interfaces = new ArrayList<Interface>();

		interface1 = new Interface();
		interface2 = new Interface();
		interface3 = new Interface();

		interfaces.add(interface1);
		interfaces.add(interface2);
		interfaces.add(interface3);

		interface1.setName("fe-0/3/3");
		interface1.setPort("1");
		interface1.setVlan(1);
		interface1.setLabelName("Inter Interface");

		interface2.setName("ge-0/2/0");
		interface2.setPort("2");
		interface2.setVlan(2);
		interface2.setLabelName("Down Interface");

		interface3.setName("lt-0/1/2");
		interface3.setPort("2");
		interface3.setLabelName("Up Interface");

		logicalRouter2.setInterfaces(interfaces);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}

	/**
	 * @return the logicalRouter1
	 */
	public LogicalRouter getLogicalRouter1() {
		return logicalRouter1;
	}

	/**
	 * @param logicalRouter1
	 *            the logicalRouter1 to set
	 */
	public void setLogicalRouter1(LogicalRouter logicalRouter1) {
		this.logicalRouter1 = logicalRouter1;
	}

	/**
	 * @return the logicalRouter2
	 */
	public LogicalRouter getLogicalRouter2() {
		return logicalRouter2;
	}

	/**
	 * @param logicalRouter2
	 *            the logicalRouter2 to set
	 */
	public void setLogicalRouter2(LogicalRouter logicalRouter2) {
		this.logicalRouter2 = logicalRouter2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VCPENetwork [id=" + id + ", name=" + name + ", template=" + template + ", logicalRouter1=" + logicalRouter1 + ", logicalRouter2=" + logicalRouter2 + "]";
	}

}
