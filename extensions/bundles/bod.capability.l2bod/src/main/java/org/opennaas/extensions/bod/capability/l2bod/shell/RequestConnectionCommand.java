package org.opennaas.extensions.bod.capability.l2bod.shell;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.bod.capability.l2bod.IL2BoDCapability;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.NetworkModel;
import org.opennaas.extensions.network.model.NetworkModelHelper;
import org.opennaas.extensions.network.model.topology.Interface;
import org.opennaas.extensions.network.model.topology.NetworkElement;

@Command(scope = "l2bod", name = "requestConnection",
		description = "Request L2 connectivity between specified interfaces.")
public class RequestConnectionCommand extends GenericKarafCommand
{
	@Argument(index = 0,
			name = "resourceType:resourceName",
			description = "The resource id to request the connectivity.",
			required = true,
			multiValued = false)
	private String	resourceId;

	@Argument(index = 1,
			name = "interface1",
			description = "The name of interface 1 to connect",
			required = true,
			multiValued = false)
	private String	interfaceName1;

	@Argument(index = 2,
			name = "interface2",
			description = "The name of interface 2 to connect",
			required = true,
			multiValued = false)
	private String	interfaceName2;

	@Option(name = "--vlanid",
			aliases = { "-v" },
			description = "VLAN ID to use for vlan-tagging",
			multiValued = false)
	private int		vlanid	= -1;

	@Option(name = "--starttime",
			aliases = { "--start", "-s" },
			description = "Start time (yyyy-MM-dd'T'HH:mm:ssZZ)",
			multiValued = false)
	private String	startTime;

	@Option(name = "--endtime",
			aliases = { "--end", "-e" },
			description = "End time (yyyy-MM-dd'T'HH:mm:ssZZ)",
			required = true,
			multiValued = false)
	private String	endTime;

	@Option(name = "--capacity",
			aliases = { "-c" },
			description = "Capacity in MB/s",
			required = true,
			multiValued = false)
	private int		capacity;

	@Override
	protected Object doExecute()
	{
		printInitCommand("request connectivity of resource: " + resourceId +
				" and interfaces: " + interfaceName1 + " - " + interfaceName2);
		try {
			IResource resource = getResourceFromFriendlyName(resourceId);
			IL2BoDCapability ipCapability = (IL2BoDCapability) resource.getCapabilityByInterface(IL2BoDCapability.class);
			ipCapability.requestConnection(createParameters(resource));
		} catch (Exception e) {
			printError("Error requesting connectivity for resource: " + resourceId);
			printError(e);
			return -1;
		} finally {
			printEndCommand();
		}
		return null;
	}

	private RequestConnectionParameters createParameters(IResource resource)
	{
		NetworkModel model = (NetworkModel) resource.getModel();
		return new RequestConnectionParameters(getInterface(model, interfaceName1),
				getInterface(model, interfaceName2),
				capacity * 1000000L, vlanid,
				parseISO8601Date(startTime),
				parseISO8601Date(endTime));
	}

	private DateTime parseISO8601Date(String s)
	{
		return (s == null) ? new DateTime() : ISODateTimeFormat.dateTimeNoMillis().parseDateTime(s);
	}

	private Interface getInterface(NetworkModel model, String name)
	{
		List<NetworkElement> elements = model.getNetworkElements();
		Interface i =
				NetworkModelHelper.getInterfaceByName(elements, name);
		if (i == null) {
			throw new NoSuchElementException("No such interface: " + name);
		}
		return i;
	}
}