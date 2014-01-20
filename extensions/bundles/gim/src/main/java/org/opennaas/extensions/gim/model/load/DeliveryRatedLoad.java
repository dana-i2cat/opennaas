package org.opennaas.extensions.gim.model.load;

/*
 * #%L
 * GIM :: GIModel and APC PDU driver
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