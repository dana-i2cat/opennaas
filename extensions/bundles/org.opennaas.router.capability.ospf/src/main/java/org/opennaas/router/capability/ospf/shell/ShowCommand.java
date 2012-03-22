package org.opennaas.router.capability.ospf.shell;

import java.io.IOException;

import net.i2cat.mantychore.model.EnabledLogicalElement.EnabledState;
import net.i2cat.mantychore.model.OSPFArea;
import net.i2cat.mantychore.model.OSPFAreaConfiguration;
import net.i2cat.mantychore.model.OSPFProtocolEndpointBase;
import net.i2cat.mantychore.model.OSPFService;
import net.i2cat.mantychore.model.utils.ModelHelper;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.router.capability.ospf.OSPFCapability;

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
			OSPFCapability ospfCapability = (OSPFCapability) getCapability(router.getCapabilities(), OSPFCapability.CAPABILITY_NAME);
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