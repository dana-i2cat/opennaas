package org.opennaas.extensions.bod.actionsets.dummy.actions;

public class LinkIdProvider {

	private int						count	= 0;
	private static LinkIdProvider	instance;

	public static LinkIdProvider getInstance() {
		if (instance == null)
			instance = new LinkIdProvider();
		return instance;
	}

	public int getNewId() {
		return count++;
	}

}
