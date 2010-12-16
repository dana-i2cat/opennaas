package net.i2cat.mantychore.commandsets.junos.digester;

import net.i2cat.mantychore.model.EthernetPort;
import net.i2cat.mantychore.model.IPHeadersFilter;
import net.i2cat.mantychore.model.IPHeadersFilter.HdrIPVersion;

public class PhysicalInterfaceParser extends DigesterEngine {

	@Override
	public void addRules() {

		// FIXME IT HAVE TO GET ONLY PHYSICAL INTERFACES
		addObjectCreate("*/interface-information/physical-interface", EthernetPort.class);
		addCallMethod("*/interface-information/physical-interface/name", "setOtherPortType", 0);
		addCallMethod("*/interface-information/physical-interface/current-physical-address", "setPermanentAddress", 0);

		addMyRule("*/interface-information/physical-interface/link-mode", "setFullDuplexParser", 0);
		addMyRule("*/interface-information/physical-interface/speed", "setMaxSpeedParser", 0);
		addBeanPropertySetter("*/interface-information/physical-interface/description", "description");

		/* Add physical interface to the parent */

		addSetNext("*/interface-information/physical-interface", "addInterface");

	}

	public void addInterface(EthernetPort ethernetPort) {
		String location = ethernetPort.getOtherPortType();
		if (mapElements.containsKey(location)) {
			EthernetPort hashEthernetPort = (EthernetPort) mapElements.get(location);
			ethernetPort.merge(hashEthernetPort);
			mapElements.remove(location);
		}
		mapElements.put(location, ethernetPort);

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* Ethernet port Parser */
	public final static String	FULLDUPLEX	= "Full-duplex";

	public void setFullDuplexParser(String fullDuplex) {

		try {
			EthernetPort ethernetPort = (EthernetPort) peek();
			ethernetPort.setFullDuplex(fullDuplex.equals(FULLDUPLEX));
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
			EthernetPort ethernetPort = (EthernetPort) peek();
			long maxSpeed = parseMaxSpeed(strMaxSpeed);
			ethernetPort.setMaxSpeed(maxSpeed);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

	}

	public static final short	DOWN	= 0;
	public static final short	UP		= 1;

	public static final String	strDOWN	= "down";
	public static final String	strUP	= "up";

	public void setEnabledStateParser(String enabledState) {
		try {
			short status = UP;
			if (enabledState.equals(strDOWN))
				status = DOWN;

			EthernetPort ethernetPort = (EthernetPort) peek();

		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {
			EthernetPort port = (EthernetPort) mapElements.get(key);
			str += "- EthernetPort: " + '\n';
			str += port.getOtherPortType() + '\n';
			str += port.getPermanentAddress() + '\n';
			str += String.valueOf(port.isFullDuplex()) + '\n';
			str += String.valueOf(port.getMaxSpeed()) + '\n';
			str += port.getDescription() + '\n';
		}

		return str;
	}

}
