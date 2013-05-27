package org.opennaas.core.resources.helpers;

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
