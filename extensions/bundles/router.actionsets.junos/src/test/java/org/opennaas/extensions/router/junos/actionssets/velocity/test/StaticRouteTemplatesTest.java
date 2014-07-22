/**
 * 
 */
package org.opennaas.extensions.router.junos.actionssets.velocity.test;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;
import org.opennaas.extensions.router.capabilities.api.model.staticroute.StaticRoute;
import org.opennaas.extensions.router.capability.staticroute.StaticRouteCapability;
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

		StaticRoute staticRoute = getIPv4StaticRoute();

		extraParams.put("nextHopSet", staticRoute.getNextHopIpAddress() != null && !staticRoute.getNextHopIpAddress().isEmpty());
		extraParams.put("discardSet", staticRoute.isDiscard());
		extraParams.put("preferenceSet",
				staticRoute.getPreference() != null && staticRoute.getPreference() != StaticRouteCapability.PREFERENCE_DEFAULT_VALUE);

		String expected = textFileToString("/actions/createStaticRoutev4.xml");
		String message = callVelocity(template, staticRoute, extraParams);

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

		StaticRoute staticRoute = getIPv6StaticRoute();

		extraParams.put("nextHopSet", staticRoute.getNextHopIpAddress() != null && !staticRoute.getNextHopIpAddress().isEmpty());
		extraParams.put("discardSet", staticRoute.isDiscard());
		extraParams.put("preferenceSet",
				staticRoute.getPreference() != null && staticRoute.getPreference() != StaticRouteCapability.PREFERENCE_DEFAULT_VALUE);

		String expected = textFileToString("/actions/createStaticRoutev6.xml");
		String message = callVelocity(template, staticRoute, extraParams);

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

	private StaticRoute getIPv4StaticRoute() {
		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setNetIdIpAdress("0.0.0.0/0");
		staticRoute.setNextHopIpAddress("192.168.1.1");
		staticRoute.setDiscard(false);
		return staticRoute;
	}

	private StaticRoute getIPv6StaticRoute() {
		StaticRoute staticRoute = new StaticRoute();
		staticRoute.setNetIdIpAdress("43:256:F1::13:A/0");
		staticRoute.setNextHopIpAddress("FDEC:45::B3");
		staticRoute.setDiscard(false);
		return staticRoute;
	}

}
