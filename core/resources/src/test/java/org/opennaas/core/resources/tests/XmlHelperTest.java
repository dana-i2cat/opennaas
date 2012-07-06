package org.opennaas.core.resources.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;

public class XmlHelperTest {
	private final static String	FORMATTED_TEST	= "<!-- Document comment -->" + System.getProperty("line.separator")
														+ "<aaa>" + System.getProperty("line.separator")
														+ "  <bbb/>" + System.getProperty("line.separator")
														+ "  <ccc/>" + System.getProperty("line.separator")
														+ "</aaa>" + System.getProperty("line.separator");

	@Test
	public void formatXMLTest() {
		List<String> toFormat = new ArrayList<String>();

		toFormat.add("<!-- Document comment --><aaa><bbb/><ccc/></aaa>");
		toFormat.add("<!-- Document comment --><aaa    >        <bbb       /><ccc/></aaa>");

		for (String unformatted : toFormat) {
			String formatted = formatString(unformatted);
			Assert.assertEquals(FORMATTED_TEST, formatted);
		}

	}

	private String formatString(String unformatted) {
		String formatted = null;
		try {
			formatted = XmlHelper.formatXML(unformatted);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
		return formatted;
	}
}
