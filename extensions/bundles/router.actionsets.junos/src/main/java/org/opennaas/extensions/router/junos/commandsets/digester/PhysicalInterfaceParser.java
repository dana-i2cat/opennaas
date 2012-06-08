package org.opennaas.extensions.router.junos.commandsets.digester;

import org.opennaas.extensions.router.model.EthernetPort;
import org.opennaas.extensions.router.model.IPHeadersFilter;
import org.opennaas.extensions.router.model.IPHeadersFilter.HdrIPVersion;
import org.opennaas.extensions.router.model.LogicalTunnelPort;
import org.opennaas.extensions.router.model.ManagedSystemElement.OperationalStatus;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.NetworkPort.LinkTechnology;
import org.opennaas.extensions.router.model.utils.IPUtilsHelper;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class PhysicalInterfaceParser extends DigesterEngine {

	class ParserRuleSet extends RuleSetBase {
		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester arg0) {
			// FIXME IT HAVE TO GET ONLY PHYSICAL INTERFACES
			addObjectCreate("*/interface-information/physical-interface", NetworkPort.class);
			addMyRule("*/interface-information/physical-interface/name", "setName", 0);
			addMyRule("*/interface-information/physical-interface/description", "setDescription", 0);
			addCallMethod("*/interface-information/physical-interface/current-physical-address", "setPermanentAddress", 0);

			addMyRule("*/interface-information/physical-interface/link-level-type", "setLinkTechnologyParser", 0);
			// addCallMethod("*/interface-information/physical-interface/mtu", "setSupportedMaximumTransmissionUnit", 0);

			addMyRule("*/interface-information/physical-interface/link-mode", "setFullDuplexParser", 0);
			addMyRule("*/interface-information/physical-interface/speed", "setMaxSpeedParser", 0);
			addBeanPropertySetter("*/interface-information/physical-interface/description", "description");

			/* Add physical interface to the parent */
			addSetNext("*/interface-information/physical-interface", "addInterface");

		}

	}

	public PhysicalInterfaceParser() {
		this.ruleSet = new ParserRuleSet();
	}

	/* Configure name */
	public void setName(String name) {
		NetworkPort networkPort = (NetworkPort) peek();

		String[] nameAndUnit = name.split("\\.");
		if (nameAndUnit.length == 1) {
			networkPort.setName(name);
		} else {
			networkPort.setName(nameAndUnit[0]);
			int portNumber = -1;
			try {
				portNumber = Integer.parseInt(nameAndUnit[1]);
			} catch (NumberFormatException numFormatException) {
				return;
			}

			networkPort.setPortNumber(portNumber);
		}

	}

	public void setDescription(String description) {
		NetworkPort networkPort = (NetworkPort) peek();
		networkPort.setDescription(description);
	}

	public void addInterface(NetworkPort networkPort) {
		int numPort = networkPort.getPortNumber();
		String nameInterface = networkPort.getName();
		if (numPort != -1) {
			nameInterface = nameInterface + "." + numPort;
		}

		if (networkPort.getName().startsWith("lt")) {
			LogicalTunnelPort logicalTunnel = (LogicalTunnelPort) networkPort;
			if (mapElements.containsKey(nameInterface)) {
				LogicalTunnelPort hashLogicalPort = (LogicalTunnelPort) mapElements.get(nameInterface);
				// TODO implements merge method
				logicalTunnel.merge((LogicalTunnelPort) hashLogicalPort);
				mapElements.remove(nameInterface);
			}
			mapElements.put(nameInterface, logicalTunnel);
		} else {
			EthernetPort ethernetPort = (EthernetPort) networkPort;
			if (mapElements.containsKey(nameInterface)) {
				NetworkPort hashLogicalPort = (NetworkPort) mapElements.get(nameInterface);
				ethernetPort.merge((EthernetPort) hashLogicalPort);
				mapElements.remove(nameInterface);
			}
			mapElements.put(nameInterface, ethernetPort);

		}

	}

	/* IPHeadersFilter Parser */

	public void setHdrSrcAddressParser(String srcAddress) {
		try {

			IPHeadersFilter ipHeadersFilter = (IPHeadersFilter) peek();

			String[] arrayIP = srcAddress.split("/");

			if (arrayIP.length > 1) {
				String netmask = arrayIP[1];
				String longNetmask = IPUtilsHelper.parseShortToLongIpv4NetMask(netmask);
				ipHeadersFilter.setHdrSrcMask(IPUtilsHelper.parseStrIPToBytesIP(longNetmask));
				ipHeadersFilter.setHdrIPVersion(HdrIPVersion.IPV4);
			}

			ipHeadersFilter.setHdrSrcAddress(IPUtilsHelper.parseStrIPToBytesIP(srcAddress));

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/* Ethernet port Parser */
	public final static String	FULLDUPLEX	= "Full-duplex";

	public void setLinkTechnologyParser(String linkTechnology) {

		try {
			NetworkPort networkPort = (NetworkPort) peek();

			networkPort.setLinkTechnology(LinkTechnology.valueOf(linkTechnology));

		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public void setFullDuplexParser(String fullDuplex) {

		try {
			NetworkPort networkPort = (NetworkPort) peek();
			networkPort.setFullDuplex(fullDuplex.equals(FULLDUPLEX));
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	private static final String	UNLIMITED	= "Unlimited";

	private long parseMaxSpeed(String strMaxSpeed) {

		// parse if the max speed is defined
		long maxSpeed = -1;
		if (!strMaxSpeed.equals(UNLIMITED)) {
			// set the speed of the interface in Mbps
			String[] arrayMaxSpeed = strMaxSpeed.split("m");
			strMaxSpeed = arrayMaxSpeed[0];
			maxSpeed = Long.parseLong(strMaxSpeed);
		}
		return maxSpeed;
	}

	public void setMaxSpeedParser(String strMaxSpeed) {
		try {
			// TODO FIX IF IT IS NOT ONLY POSSIBLE TO FIX NUMBERS
			NetworkPort networkPort = (NetworkPort) peek();
			long maxSpeed = parseMaxSpeed(strMaxSpeed);
			networkPort.setMaxSpeed(maxSpeed);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public static final String	strDOWN	= "down";
	public static final String	strUP	= "up";

	public void setEnabledStateParser(String enabledState) {
		try {
			NetworkPort networkPort = (NetworkPort) peek();
			if (enabledState.equals(strDOWN)) {
				networkPort.setOperationalStatus(OperationalStatus.STOPPED);
			} else {
				networkPort.setOperationalStatus(OperationalStatus.OK);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {
			NetworkPort port = (NetworkPort) mapElements.get(key);
			str += "- NetworkPort: " + '\n';
			str += port.getName() + '\n';
			str += port.getPermanentAddress() + '\n';
			str += String.valueOf(port.isFullDuplex()) + '\n';
			str += String.valueOf(port.getMaxSpeed()) + '\n';
			str += port.getDescription() + '\n';
			if (mapElements.get(key) instanceof LogicalTunnelPort) {
				LogicalTunnelPort logicalTunnel = new LogicalTunnelPort();
				str += "lt peer-unit:" + logicalTunnel.getPeer_unit();
			}
		}

		return str;
	}
}
