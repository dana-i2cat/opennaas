package org.opennaas.extensions.router.capability.vrrp.shell;

import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResource;
import org.opennaas.core.resources.ResourceException;
import org.opennaas.core.resources.shell.GenericKarafCommand;
import org.opennaas.extensions.router.capability.vrrp.IVRRPCapability;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.IPProtocolEndpoint;
import org.opennaas.extensions.router.model.LogicalDevice;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.ProtocolEndpoint;
import org.opennaas.extensions.router.model.ServiceAccessPoint;
import org.opennaas.extensions.router.model.VRRPGroup;
import org.opennaas.extensions.router.model.VRRPProtocolEndpoint;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

/**
 * @author Julio Carlos Barrera
 */
@Command(scope = "vrrp", name = "unconfigure", description = "Unconfigure VRRP in given IP Address in an interface of a router")
public class UnconfigureVRRPCommand extends GenericKarafCommand {

	@Argument(index = 0, name = "resourceType:resourceName", description = "Name of the router to unconfigure VRRP", required = true, multiValued =
			false)
	private String	resourceId;

	@Argument(index = 1, name = "interfaceName", description = "Name of the interface in the router", required = true, multiValued =
			false)
	private String	interfaceName;

	@Argument(index = 2, name = "interfaceIPAddress", description = "IP address of the interface in the router", required = true, multiValued =
			false)
	private String	interfaceIPAddress;

	@Argument(index = 3, name = "vrrpGroupId", description = "The VRRP group ID", required = true, multiValued =
			false)
	private int		vrrpGroupId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("Unconfigure VRRP");
		try {
			IResource router = getResourceFromFriendlyName(resourceId);

			IVRRPCapability vrrpCapability = (IVRRPCapability) router.getCapabilityByInterface(IVRRPCapability.class);

			ComputerSystem computerSystem = (ComputerSystem) router.getModel();

			VRRPProtocolEndpoint vrrpProtocolEndpoint = null;
			// get router LogicalDevice's
			List<LogicalDevice> logicalDevices = computerSystem.getLogicalDevices();
			for (LogicalDevice logicalDevice : logicalDevices) {
				if (logicalDevice instanceof NetworkPort) {
					NetworkPort networkPort = (NetworkPort) logicalDevice;
					// get specified NetworkPort
					// get interface name and port
					String[] argsInterface = null;
					try {
						argsInterface = splitInterfaces(interfaceName);
					} catch (Exception e) {
						printError("Error unconfiguring VRRP. Bad interfaceName received (" + interfaceName + ")");
						printEndCommand();
						return -1;
					}
					String ifaceName = argsInterface[0];
					int port = Integer.parseInt(argsInterface[1]);
					if (networkPort.getName().equals(ifaceName) && networkPort.getPortNumber() == port) {
						// get ProtocolEndpoint's
						List<ProtocolEndpoint> protocolEndpoints = networkPort.getProtocolEndpoint();
						for (ProtocolEndpoint protocolEndpoint : protocolEndpoints) {
							if (protocolEndpoint instanceof IPProtocolEndpoint) {
								IPProtocolEndpoint ipProtocolEndpoint = (IPProtocolEndpoint) protocolEndpoint;
								// get specified IPProtocolEndpoint
								// TODO setIPv4 or IPv6? now IPv4
								String[] ipParts = IPUtilsHelper.composedIPAddressToIPAddressAndMask(interfaceIPAddress);
								if (ipParts[0].equals(ipProtocolEndpoint.getIPv4Address()) && ipParts[1].equals(ipProtocolEndpoint.getSubnetMask())) {
									// get VRRPProtocolEndpoint with VRRPGroup and unconfigure it
									List<ServiceAccessPoint> serviceAccessPoints = ipProtocolEndpoint.getBindedServiceAccessPoints();
									for (ServiceAccessPoint serviceAccessPoint : serviceAccessPoints) {
										if (serviceAccessPoint instanceof VRRPProtocolEndpoint) {
											int vrrpName = ((VRRPGroup) ((VRRPProtocolEndpoint) serviceAccessPoint).getService()).getVrrpName();
											if (vrrpName == vrrpGroupId) {
												// VRRPProtocolEndpoint found
												vrrpProtocolEndpoint = (VRRPProtocolEndpoint) serviceAccessPoint;
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}

			if (vrrpProtocolEndpoint != null) {
				vrrpCapability.unconfigureVRRP(vrrpProtocolEndpoint);
				printEndCommand();
				return null;
			} else {
				printError("Error unconfiguring VRRP. VRRPProtocolEndpoint not found.");
				printEndCommand();
				return -1;
			}

		} catch (ResourceException e) {
			printError(e);
			printEndCommand();
			return -1;
		} catch (Exception e) {
			printError("Error unconfiguring VRRP.");
			printError(e);
			printEndCommand();
			return -1;
		}
	}
}
