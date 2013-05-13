package org.opennaas.core.scripting;

import groovy.lang.Binding;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

public interface IScriptingManager {

	public Object runScript(String scriptName) throws ResourceException, ScriptException;

	public Object runScript(String scriptName, Binding binding) throws ResourceException, ScriptException;
}
