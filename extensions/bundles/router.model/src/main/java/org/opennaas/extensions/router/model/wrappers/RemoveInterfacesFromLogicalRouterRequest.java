/**
 * 
 */
package org.opennaas.extensions.router.model.wrappers;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.LogicalPort;

/**
 * @author Jordi
 */
@XmlRootElement
public class RemoveInterfacesFromLogicalRouterRequest {

	private ComputerSystem		logicalRouter;
	private List<LogicalPort>	interfaces;

	/**
	 * @return the logicalRouter
	 */
	public ComputerSystem getLogicalRouter() {
		return logicalRouter;
	}

	/**
	 * @param logicalRouter
	 *            the logicalRouter to set
	 */
	public void setLogicalRouter(ComputerSystem logicalRouter) {
		this.logicalRouter = logicalRouter;
	}

	/**
	 * @return the interfaces
	 */
	public List<LogicalPort> getInterfaces() {
		return interfaces;
	}

	/**
	 * @param interfaces
	 *            the interfaces to set
	 */
	public void setInterfaces(List<LogicalPort> interfaces) {
		this.interfaces = interfaces;
	}

}
