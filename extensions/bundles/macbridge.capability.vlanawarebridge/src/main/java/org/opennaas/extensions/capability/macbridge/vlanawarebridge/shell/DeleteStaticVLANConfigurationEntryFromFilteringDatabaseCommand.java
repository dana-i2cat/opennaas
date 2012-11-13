package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "deletestaticvlanconfiguration", description = "Delete a static VLAN configuration entry to the filtering database")
public class DeleteStaticVLANConfigurationEntryFromFilteringDatabaseCommand extends GenericKarafCommand {
	
	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to delete the static VLAN configuration on", required = true, multiValued = false)
	private String	resourceId;
	
	@Argument(index = 1, name = "vlanID", description = "ID of the VLAN", required = true, multiValued = false)
	private int	vlanID;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Delete a static VLAN configuration entry to the filtering database");
		try {
			IResource macBridge = getResourceFromFriendlyName(resourceId);
			IVLANAwareBridgeCapability vlanAwareBridgeCapability = 
				(IVLANAwareBridgeCapability) macBridge.getCapabilityByInterface(IVLANAwareBridgeCapability.class);
			vlanAwareBridgeCapability.deleteStaticVLANRegistrationEntryFromFilteringDatabase(vlanID);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error deleting a static VLAN configuration entry to the filtering database");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}