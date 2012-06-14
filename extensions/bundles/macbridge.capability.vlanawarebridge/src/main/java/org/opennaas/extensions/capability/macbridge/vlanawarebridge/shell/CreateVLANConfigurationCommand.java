package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "createvlanconfig", description = "Create a VLAN Configuration")
public class CreateVLANConfigurationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to create the VLAN configuration on", required = true, multiValued = false)
	private String	resourceId;

	@Argument(index = 1, name = "name", description = "Name that describes the VLAN", required = true, multiValued = false)
	private String	name;
	
	@Argument(index = 2, name = "vlanID", description = "ID of the VLAN", required = true, multiValued = false)
	private int	vlanID;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create VLAN Configuration ");
		try {
			IResource macBridge = getResourceFromFriendlyName(resourceId);
			IVLANAwareBridgeCapability vlanAwareBridgeCapability = 
				(IVLANAwareBridgeCapability) macBridge.getCapabilityByInterface(IVLANAwareBridgeCapability.class);
			vlanAwareBridgeCapability.createVLANConfiguration(new VLANConfiguration(name, vlanID));
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating VLAN Configuration");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}