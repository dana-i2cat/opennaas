package org.opennaas.core.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GenericListWrapper<T> {

	@XmlAnyElement(lax = true)
	private List<T>	items;

	public GenericListWrapper() {
		items = new ArrayList<T>();
	}

	public GenericListWrapper(List<T> items) {
		this.items = items;
	}

	public List<T> getItems() {
		return items;
	}

}
