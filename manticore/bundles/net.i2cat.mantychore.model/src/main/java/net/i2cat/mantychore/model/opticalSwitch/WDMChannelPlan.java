package net.i2cat.mantychore.model.opticalSwitch;

import java.util.ArrayList;
import java.util.List;

public class WDMChannelPlan extends FiberChannelPlan {

	/**
	 * Light speed (m/s)
	 */
	private final double		c	= 299792458;	// m/s

	// Frequencies in THz
	private double				maxFreq;
	private double				minFreq;
	private double				guardInterval;

	private ITUGrid				ituGrid;

	private List<FiberChannel>	channels;

	public WDMChannelPlan() {
		channelType = ChannelType.WDM;

		// use all ITU Grid by default
		ituGrid = new ITUGrid();
		maxFreq = ITUGrid.getInitialFreq();
		minFreq = ITUGrid.getFinalFreq();
		guardInterval = ITUGrid.getGuardInterval();

		channels = generateChannels();
	}

	public WDMChannelPlan(double maxFreq, double minFreq, double guardInterval, ITUGrid ituGrid) {
		channelType = ChannelType.WDM;

		setMaxFreq(maxFreq);
		setMinFreq(minFreq);
		setGuardInterval(guardInterval);
		setITUGrid(ituGrid);

		channels = generateChannels();
	}

	public double getMaxFreq() {
		return maxFreq;
	}

	private void setMaxFreq(double maxFreq) {
		this.maxFreq = maxFreq;
	}

	public double getMinFreq() {
		return minFreq;
	}

	private void setMinFreq(double minFreq) {
		this.minFreq = minFreq;
	}

	public double getGuardInterval() {
		return guardInterval;
	}

	private void setGuardInterval(double guardInterval) {
		this.guardInterval = guardInterval;
	}

	private void setITUGrid(ITUGrid ituGrid) {
		this.ituGrid = ituGrid;
	}

	public final ITUGrid getITUGrid() {
		return ituGrid;
	}

	/**
	 *
	 * @param freq
	 *            in THz
	 * @return channel number
	 */
	public int getChannelNumber(double freq) {
		int channelNum = ituGrid.getChannelNumberFromFrequency(freq);

		// FIXME
		// if (! channelNumSupported(channelNum))
		// throw new ChannelNotSupportedException();

		return channelNum;
	}

	/**
	 *
	 * @param lambda
	 * @return
	 */
	public int getChannelNumberFromLambda(double lambda) {
		double freq = getFrequency(lambda);
		int channelNum = getChannelNumber(freq);

		// FIXME
		// if (! channelNumSupported(channelNum))
		// throw new ChannelNotSupportedException();

		return channelNum;
	}

	/**
	 *
	 * @param channelNum
	 * @return channel frequency in THz
	 */
	public double getFrequency(int channelNum) {

		// FIXME
		// if (! channelNumSupported(channelNum))
		// throw new ChannelNotSupportedException();

		return ituGrid.getFrequencyFromChannelNum(channelNum);
	}

	/**
	 *
	 * @param lambda
	 *            (nm)
	 * @return channel frequency in THz
	 */
	private double getFrequency(double lambda) {
		double freq = ituGrid.getFrequencyFromLambda(lambda);
		// FIXME
		// if (freq > getMaxFreq() || freq < getMinFreq()) {
		// throw new ChannelNotSupportedException();
		// }
		return freq;
	}

	/**
	 *
	 * @param channelNum
	 * @return channel lambda (wavelength) in nm
	 */
	private double getLambda(int channelNum) {
		// FIXME
		// if (!channelNumSupported(channelNum)) {
		// throw new ChannelNotSupportedException();
		// }

		double freq = getFrequency(channelNum);
		return ituGrid.getLambdaFromFrequency(freq);
	}

	@Override
	public int[] getAllChannelsNum() {

		List<FiberChannel> channels = getAllChannels();
		int[] channelNums = new int[channels.size()];

		for (int i = 0; i < channelNums.length; i++) {
			channelNums[i] = channels.get(i).getNumChannel();
		}
		return channelNums;
	}

	public FiberChannel getChannel(int channelNum) {

		// FIXME
		// if (!channelNumSupported(channelNum)) {
		// throw new ChannelNotSupportedException();
		// }

		int index = (channelNum - getFirstChannel()) / getChannelGap();

		// assumes channels is ordered
		return channels.get(index);
	}

	@Override
	public List<FiberChannel> getAllChannels() {
		return channels;
	}

	@Override
	public int getFirstChannel() {
		return ituGrid.getChannelNumberFromFrequency(maxFreq);
	}

	@Override
	public int getLastChannel() {
		return ituGrid.getChannelNumberFromFrequency(minFreq);
	}

	@Override
	public int getChannelGap() {
		return (int) (getGuardInterval() / ituGrid.getGuardInterval());
	}

	private List<FiberChannel> generateChannels() {
		// assure only 4 decimals are taken into account
		// use operations wuth ints as doubles are not exact
		long maxFreqRounded = Math.round(maxFreq * 10000);
		long minFreqRounded = Math.round(minFreq * 10000);
		long guardIntervalRounded = Math.round(guardInterval * 10000);

		int numberOfChannels = (int) ((maxFreqRounded - minFreqRounded) / guardIntervalRounded);
		List<FiberChannel> allChannels = new ArrayList<FiberChannel>(numberOfChannels);

		int firstChannelNum = getFirstChannel();
		int channelGap = getChannelGap();

		for (int i = 0; i < numberOfChannels; i++) {
			DWDMChannel channel = new DWDMChannel();
			channel.setChannelNumber(firstChannelNum + i * channelGap);
			channel.setLambda(getLambda(channel.getChannelNumber()));
			allChannels.add(channel);
		}
		return allChannels;
	}

	// private int localNumToITUGridNum(int localChannelNum) {
	//
	// // FIXME
	// // if (! channelNumSupported(localChannelNum))
	// // throw new ChannelNotSupportedException();
	//
	// int local0 = ituGrid.getChannelNumberFromFrequency(maxFreq);
	// return (int) (local0 + localChannelNum * guardInterval / ituGrid.getGuardInterval());
	// }
	//
	// private int ituGridNumToLocalNum(int ituGridChannelNum) {
	// int local0 = ituGrid.getChannelNumberFromFrequency(maxFreq);
	// int localNum = (int) ((ituGridChannelNum - local0) / (guardInterval / ituGrid.getGuardInterval()));
	//
	// // FIXME
	// // if (! channelNumSupported(localNum))
	// // throw new ChannelNotSupportedException();
	//
	// return localNum;
	// }

	public boolean channelNumSupported(int channelNum) {
		return (channelNum >= getFirstChannel() || channelNum <= getLastChannel());
	}

}
