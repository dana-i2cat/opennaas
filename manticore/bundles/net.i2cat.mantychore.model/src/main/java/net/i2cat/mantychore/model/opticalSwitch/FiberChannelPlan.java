package net.i2cat.mantychore.model.opticalSwitch;

import java.util.ArrayList;
import java.util.List;

/**
 * MANUALLY ADDED TO CIM (14/04/2011)<br>
 *
 * Stores data about a Fiber Channel Plan. A Fiber Channel Plan determines how to distribute Fiber Channels into a Fiber.
 *
 * @author isart
 *
 */
public class FiberChannelPlan {

	private int	firstChannel	= 0;
	private int	lastChannel		= 0;
	private int	channelGap		= 1;

	public enum ChannelType {
		UNKNOWN,
		WDM,
		TDM
	}

	protected ChannelType	channelType;

	public int getFirstChannel() {
		return firstChannel;
	}

	public void setFirstChannel(int firstChannel) {
		this.firstChannel = firstChannel;
	}

	public int getLastChannel() {
		return lastChannel;
	}

	public void setLastChannel(int lastChannel) {
		this.lastChannel = lastChannel;
	}

	public int getChannelGap() {
		return channelGap;
	}

	public void setChannelGap(int channelGap) {
		this.channelGap = channelGap;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

	public void setChannelType(ChannelType channelType) {
		this.channelType = channelType;
	}

	public int[] getAllChannelsNum() {

		int[] channels = new int[(getLastChannel() - getFirstChannel()) / getChannelGap()];
		for (int i = 0; i < channels.length; i++) {
			channels[i] = getFirstChannel() + i * getChannelGap();
		}
		return channels;
	}

	public List<FiberChannel> getAllChannels() {

		int numOfChannels = (getLastChannel() - getFirstChannel()) / getChannelGap();
		List<FiberChannel> allChannels = new ArrayList<FiberChannel>(numOfChannels);

		for (int i = 0; i < numOfChannels; i++) {
			FiberChannel channel = new FiberChannel();
			// FIXME cannot distinguish between channels!!!
			allChannels.add(channel);
		}
		return allChannels;
	}

}
