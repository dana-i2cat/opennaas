/**
 * 
 */
package org.opennaas.gui.vcpe.entities;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Jordi
 */
public class LogicalRouter {

	@NotNull
	@Size(min = 1, max = 25)
	private String			name;

	private String			templateName;

	private List<Interface>	interfaces;

	/**
	 * 
	 */
	public LogicalRouter() {
		interfaces = new ArrayList<Interface>();
		Interface interface1 = new Interface();
		interfaces.add(interface1);
		Interface interface2 = new Interface();
		interfaces.add(interface2);
		Interface interface3 = new Interface();
		interfaces.add(interface3);
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
	 * @return the interfaces
	 */
	public List<Interface> getInterfaces() {
		return interfaces;
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName
	 *            the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

}
