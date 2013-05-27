package org.opennaas.extensions.router.model.opticalSwitch.dwdm.proteus.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.LogicalModule;
import org.opennaas.extensions.router.model.LogicalPort;
import org.opennaas.extensions.router.model.NetworkPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannel;
import org.opennaas.extensions.router.model.opticalSwitch.FiberChannelPlan;
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;
import org.opennaas.extensions.router.model.opticalSwitch.dwdm.WDMFCPort;

/**
 * MANUALLY ADDED TO CIM (12/4/2011)
 * 
 * This class represents configurable cards providing functionalities for optical switching.
 * 
 * Uses ModulePort association to store ports in this Card.
 * 
 * @author isart
 * 
 */
public class ProteusOpticalSwitchCard extends LogicalModule implements IOpticalSwitchCard {

	// ModulePort Association represents ports physically present on this card (i.e: FCPort where a fiber is connected)
	// getModulePorts().get(i).getPortsOnDevice() represents channels in ModulePort i.

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6451792646851131259L;
	/**
	 * Type and subtype of the card
	 */
	private int	type;
	private int	subtype;

	private int	chasis;

	public enum CardType {
		ROADM_ADD, ROADM_DROP, OPOSNL, PSTN25G, OPTR10G, OPTR25G, OPTR25GPM, PSEDFA, TDCM, PS_OPM
	};

	protected CardType								cardType;

	private FiberChannelPlan						channelPlan			= null;

	// /** Internal ports of this card */
	// private List<NetworkPort> internalPorts = new ArrayList<NetworkPort>();

	private boolean									allowsProtection	= false;

	private HashMap<NetworkPort, List<NetworkPort>>	internalConnections	= new HashMap<NetworkPort, List<NetworkPort>>();

	// private boolean drop = false;

	public ProteusOpticalSwitchCard() {
	}

	// FIXME This constructor should be deleted, the correct access is with sets and gets
	public ProteusOpticalSwitchCard(int chasis, int slot, int type, int subtype) {
		this.type = type;
		this.subtype = subtype;
		this.chasis = chasis;
		this.setSlot(slot);
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getType() {
		return type;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setChasis(int chasis) {
		this.chasis = chasis;
	}

	public int getChasis() {
		return chasis;
	}

	public void setSlot(int slot) {
		setModuleNumber(slot);
	}

	public int getSlot() {
		return getModuleNumber();
	}

	public CardType getCardType() {
		return cardType;
	}

	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	public FiberChannelPlan getChannelPlan() {
		return channelPlan;
	}

	public void setChannelPlan(FiberChannelPlan channelPlan) {
		this.channelPlan = channelPlan;
	}

	public void setAllowsProtection(boolean allowsProtection) {
		this.allowsProtection = allowsProtection;
	}

	public boolean getAllowsProtection() {
		return allowsProtection;
	}

	public boolean addSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {

		if (isInternallyConnected(srcPort, dstPort)) {

			NetworkPort source = srcPort;
			if (srcPort instanceof FCPort) {
				FCPort srcSubPort = getSubPort((FCPort) srcPort, (DWDMChannel) srcChannel);
				if (srcSubPort == null)
					srcSubPort = createSubPort((FCPort) srcPort, (DWDMChannel) srcChannel);
				source = srcSubPort;
			}

			NetworkPort destination = dstPort;
			if (dstPort instanceof FCPort) {
				FCPort dstSubPort = getSubPort((FCPort) dstPort, (DWDMChannel) dstChannel);
				if (dstSubPort == null)
					dstSubPort = createSubPort((FCPort) dstPort, (DWDMChannel) dstChannel);
				destination = dstSubPort;
			}

			return source.addDeviceConnection(destination);

		} else
			return false;
	}

	public boolean removeSwitchingRule(FiberChannel srcChannel, NetworkPort srcPort, FiberChannel dstChannel, NetworkPort dstPort) {

		NetworkPort source = srcPort;
		if (srcPort instanceof FCPort) {
			source = getSubPort((FCPort) srcPort, (DWDMChannel) srcChannel);
		}

		NetworkPort destination = dstPort;
		if (dstPort instanceof FCPort) {
			destination = getSubPort((FCPort) dstPort, (DWDMChannel) dstChannel);
		}

		boolean result = false;
		// if the switching rule may exist
		if (source != null && destination != null) {

			result = source.removeDeviceConnection(destination);

			// check if ports should be removed
			// should remove if they have no connections
			if (source instanceof FCPort) {
				if (source.getOutgoingDeviceConnections().isEmpty()) {
					srcPort.removePortOnDevice(source);
				}
			}

			if (destination instanceof FCPort) {
				if (destination.getIncomingDeviceConnections().isEmpty()) {
					dstPort.removePortOnDevice(destination);
				}
			}
		}

		return result;
	}

	public List getSwitchingRules() {
		// TODO Auto-generated method stub
		return null;
	}

	public List<NetworkPort> getPorts() {
		return getModulePorts();
	}

	public NetworkPort getPort(int portNum) {
		for (NetworkPort port : getModulePorts()) {
			if (port.getPortNumber() == portNum)
				return port;
		}
		return null;
	}

	public List<FiberChannel> getAvailableChannels(NetworkPort port) {
		return getFreeChannels(port);
	}

	public List<FiberChannel> getFreeChannels(NetworkPort port) {
		if (!(port instanceof FCPort))
			return new ArrayList<FiberChannel>(0);

		List<FiberChannel> usedChannels = new ArrayList<FiberChannel>();
		for (LogicalPort subPort : port.getPortsOnDevice()) {
			if (subPort instanceof WDMFCPort) {
				usedChannels.add(((WDMFCPort) subPort).getDWDMChannel());
			}
		}

		List<FiberChannel> allChannels = getChannelPlan().getAllChannels();

		List<FiberChannel> freeChannels = new ArrayList<FiberChannel>(allChannels.size() - usedChannels.size());

		for (FiberChannel channel : allChannels) {
			if (!usedChannels.contains(channel)) {
				freeChannels.add(channel);
			}
		}
		return freeChannels;
	}

	public FCPort getSubPort(FCPort parentPort, DWDMChannel channel) {
		for (LogicalPort subPort : parentPort.getPortsOnDevice()) {
			if (subPort instanceof WDMFCPort) {
				if (((WDMFCPort) subPort).getDWDMChannel().getLambda() == channel.getLambda())
					return (FCPort) subPort;
			}
		}
		return null;
	}

	protected FCPort createSubPort(FCPort parentPort, DWDMChannel channel) {
		WDMFCPort subPort = new WDMFCPort();
		subPort.setDWDMChannel(channel);
		subPort.setPortNumber(channel.getChannelNumber());
		parentPort.addPortOnDevice(subPort);

		return subPort;
	}

	protected FCPort removeSubPort(FCPort parentPort, DWDMChannel channel) {
		FCPort subPort = getSubPort(parentPort, channel);
		if (subPort == null)
			return null;

		parentPort.removePortOnDevice(subPort);
		return subPort;
	}

	public boolean isInternallyConnected(NetworkPort srcPort, NetworkPort dstPort) {
		if (internalConnections.get(srcPort) == null)
			return false;

		return internalConnections.get(srcPort).contains(dstPort);
	}

	public void addInternalConnections(NetworkPort srcPort, List<NetworkPort> dstPorts) {
		internalConnections.put(srcPort, dstPorts);
	}

	public List<NetworkPort> getInternalConnections(NetworkPort srcPort) {
		if (internalConnections.get(srcPort) == null)
			return new ArrayList<NetworkPort>();

		return internalConnections.get(srcPort);
	}

	@Override
	public boolean isPassive() {
		return false;
	}
}
