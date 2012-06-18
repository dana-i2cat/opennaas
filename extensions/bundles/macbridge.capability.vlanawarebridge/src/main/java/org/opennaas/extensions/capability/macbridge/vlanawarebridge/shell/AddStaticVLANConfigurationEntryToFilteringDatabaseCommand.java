package org.opennaas.extensions.capability.macbridge.vlanawarebridge.shell;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.capability.macbridge.model.PortConfiguration;
import org.opennaas.extensions.capability.macbridge.model.StaticVLANRegistrationEntry;
import org.opennaas.extensions.capability.macbridge.vlanawarebridge.IVLANAwareBridgeCapability;

/**
 * @author Eduard Grasa
 */
@Command(scope = "vlanawarebridge", name = "addstaticvlanconfiguration", description = "Add a static VLAN configuration entry to the filtering database")
public class AddStaticVLANConfigurationEntryToFilteringDatabaseCommand extends GenericKarafCommand {

	private static final String TAGGED = "tagged";
	
	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the MAC bridge to create the static VLAN configuration on", required = true, multiValued = false)
	private String	resourceId;
	
	@Argument(index = 1, name = "vlanID", description = "ID of the VLAN", required = true, multiValued = false)
	private int	vlanID;
	
	@Argument(index = 2, name = "portConfigurations", description = "The port configurations that will be part of this VLAN, in " +
			"the form portId1=tagged&portId2=untagged&portid3=tagged", required = true, multiValued = false)
	private String portConfigurations;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Create a static VLAN configuration entry to the filtering database");
		try {
			IResource macBridge = getResourceFromFriendlyName(resourceId);
			IVLANAwareBridgeCapability vlanAwareBridgeCapability = 
				(IVLANAwareBridgeCapability) macBridge.getCapabilityByInterface(IVLANAwareBridgeCapability.class);
			StaticVLANRegistrationEntry entry = generateStaticVLANRegistrationEntry(vlanID, portConfigurations);
			vlanAwareBridgeCapability.addStaticVLANRegistrationEntryToFilteringDatabase(entry);
		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error creating a static VLAN configuration entry to the filtering database");
			printError(e);
			printEndCommand();
			return -1;
		}
		printEndCommand();
		return null;
	}
	
	private StaticVLANRegistrationEntry generateStaticVLANRegistrationEntry(int vlanID, String portConfigurations){
		String[] aux = null;
		String[] aux2 = null;
		
		StaticVLANRegistrationEntry entry = new StaticVLANRegistrationEntry();
		entry.setVlanID(vlanID);
		
		aux = portConfigurations.split("&");
		PortConfiguration portConfiguration = null;
		for(int i=0; i<aux.length; i++){
			portConfiguration = new PortConfiguration();
			aux2 = aux[i].split("=");
			portConfiguration.setPortInterfaceId(aux2[0]);
			if(aux2[1].equals(TAGGED)){
				portConfiguration.setTagged(true);
			}else{
				portConfiguration.setTagged(false);
			}
			
			entry.getPortConfigurations().add(portConfiguration);
		}
		
		return entry;
	}
}