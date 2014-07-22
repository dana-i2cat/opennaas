package org.opennaas.core.resources.tests;

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
