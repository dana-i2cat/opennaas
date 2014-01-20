package org.opennaas.core.resources.helpers;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

public class XmlHelper {

	public static String formatXML(String unformatted) throws SAXException, IOException, TransformerException, ParserConfigurationException {

		if (unformatted == null)
			return null;

		// remove whitespaces between xml tags
		String unformattedNoWhiteSpaces = unformatted.toString().replaceAll(">[\\s]+<", "><");

		// Instantiate transformer input
		Source xmlInput = new StreamSource(new StringReader(unformattedNoWhiteSpaces));
		StreamResult xmlOutput = new StreamResult(new StringWriter());

		// Configure transformer
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

		transformer.transform(xmlInput, xmlOutput);
		String formatted = xmlOutput.getWriter().toString();

		return formatted;
	}

	/**
	 * Compares two XML String applying format
	 * 
	 * @param xmlString1
	 *            first XML String to compare
	 * @param xmlString2
	 *            second XML String to compare
	 * @return true if both XML formatted Strings are equal, false otherwise
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SAXException
	 * 
	 * @author Julio Carlos Barrera
	 */
	public static boolean compareXMLStrings(String xmlString1, String xmlString2) throws SAXException, IOException, TransformerException,
			ParserConfigurationException {
		String formattedXMLString1 = XmlHelper.formatXML(xmlString1);
		String formattedXMLString2 = XmlHelper.formatXML(xmlString2);
		return formattedXMLString2.equals(formattedXMLString1);
	}
}
