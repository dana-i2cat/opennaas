package org.opennaas.extensions.router.model.opticalSwitch.dwdm;

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

import org.opennaas.extensions.router.model.FCPort;
import org.opennaas.extensions.router.model.opticalSwitch.DWDMChannel;

public class WDMFCPort extends FCPort {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3682744419246233229L;
	private DWDMChannel			channel;

	public DWDMChannel getDWDMChannel() {
		return channel;
	}

	public void setDWDMChannel(DWDMChannel channel) {
		this.channel = channel;
	}

	// @Override
	// public int hashCode() {
	// final int prime = 31;
	// int result = 1;
	// result = prime * result + ((channel == null) ? 0 : channel.hashCode());
	// return result;
	// }
	//
	// @Override
	// public boolean equals(Object obj) {
	//
	// if (this == obj)
	// return true;
	// if (obj == null)
	// return false;
	// if (getClass() != obj.getClass())
	// return false;
	// WDMFCPort other = (WDMFCPort) obj;
	// if (channel == null) {
	// if (other.channel != null)
	// return false;
	// } else if (!channel.equals(other.channel))
	// return false;
	// return true;
	// }

}
