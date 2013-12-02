package org.opennaas.extensions.router.junos.commandsets.velocity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

public class VelocityEngine {

	private final Map<String, Object>	extraParams		= new HashMap<String, Object>();

	public static final String			PARAM_CONSTANT	= "param";

	Log									log				= LogFactory
																.getLog(VelocityEngine.class);

	private static String				VELOCITY_PROPS	= "/velocity.properties";

	private String						template;

	private Object						param;

	private void init() throws Exception {
		InputStream velocityPropFile = getClass().getResourceAsStream(VELOCITY_PROPS);

		if (velocityPropFile == null)
			throw new ResourceNotFoundException("Cannot load: " + VELOCITY_PROPS);

		Properties prop = new Properties();
		prop.load(velocityPropFile);

		Velocity.init(prop);

	}

	public String mergeTemplate() throws ResourceNotFoundException, ParseErrorException, Exception {
		init();
		String currentPath = (new File(".")).getCanonicalPath();
		log.info("Current directory to get template: " + currentPath);

		Template tpl = Velocity.getTemplate(template);

		VelocityContext ctx = new VelocityContext();

		// //TODO FIX PROBLEMS TO PARAMS IN NULL
		ctx.put(PARAM_CONSTANT, param);

		for (String key : extraParams.keySet())
			ctx.put(key, extraParams.get(key));

		Writer writer = new StringWriter();

		tpl.merge(ctx, writer);

		return writer.toString();
	}

	public void addExtraParam(String name, Object newParam) {
		this.extraParams.put(name, newParam);
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	/*
	 * It must exist other method to implements this method to get resources It was used to get the velocity template from a jar. In old versions, it
	 * gave troubles to use
	 */
	@Deprecated
	private void addJarProperties(Properties prop) {
		Properties oldProps = (Properties) prop.clone();
		prop.setProperty("resource.loader", "jar");
		prop.setProperty("jar.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.JarResourceLoader");
		prop.setProperty("jar.resource.loader.cache", "true");
		String absolutPath = "";
		try {
			absolutPath = "jar:file:" + (new File(".")).getCanonicalPath();
		} catch (IOException e) {
			log.error("It was impossible to get the canonical path");
			// Restore propeties file
			prop = oldProps;
			return;
		}
		log.info("absoluthPath=" + absolutPath);
		prop.setProperty(
				"jar.resource.loader.path",
				absolutPath
						+ "/bundles/net.i2cat.mantychore.commandsets.junos_1.0.0.SNAPSHOT.jar");
		// where there are templates
		prop.setProperty("template.root", "VM_files");

	}

}
