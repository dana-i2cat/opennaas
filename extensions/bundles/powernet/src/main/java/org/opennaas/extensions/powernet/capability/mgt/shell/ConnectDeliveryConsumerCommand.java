package org.opennaas.extensions.powernet.capability.mgt.shell;

import java.lang.StringBuffer;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.powernet.capability.mgt.IPowerNetManagementCapability;
import org.opennaas.extensions.gim.controller.ModelElementNotFoundException;

@Command(scope = "gim", name = "connectDeliveryConsumer", description = "Connects consumer with given delivery.")
public class ConnectDeliveryConsumerCommand extends GenericKarafCommand {

	@Option(name="--disconnect", aliases="-d", description="Disconnect given elements, instead of normal behaviour", required=false, multiValued=false)
	boolean disconnect = false;
	
	@Argument(index = 0, name = "resourceType:resourceName", description = "The resource id", required = true, multiValued = false)
	private String	resourceName;
	
	@Argument(index = 1, name = "deliveryId", description = "The id of the delivery giving energy.", required = true, multiValued = false)
	private String deliveryId;
	
	@Argument(index = 2, name = "consumerId", description = "The id of the consumer receiving.", required = true, multiValued = false)
	private String	consumerId;
	
	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("connectDeliveryConsumer");
		try {
			IResource resource = getResourceFromFriendlyName(resourceName);
			IPowerNetManagementCapability capab = (IPowerNetManagementCapability) resource
					.getCapabilityByInterface(IPowerNetManagementCapability.class);
			if (!disconnect){
				capab.connectDeliveryConsumer(deliveryId, consumerId);
			} else {
				capab.disconnectDeliveryConsumer(deliveryId, consumerId);
			}
		} catch (Exception e) {
			printError("Error in connectDeliveryConsumer in resource" + resourceName + " with delivery " + deliveryId + " and consumer " + consumerId);
			printError(e);
		} finally {
			printEndCommand();
		}
		printEndCommand();
		return null;
	}

}
