package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

import java.util.Iterator;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.model.VLANConfiguration;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "showvlanconfig", description = "Show the existing VLAN Configurations in the VLAN database")
public class ShowVLANConfigurationCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to show the VLAN configurations", required = true, multiValued = false)
	private String	resourceId;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Show VLAN Configurations");
		try {
			IResource macBridgeResource = getResourceFromFriendlyName(resourceId);
			MACBridge macBridge = (MACBridge) macBridgeResource.getModel();
			Iterator<VLANConfiguration> iterator = macBridge.getVLANDatabase().values().iterator();
			VLANConfiguration currentEntry = null;
			this.printSymbolWithoutDoubleLine("VLAN Configuration database contents:\n");
			while (iterator.hasNext()){
				currentEntry = iterator.next();
				this.printSymbolWithoutDoubleLine("VLAN id: "+currentEntry.getVlanID() 
						+". Name: "+currentEntry.getName()+"\n");
			}
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error showing VLAN Configuration");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}