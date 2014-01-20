package org.opennaas.extensions.ofertie.ncl.provisioner.components.mockup.routing;

/*
 * #%L
 * OpenNaaS :: OFERTIE :: NCL components
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.ObjectSerializer;
import org.opennaas.core.resources.SerializationException;

public abstract class RouteSelectionMapLoader {

	private static Log	LOG	= LogFactory.getLog(RouteSelectionMapLoader.class);

	public static RouteSelectionMap getRouteSelectionMapFromXml(String xmlWithMap) throws SerializationException {

		LOG.debug("Parsing RouteSelectionMap from xml.");

		RouteSelectionMap map = (RouteSelectionMap) ObjectSerializer.fromXml(xmlWithMap, RouteSelectionMap.class);

		LOG.debug("RouteSelectionMap parsed from xml.");

		return map;
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
