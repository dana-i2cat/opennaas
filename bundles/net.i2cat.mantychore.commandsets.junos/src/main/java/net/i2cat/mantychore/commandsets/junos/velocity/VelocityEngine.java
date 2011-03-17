package net.i2cat.mantychore.commandsets.junos.velocity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VelocityEngine {

	public static final String	PARAMS			= "param";

	Logger						log				= LoggerFactory
																	.getLogger(VelocityEngine.class);

	private static String		VELOCITY_PROPS	= "/velocity.properties";
	private VelocityContext		ctx;
	private String				template;
	private Object				params;

	public VelocityEngine(String template, Object params) {
		this.template = template;
		this.params = params;
	}

	private void init() throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getResourceAsStream(VELOCITY_PROPS));
		
		
		Velocity.init(prop);

		ctx = new VelocityContext();
		
		
	}

	/*
	 * It must exist other method to implements this method to get resources
	 * It was used to get the velocity template from a jar. In old versions, it was problems to use 
	 */
	@Deprecated
	private void addJarProperties(Properties prop) {
		Properties oldProps = (Properties)prop.clone();
		prop.setProperty("resource.loader", "jar");
		prop.setProperty("jar.resource.loader.class","org.apache.velocity.runtime.resource.loader.JarResourceLoader");
		prop.setProperty("jar.resource.loader.cache", "true");
		String absolutPath = "";
		try {
			absolutPath = "jar:file:" +	(new File (".")).getCanonicalPath();
		} catch (IOException e) {
			log.error("It was impossible to get the canonical path");
			//Restore propeties file
			prop = oldProps;
			return;
		}
		log.info("absoluthPath="+absolutPath);
		prop.setProperty("jar.resource.loader.path", absolutPath +"/bundles/net.i2cat.mantychore.commandsets.junos_1.0.0.SNAPSHOT.jar");
		//where there are templates
		prop.setProperty("template.root","VM_files");
		
	}


	
	
	public String mergeTemplate() throws ResourceNotFoundException,
			ParseErrorException, Exception {
		init();
		String currentPath = (new File (".")).getCanonicalPath();
    	System.out.println(currentPath);
    	log.info(currentPath);
		
		Template tpl = Velocity.getTemplate(template);

		ctx.put(PARAMS, params);

		Writer writer = new StringWriter();

		tpl.merge(ctx, writer);

		return writer.toString();
	}

}
