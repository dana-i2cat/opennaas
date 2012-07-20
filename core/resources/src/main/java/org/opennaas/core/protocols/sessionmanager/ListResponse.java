/**
 * 
 */
package org.opennaas.core.protocols.sessionmanager;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jordi
 */
@XmlRootElement
public class ListResponse {

	private List<?>	list;

	/**
	 * @return the list
	 */
	public List<?> getList() {
		return list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List<?> list) {
		this.list = list;
	}

}
