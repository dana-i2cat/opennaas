package net.i2cat.mantychore.capability.chassis.shell;

import java.util.List;

import net.i2cat.mantychore.actionsets.junos.ActionConstants;
import net.i2cat.mantychore.capability.chassis.ChassisCapability;
import net.i2cat.mantychore.model.ComputerSystem;
import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.LogicalDevice;
import net.i2cat.mantychore.model.LogicalTunnelPort;
import net.i2cat.mantychore.model.NetworkPort;
import net.i2cat.mantychore.model.VLANEndpoint;
import net.i2cat.mantychore.model.NetworkPort.LinkTechnology;
import net.i2cat.nexus.resources.IResource;
import net.i2cat.nexus.resources.IResourceIdentifier;
import net.i2cat.nexus.resources.IResourceManager;
import net.i2cat.nexus.resources.ResourceException;
import net.i2cat.nexus.resources.capability.ICapability;
import net.i2cat.nexus.resources.shell.GenericKarafCommand;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;

@Command(scope = "chassis", name = "setVLAN", description = "Set a VLAN id into an interface.")
public class SetVLANCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource name.", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "subInterface", description = "The interface where to set the VLAN.", required = true, multiValued = false)
	private String	subinterface;

	@Argument(index = 2, name = "VLANid", description = "the VLAN id.", required = true, multiValued = false)
	private int		vlanId;

	@Override
	protected Object doExecute() throws Exception {

		initcommand("set VLAN");

		try {
			IResourceManager manager = getResourceManager();

			if (!splitResourceName(resourceId))
				return null;

			IResourceIdentifier resourceIdentifier = null;

			resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);
			if (resourceIdentifier == null) {
				printError("Error in identifier.");
				endcommand();
				return null;
			}

			IResource resource = manager.getResource(resourceIdentifier);

			validateResource(resource);

			Object params = validateParams(resource);
			if (params == null) {
				return null;
			}
			ICapability chassisCapability = getCapability(resource.getCapabilities(), ChassisCapability.CHASSIS);
			printInfo("Sending message to the queue");
			chassisCapability.sendMessage(ActionConstants.SETVLAN, params);

		} catch (ResourceException e) {
			printError(e);
			endcommand();
			return null;
		} catch (Exception e) {
			printError("Error listing interfaces.");
			printError(e);
			endcommand();
			return null;
		}
		endcommand();
		return null;
	}

	private boolean validateResource(IResource resource) throws ResourceException {
		if (resource == null)
			throw new ResourceException("No resource found.");
		if (resource.getModel() == null)
			throw new ResourceException("The resource didn't have a model initialized. Start the resource first.");
		if (resource.getCapabilities() == null) {
			throw new ResourceException("The resource didn't have the capabilities initialized. Start the resource first.");
		}
		return true;
	}

	private Object validateParams(IResource resource) throws Exception {
		if (splitInterfaces(subinterface)) {

			String name = argsInterface[0];
			int port = Integer.parseInt(argsInterface[1]);
			ComputerSystem routerModel = (ComputerSystem) resource.getModel();

			List<LogicalDevice> ld = routerModel.getLogicalDevices();
			for (LogicalDevice device : ld) {
				// the interface is found on the model
				if (device.getElementName().equalsIgnoreCase(name)) {
					if (device instanceof NetworkPort) {

						// TODO implement method clone
						device.getElementName();
						if (((NetworkPort) device).getPortNumber() == port) {
							if (name.startsWith("lt")) {
								LogicalTunnelPort lt = new LogicalTunnelPort();
								LogicalTunnelPort ltOld = (LogicalTunnelPort) device;
								lt.setElementName(ltOld.getElementName());
								lt.setPortNumber(ltOld.getPortNumber());
								lt.setPeer_unit(ltOld.getPeer_unit());
								lt.setLinkTechnology(LinkTechnology.OTHER);
								VLANEndpoint vlan = new VLANEndpoint();
								vlan.setVlanID(vlanId);
								lt.addProtocolEndpoint(vlan);
								return lt;
							} else if (name.startsWith("lo")) {

								printError("Not allowed VLAN configuration for loopback interface");
								endcommand();
								return null;

							} else {
								EthernetPort ethOld = (EthernetPort) device;
								EthernetPort eth = new EthernetPort();
								eth.setElementName(ethOld.getElementName());
								eth.setPortNumber(ethOld.getPortNumber());
								eth.setLinkTechnology(LinkTechnology.OTHER);
								VLANEndpoint vlan = new VLANEndpoint();
								vlan.setVlanID(vlanId);
								eth.addProtocolEndpoint(vlan);
								return eth;
							}

						}
					}
				}
			}
		}
		return null;

	}
}
