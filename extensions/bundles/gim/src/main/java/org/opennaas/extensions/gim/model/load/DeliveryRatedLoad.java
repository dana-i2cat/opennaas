package org.opennaas.extensions.gim.model.load;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 16:31:02
 */
@XmlRootElement
public class DeliveryRatedLoad extends RatedLoad {

	private double	outputVoltage;
	private double	outputCurrent;

	public DeliveryRatedLoad() {

	}

	/**
	 * @return the outputVoltage
	 */
	public double getOutputVoltage() {
		return outputVoltage;
	}

	/**
	 * @param outputVoltage
	 *            the outputVoltage to set
	 */
	public void setOutputVoltage(double outputVoltage) {
		this.outputVoltage = outputVoltage;
	}

	/**
	 * @return the outputCurrent
	 */
	public double getOutputCurrent() {
		return outputCurrent;
	}

	/**
	 * @param outputCurrent
	 *            the outputCurrent to set
	 */
	public void setOutputCurrent(double outputCurrent) {
		this.outputCurrent = outputCurrent;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public String toString() {
		return "DeliveryRatedLoad [outputVoltage=" + getOutputVoltage() + ", outputCurrent()=" + getOutputCurrent() + ", inputVoltage=" + getVoltage() + ", inputCurrent=" + getCurrent() + ", inputPower()=" + getPower() + ", inputEnergy()=" + getEnergy() + "]";
	}

}