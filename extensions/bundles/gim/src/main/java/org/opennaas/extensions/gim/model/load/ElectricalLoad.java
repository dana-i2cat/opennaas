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


/**
 * @author rcarroll
 * @version 1.0
 * @created 14-Feb-2013 15:41:39
 */
public class ElectricalLoad extends Load {

	private double	inputVoltage;
	private double	inputCurrent;
	private double	inputPower;
	private double	inputEnergy;

	public ElectricalLoad() {

	}

	public void setVoltage(double voltage) {
		inputVoltage = voltage;
	}

	public void setCurrent(double current) {
		inputCurrent = current;
	}

	public void setPower(double power) {
		inputPower = power;
	}

	public void setEnergy(double energy) {
		inputEnergy = energy;
	}

	public double getVoltage() {
		return inputVoltage;
	}

	public double getCurrent() {
		return inputCurrent;
	}

	public double getPower() {
		return inputPower;
	}

	public double getEnergy() {
		return inputEnergy;
	}

	public void finalize() throws Throwable {
		super.finalize();
	}

}