package org.opennaas.extensions.gim.model.energy;

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

public enum EnergyClass {
	Green("green"), Brown("brown"), Mixed("mixed");

	private final String	text;

	/**
	 * @param text
	 */
	private EnergyClass(final String text) {
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}

	public static EnergyClass fromString(String text) {
		for (EnergyClass type : EnergyClass.values()) {
			if (type.toString().equals(text))
				return type;
		}
		throw new IllegalArgumentException("Invalid value" + text);
	}
}
