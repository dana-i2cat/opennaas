package net.i2cat.mantychore.model.utils;

import java.util.ArrayList;
import java.util.List;

//FIXME THIS IMPLEMENTATION NEEDS TO OVERRIDE THE EQUALS METHOD
public class Associable<T, S> {

	List<T>	listElems	= new ArrayList<T>();

	public void addLink(T elem, S sourceElem, Associable<S, T> associable) {
		if (!listElems.contains(elem) && !associable.listElems.contains(sourceElem)) {
			listElems.add(elem);
			associable.listElems.add(sourceElem);
		}

	}

	public void removeLink(T elem, S sourceElem, Associable<S, T> associable) {
		if (listElems.contains(elem) && listElems.contains(sourceElem)) {
			listElems.remove(elem);
			associable.listElems.remove(sourceElem);
		}

	}

	public List<T> getLinks() {
		return listElems;
	}

}
