package net.i2cat.mantychore.commandsets.junos.digester;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class CommandTestDummy {

	private Log		log							= LogFactory.getLog(CommandTestDummy.class);

	private String	path						= "src" + File.separator
														+ "main" + File.separator
														+ "resources";

	private String	getConfig					= path + File.separator + "getconfiguration.xml";

	private String	interfaceConfig				= path + File.separator + "getinterfaceinformation.xml";

	private String	logicalRoutersConfig		= path + File.separator + "LRconfiguration.xml";

	private String	rulesIPConfiguration		= path + File.separator + "ipconfiguration.xml";
	private String	rulesChassisConfiguration	= "chassisconfiguration.xml";
	private String	rulesRouterConfiguration	= "iprouterconfiguration.xml";
	private String	rulesPoliciesConfiguration	= "policiesconfiguration.xml";

	public CommandTestDummy() {

	}

	@Before
	public void init() {

	}

	@Test
	public void firstTestModel() {
		try {
			// PhysicalInterfaceParser engineParser = new
			// PhysicalInterfaceParser();
			// engineParser.init();
			//
			// /* get physical interfaces information */
			// engineParser.parse(interfaceConfig);
			// System.out.println(engineParser.toPrint());

			LogicalInterfaceParser logicalEngineParser = new
					LogicalInterfaceParser();
			logicalEngineParser.init();
			logicalEngineParser.configurableParse(getConfig);

		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}

	}

}
