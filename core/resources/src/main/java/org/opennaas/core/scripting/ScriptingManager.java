package org.opennaas.core.scripting;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.opennaas.core.persistence.Activator;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.configurationadmin.ConfigurationAdminUtil;
import org.osgi.framework.BundleContext;

public class ScriptingManager implements IScriptingManager {

	Log							log		= LogFactory.getLog(ScriptingManager.class);

	private IResourceManager	resourceManager;
	BundleContext				bundleContext;
	ConfigurationAdminUtil		configuration;

	GroovyScriptEngine			gse;
	CompilerConfiguration		compiler;
	Binding						defaultBinding;

	List<String>				roots	= new ArrayList<String>();

	public ScriptingManager() {

		log.info("Loading ScriptManager");

		compiler = new CompilerConfiguration();

		defaultBinding = new Binding();

		bundleContext = Activator.getBundleContext();

		configuration = new ConfigurationAdminUtil(bundleContext);
	}

	public void init() {
		log.info("Loading ScriptManager");

		try {
			String scriptingDir = configuration.getProperty("org.opennaas", "scripting.dir");

			if (scriptingDir != null && !scriptingDir.contentEquals("")) {
				roots.add(scriptingDir);
			}

		} catch (IOException e) {
			log.error(e);
		}

		try {
			for (String root : roots)
				log.info("\tScriptingManager root: " + root);

			gse = new GroovyScriptEngine(roots.toArray(new String[roots.size()]));

		} catch (IOException e) {
			e.printStackTrace();
		}

		compiler.setScriptBaseClass(BaseScript.class.getCanonicalName());
		gse.setConfig(compiler);

		defaultBinding = new Binding();
	}

	public IResourceManager getResourceManager() {
		return resourceManager;
	}

	public void setResourceManager(IResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void destroy() {
		log = null;
		resourceManager = null;
		gse = null;
		compiler = null;
		defaultBinding = null;
		roots = null;
	}

	@Override
	public Object runScript(String scriptName) throws ResourceException, ScriptException {
		return runScript(scriptName, defaultBinding);
	}

	@Override
	public Object runScript(String scriptName, Binding binding) throws ResourceException, ScriptException {

		for (Object key : defaultBinding.getVariables().keySet()) {
			binding.setVariable((String) key, defaultBinding.getVariable(key.toString()));
		}

		return gse.run(scriptName, defaultBinding);
	}
}
