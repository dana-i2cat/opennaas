/**
 * 
 */
package org.opennaas.extensions.queuemanager;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jordi
 * @param <E>
 */
@XmlRootElement
public class Response {

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
