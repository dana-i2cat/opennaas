package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.sdnnetwork.model.Route;

public abstract class PathLoader {

	private static Log	LOG	= LogFactory.getLog(PathLoader.class);

	public static Map<String, Route> getRoutesFromXml(String xmlWithPaths) throws SerializationException {

		LOG.debug("Parsing routes from xml.");

		Map<String, Route> finalPaths = new HashMap<String, Route>();
		List<Route> routes = ObjectSerializer.fromXML(xmlWithPaths, Route.class);

		for (Route route : routes) {
			finalPaths.put(route.getId(), route);

		}

		LOG.debug(routes.size() + " routes parsed from xml.");

		return finalPaths;
	}

	public static String readXMLFile(String filePath) throws IOException {

		LOG.debug("Reading content of file " + filePath);

		InputStreamReader reader = null;
		BufferedReader bufferReader = null;
		try {
			URL url = new URL(filePath);
			reader = new InputStreamReader(url.openStream());
			bufferReader = new BufferedReader(reader);
		} catch (MalformedURLException ignore) {
			// They try a file
			File file = new File(filePath);
			bufferReader = new BufferedReader(new FileReader(file));
		}

		CharArrayWriter w = new CharArrayWriter();

		int n;
		char[] buf = new char[8192];
		while ((n = bufferReader.read(buf)) > 0) {
			w.write(buf, 0, n);
		}

		bufferReader.close();

		String fileContent = w.toString();

		LOG.debug("Readed content of file " + filePath);

		return fileContent;
	}
}
