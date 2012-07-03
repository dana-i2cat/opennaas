package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

import java.util.Iterator;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.capability.macbridge.model.MACBridge;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "showstaticvlanconfiguration", description = "Show the existing static VLAN Configurations in the filtering database")
public class ShowStaticVLANConfigurationEntriesInFilteringDatabaseCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to show the VLAN configurations", required = true, multiValued = false)
	private String	resourceId;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Show static VLAN configuration entries in filtering database");
		try {
			IResource macBridgeResource = getResourceFromFriendlyName(resourceId);
			MACBridge macBridge = (MACBridge) macBridgeResource.getModel();
			Iterator<StaticVLANRegistrationEntry> iterator = macBridge.getFilteringDatabase().getStaticVLANRegistrations().values().iterator();
			StaticVLANRegistrationEntry currentEntry = null;
			this.printSymbolWithoutDoubleLine("Static VLAN configuration entries in filtering database: \n");
			String result = null;
			while (iterator.hasNext()){
				currentEntry = iterator.next();
				result = "VLAN id: "+currentEntry.getVlanID()+ "; Ports: ";
				for(int i=0; i<currentEntry.getPortConfigurations().size(); i++){
					result = result + currentEntry.getPortConfigurations().get(i).getPortInterfaceId()+"=";
					if(currentEntry.getPortConfigurations().get(i).isTagged()){
						result = result + "tagged";
					}else{
						result = result + "untagged";
					}
					if (i+1<currentEntry.getPortConfigurations().size()){
						result = result+" ";
					}
				}
				this.printSymbolWithoutDoubleLine(result + "\n");
			}
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error showing static VLAN configuration entries in filtering database");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
}