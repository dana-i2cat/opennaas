package net.i2cat.mantychore.commandsets.junos.digester;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.ManagedElement;
import net.i2cat.mantychore.model.ManagedSystemElement;
import net.i2cat.mantychore.model.NextHopIPRoute;

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

			DigesterEngine physicalInterfParser = new PhysicalInterfaceParser();
			physicalInterfParser.init();
			physicalInterfParser.configurableParse(interfaceConfig);
			HashMap<String, Object> interfs = physicalInterfParser.getMapElements();

			// /* Parse logical interface info */
			// DigesterEngine logicalInterfParser = new ListLogicalRouterParser();
			// logicalInterfParser.init();
			// logicalInterfParser.setMapElements(interfs);
			// logicalInterfParser.configurableParse(rulesIPConfiguration);

			DigesterEngine routingOptionsParser = new RoutingOptionsParser();
			routingOptionsParser.init();
			routingOptionsParser.configurableParse(rulesIPConfiguration);
			HashMap<String, Object> nextHop = routingOptionsParser.getMapElements();

			net.i2cat.mantychore.model.System routerModel = new net.i2cat.mantychore.model.System();

			// add to the router model
			for (String keyInterf : interfs.keySet()) {
				EthernetPort eP = (EthernetPort) interfs.get(keyInterf);
				eP.setElementName("EthernetPort");
				routerModel.addManagedSystemElement(eP);
			}
			for (String key : nextHop.keySet()) {
				NextHopIPRoute nh = (NextHopIPRoute) nextHop.get(key);
				nh.setElementName("NextHopIPRoute");
				routerModel.addManagedElement(nh);
			}

			// TEsting the parser
			String str = "";
			// TODO not all the managed elements in the list will be NextHopeIPRoute
			// needed to identify
			List<ManagedElement> elements = routerModel.getManagedElements();
			List<ManagedSystemElement> sysElement = routerModel.getManagedSystemElements();
			for (ManagedElement managedElement : elements) {
				if (managedElement.getElementName().equalsIgnoreCase("NextHopIPRoute")) {
					NextHopIPRoute port = (NextHopIPRoute) managedElement;
					str += "- Routing options " + '\n';
					str += "IP adress " + port.getDestinationAddress() + '\n';
					str += "Mask " + port.getDestinationMask() + '\n';
					str += "is Static " + String.valueOf(port.isIsStatic()) + '\n';
					str += "Next hop " + port.getRouteUsesEndPoint().getIPv4Address() + '\n';
				}

			}
			for (ManagedSystemElement managedSystemElement : sysElement) {
				if (managedSystemElement.getElementName().equalsIgnoreCase("EthernetPort")) {

					EthernetPort port = (EthernetPort) managedSystemElement;
					str += "- EthernetPort: " + '\n';
					str += port.getOtherPortType() + '\n';
					str += port.getPermanentAddress() + '\n';
					str += String.valueOf(port.isFullDuplex()) + '\n';
					str += String.valueOf(port.getMaxSpeed()) + '\n';
					str += port.getDescription() + '\n';

				}
			}
			System.out.println(str);

			// PhysicalInterfaceParser engineParser = new
			// PhysicalInterfaceParser();
			// RoutingOptionsParser engineParser = new
			// RoutingOptionsParser();
			// // LogicalInterfaceParser engineParser = new
			// // LogicalInterfaceParser();
			// // ListLogicalRouterParser engineParser = new ListLogicalRouterParser();
			//
			// engineParser.init();
			//
			// // PhysicalInterfaceParser engineParser = new
			// // PhysicalInterfaceParser();
			// // engineParser.init();
			// //
			// // /* get physical interfaces information */
			// // engineParser.parse(interfaceConfig);
			// // System.out.println(engineParser.toPrint());
			//
			// /* get physical interfaces information */
			// // System.out.println(logicalEngineParser.toPrint());
			//
			// engineParser.configurableParse(rulesIPConfiguration);
			//
			// System.out.println(engineParser.toPrint());

		} catch (IOException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}

	}
}
