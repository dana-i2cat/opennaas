package net.i2cat.mantychore.model.opticalSwitch;

/**
 * See ITU G.694.1
 * 
 * f (THz)= 196,5000 – n * 0,0125 <br/>
 * n = ( 196,5000 – f (THz) ) / 0,0125 <br/>
 * n = ( 196500,0 – c / lambda(nm) ) / 12'50 <br/>
 * lambda (nm) = c / (196500,0 – n * 12,50 ) <br/>
 * 
 * @author isart
 * 
 */
public class ITUGrid {

	private static final int	c				= 299792458;	// light speed in m/s

	private static final double	initialFreq		= 196.5;		// THz
	private static final double	finalFreq		= 186.2;		// THz
	private static final double	guardInterval	= 0.0125;		// THz

	public double getFrequencyFromChannelNum(int channelNum) {
		return initialFreq - (channelNum * guardInterval);
	}

	public int getChannelNumberFromFrequency(double frequency) {
		return (int) ((initialFreq - frequency) / guardInterval);
	}

	public double getFrequencyFromLambda(double lambda) {
		return c / (lambda * 1000);
	}

	public double getLambdaFromFrequency(double frequency) {
		return c / (frequency * 1000);
	}

	public int getNumberOfChannels() {
		return (int) ((initialFreq - finalFreq) / guardInterval);
	}

	public double[] getAllChannelsFreq() {

		double[] channelsFreq = new double[getNumberOfChannels()];

		for (int n = 0; n < channelsFreq.length; n++) {
			channelsFreq[n] = getFrequencyFromChannelNum(n);
		}
		return channelsFreq;
	}

	public static double getInitialFreq() {
		return initialFreq;
	}

	public static double getFinalFreq() {
		return finalFreq;
	}

	public static double getGuardInterval() {
		return guardInterval;
	}

}
