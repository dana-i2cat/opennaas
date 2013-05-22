package org.opennaas.core.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.security.persistence.SecurityRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Julio Carlos Barrera
 * 
 */
public class Activator implements BundleActivator {
	private static BundleContext	context;
	static Log						log	= LogFactory.getLog(Activator.class);

	private SecurityRepository		securityRepository;

	public static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		Activator.context = context;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

	public static BundleContext getBundleContext() {
		return context;
	}

	public static String getBundleTextFileContents(String path) {
		log.trace("Obtaining contents from resource '" + path + "':");

		URL url = context.getBundle().getEntry(path);

		if (url == null) {
			log.error("Resource not found: " + path);
			return null;
		}

		String contents = "";

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				contents += line + "\n";
			}
		} catch (IOException e) {
			log.error("Error getting contents from resource: " + url, e);
		}

		log.trace("Resource contents:\n" + contents);

		return contents;
	}
}
