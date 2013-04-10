package org.opennaas.extensions.router.capability.ospfv3.shell;

import java.io.IOException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospfv3.IOSPFv3Capability;
import org.opennaas.extensions.router.model.EnabledLogicalElement.EnabledState;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.OSPFAreaConfiguration;
import org.opennaas.extensions.router.model.OSPFProtocolEndpointBase;
import org.opennaas.extensions.router.model.OSPFService;
import org.opennaas.extensions.router.model.utils.ModelHelper;

@Command(scope = "ospfv3", name = "show", description = "Shows full OSPFv3 configuration.")
public class ShowCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String	resourceId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Show OSPF configuration");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);
			IOSPFv3Capability ospfCapability = (IOSPFv3Capability) router.getCapabilityByInterface(IOSPFv3Capability.class);
			OSPFService ospfService = ospfCapability.showOSPFv3Configuration();
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