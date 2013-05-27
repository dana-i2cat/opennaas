package org.opennaas.extensions.router.capability.ospf.shell;

import java.util.ArrayList;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.ospf.IOSPFCapability;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.OSPFArea;
import org.opennaas.extensions.router.model.utils.ModelHelper;

/**
 * @author Isart Canyameres
 */
@Command(scope = "ospf", name = "configureInterfaceInArea", description = "Configure interface in OSPF area")
public class ConfigureInterfaceInAreaCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to apply this command on", required = true, multiValued = false)
	private String			resourceId;

	@Argument(index = 1, name = "areaId", description = "OSPF area where to configure given interfaces.", required = true, multiValued = false)
	private String			areaId;

	@Argument(index = 2, name = "interfaceNames", description = "Interfaces to configure.", required = true, multiValued = true)
	private List<String>	interfaceNames;

	@Option(name = "--delete", aliases = { "-d" }, description = "Delete interfaces from given area, instead of adding them.")
	boolean					delete;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Configure interfaces in OSPF area ");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			// FIXME Cannot read model to get interfaces.
			// model may not be updated :S

			OSPFArea area = new OSPFArea();
			area.setAreaID(ModelHelper.ipv4StringToLong(areaId));

			List<LogicalPort> interfaces = new ArrayList<LogicalPort>(interfaceNames.size());
			for (String interfaceName : interfaceNames) {
				interfaces.add(createInterface(interfaceName));
			}

			IOSPFCapability ospfCapability = (IOSPFCapability) router.getCapabilityByInterface(IOSPFCapability.class);

			if (delete) {
				ospfCapability.removeInterfacesInOSPFArea(interfaces, area);
			} else {
				ospfCapability.addInterfacesInOSPFArea(interfaces, area);
			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error configuring interfaces in OSPF area");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}

	private NetworkPort createInterface(String interfaceName) throws Exception {
		String argsInterface[] = new String[2];
		try {
			argsInterface = splitInterfaces(interfaceName);
		} catch (Exception e) {
			return null;
		}

		String name = argsInterface[0];
		int port = Integer.parseInt(argsInterface[1]);

		if (name.startsWith("lo")) {
			printError("Configuration for Loopback interface not allowed");
			return null;
		}

		NetworkPort networkPort = new NetworkPort();
		networkPort.setName(name);
		networkPort.setPortNumber(port);
		networkPort.setLinkTechnology(LinkTechnology.OTHER);

		return networkPort;
	}
}
