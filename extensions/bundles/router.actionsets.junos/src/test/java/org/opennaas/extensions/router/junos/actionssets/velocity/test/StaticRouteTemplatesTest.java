/**
 * 
 */
package org.opennaas.extensions.router.junos.actionssets.velocity.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;

/**
 * @author Jordi
 * 
 */
public class StaticRouteTemplatesTest extends VelocityTemplatesTest {

	/**
	 * 
	 */
	private final Log	log	= LogFactory.getLog(StaticRouteTemplatesTest.class);

	@Test
	public void testCreateLogicalRouterTemplate() throws Exception {

		template = "/VM_files/createStaticRoute.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());
		extraParams.put("elementName", "");

		String expected = textFileToString("/actions/createStaticRoutev4.xml");
		String message = callVelocity(template, getParams(), extraParams);

		Assert.assertEquals(XmlHelper.formatXML(expected), XmlHelper.formatXML(message));

		log.info(XmlHelper.formatXML(message));
	}

	@Test
	public void testCreateLogicalRouterv6Template() throws Exception {

		template = "/VM_files/createStaticRoutev6.vm";

		Map<String, Object> extraParams = new HashMap<String, Object>();
		extraParams.put("disabledState", EnabledState.DISABLED.toString());
		extraParams.put("enabledState", EnabledState.ENABLED.toString());
		extraParams.put("elementName", "");

		String expected = textFileToString("/actions/createStaticRoutev6.xml");
		String message = callVelocity(template, getParamsv6(), extraParams);

		Assert.assertEquals(XmlHelper.formatXML(expected), XmlHelper.formatXML(message));

		log.info(XmlHelper.formatXML(message));
	}

	private String textFileToString(String fileLocation) throws IOException {
		String fileString = "";
		BufferedReader br = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream(fileLocation)));
		String line;
		while ((line = br.readLine()) != null) {
			fileString += line;
		}
		br.close();
		return fileString;
	}

	/**
	 * @return string array with params
	 */
	private String[] getParams() {
		String[] params = new String[3];
		params[0] = "0.0.0.0/0";
		params[1] = "192.168.1.1";
		params[2] = "false";
		return params;
	}

	private String[] getParamsv6() {
		String[] params = new String[3];
		params[0] = "43:256:F1::13:A/0";
		params[1] = "FDEC:45::B3";
		params[2] = "false";
		return params;
	}

}
