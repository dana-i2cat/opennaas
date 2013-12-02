package org.opennaas.core.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;

public class GenericListWrapper<T> {

	private List<T>	items;

	public GenericListWrapper() {
		items = new ArrayList<T>();
	}

	public GenericListWrapper(List<T> items) {
		this.items = items;
	}

	@XmlAnyElement(lax = true)
	public List<T> getItems() {
		return items;
	}

}
