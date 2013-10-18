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

import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;
import org.opennaas.extensions.sdnnetwork.model.Route;

public abstract class PathLoader {

	public static Map<String, Route> loadPathsFromXML(String fileURI) throws IOException, SerializationException {

		Map<String, Route> finalPaths = new HashMap<String, Route>();

		String xmlWithPaths = loadPathsFromXMLFile(fileURI);

		List<Route> routes = ObjectSerializer.fromXML(xmlWithPaths, Route.class);

		for (Route route : routes) {
			finalPaths.put(route.getId(), route);
		}

		return finalPaths;

	}

	private static String loadPathsFromXMLFile(String filePath) throws IOException {

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

		return w.toString();
	}
}
