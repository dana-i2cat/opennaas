package org.opennaas.extensions.router.capability.ospf.shell;

import java.io.IOException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "show", description = "Shows full OSPF configuration.")
public class ShowCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to activate OSPF on", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Show OSPF configuration");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);
			OSPFService ospfService = ospfCapability.showOSPFConfiguration();
			printOSPFConfiguration(ospfService);
			return null;
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error showing OSPF configuration");
			printError(e);
			printEndCommand();
			return -1;
		}
	}

	private void printOSPFConfiguration(OSPFService ospfService) throws IOException {
		if (ospfService == null) {
			printSymbol("OSPF not configured");
			return;
		}

		if (ospfService.getEnabledState().equals(EnabledState.ENABLED))
			printSymbol("Status: ENABLED");
		else
			printSymbol("Status: DISABLED");

		for (OSPFAreaConfiguration areaConfig : ospfService.getOSPFAreaConfiguration()) {
			OSPFArea area = areaConfig.getOSPFArea();

			printSymbol("Area " + ModelHelper.ipv4LongToString(area.getAreaID()) + ":");
			printSymbol("\tInterfaces:");
			String status;
			for (OSPFProtocolEndpointBase pep : area.getEndpointsInArea()) {
				if (pep.getEnabledState().equals(EnabledState.ENABLED)) {
					status = "ENABLED";
				} else {
					status = "DISABLED";
				}
				printSymbol("\t" + pep.getName() + " " + status);
			}
		}
	}

}