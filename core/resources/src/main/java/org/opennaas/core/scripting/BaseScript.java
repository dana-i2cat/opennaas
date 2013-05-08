package org.opennaas.core.scripting;

/**
 * This is the base class for all scripts launched with IScriptingManager (no need to declare it, it is injected at launch time). Properties and
 * methods in this class will be automatically made available to all scripts.
 */
public class BaseScript extends groovy.lang.Script {

	public void karaf(String cmd) {
		// get CommandProcessor, launch command to shell, return output, etc
	}

	@Override
	public Object run() {
		// not executed, make class abstract?
		return null;
	}

}
