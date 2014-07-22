package org.opennaas.extensions.router.model.opticalSwitch;

/*
 * #%L
 * OpenNaaS :: CIM Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

/**
 * See ITU G.694.1
 * 
 * f (THz)= 196,5000 n * 0,0125 <br/>
 * n = ( 196,5000 f (THz) ) / 0,0125 <br/>
 * n = ( 196500,0 c / lambda(nm) ) / 12'50 <br/>
 * lambda (nm) = c / (196500,0 n * 12,50 ) <br/>
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
		// assure only 4 decimals are used (operations with doubles may not be exact!)
		int initFreq = (int) Math.round(initialFreq * 10000);
		int guardInterval = (int) Math.round(ITUGrid.guardInterval * 10000);

		int freq = initFreq - (channelNum * guardInterval);
		double realFreq = freq / 10000.0;
		return realFreq;
	}

	public int getChannelNumberFromFrequency(double frequency) {

		// assure only 4 decimals are used (operations with doubles may not be exact!)
		int freq = (int) Math.round(frequency * 10000);
		int initFreq = (int) Math.round(initialFreq * 10000);
		int guardInterval = (int) Math.round(ITUGrid.guardInterval * 10000);

		long channelNum = (initFreq - freq) / guardInterval;
		return (int) channelNum;
	}

	public double getFrequencyFromLambda(double lambda) {
		// assure frequency has only 4 decimals
		double freq = c / lambda;
		long roundedFreq = Math.round(freq * 10000);
		roundedFreq = roundedFreq / 10000;
		double realFreq = roundedFreq / 1000.0;
		return realFreq;
	}

	public double getLambdaFromFrequency(double frequency) {
		return c / (frequency * 1000.0);
	}

	public int getNumberOfChannels() {
		// assure operations use only 4 decimals
		int initFreq = (int) Math.round(initialFreq * 10000);
		int endFreq = (int) Math.round(finalFreq * 10000);
		int guardInterval = (int) Math.round(ITUGrid.guardInterval * 10000);

		return (int) ((initFreq - endFreq) / guardInterval);
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
