/**
 * 
 */
package net.i2cat.mantychore.actionsets.junos.velocity.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.i2cat.mantychore.commandsets.junos.commons.IPUtilsHelper;
import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.opennaas.core.resources.helpers.XmlHelper;

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
		extraParams.put("ipUtilsHelper", IPUtilsHelper.class);

		String message = callVelocity(template, getParams(), extraParams);

		Assert.assertNotNull(message);
		Assert.assertTrue(message.contains("<routing-options>"));
		Assert.assertTrue(message.contains("<static>"));
		Assert.assertTrue(message.contains("<route>"));
		Assert.assertTrue(message.contains("<name>0.0.0.0/0</name>"));
		Assert.assertTrue(message.contains("<next-hop>192.168.1.1</next-hop>"));
		Assert.assertTrue(message.contains("</route>"));
		Assert.assertTrue(message.contains("</static>"));
		Assert.assertTrue(message.contains("</routing-options>"));

		log.info(XmlHelper.formatXML(message));
	}

	/**
	 * @return string array with params
	 */
	private String[] getParams() {
		String[] params = new String[3];
		params[0] = "0.0.0.0";
		params[1] = "0.0.0.0";
		params[2] = "192.168.1.1";
		return params;
	}

}
