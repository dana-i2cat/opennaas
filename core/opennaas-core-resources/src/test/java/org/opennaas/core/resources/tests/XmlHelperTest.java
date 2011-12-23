package org.opennaas.core.resources.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;

public class XmlHelperTest {

	@Test
	public void formatXMLTest() {

		List<String> toFormat = new ArrayList<String>();

		toFormat.add("<!-- Document comment --><aaa><bbb/><ccc/></aaa>");
		toFormat.add("<!-- Document comment --><aaa    >        <bbb       /><ccc/></aaa>");

		for (String unformatted : toFormat) {
			System.out.println("Unformated:\n" + unformatted);
			System.out.println("Formatted:\n" + formatString(unformatted));
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
