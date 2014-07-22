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
import org.opennaas.extensions.router.model.opticalSwitch.IOpticalSwitchCard;

/**
 * An add card allows adding DWDMChannels into a fiber. A configurable add card may filter channels to add into a fiber, and may be capable of
 * selecting witch port a channel should reach.
 * 
 * @author isart
 * 
 */
public interface IDWDMAddCard extends IOpticalSwitchCard {

	public void configureAddChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

	public void removeAddChannel(DWDMChannel channel, FCPort srcPort, FCPort dstPort);

}
