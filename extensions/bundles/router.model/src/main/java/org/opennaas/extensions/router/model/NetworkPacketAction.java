package org.opennaas.extensions.router.model;

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
 * NetworkPacketAction standardizes different processing options that can be taken at the network packet level. The specific action is defined in the
 * PacketAction enumerated property. Note that this property can be used in conjunction with other actions aggregated into a Rule, to fully define its
 * effects. For example, when aggregated with the SAStaticAction class, NetworkPacketAction indicates whether a specific packet will be encrypted,
 * bypassed or discarded for the lifetime of the Security Association.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class NetworkPacketAction extends PolicyAction {

	/**
	 * Default constructor
	 */
	public NetworkPacketAction() {
	}

	/**
	 * A network packet can be processed, bypassed for processing (i.e., allowed to continue without further processing, such as being forwarded in
	 * the clear versus being encrypted), or discarded. This enumeration indicates how a packet should be handled if a PolicyRule's PolicyConditions
	 * evaluate to TRUE.
	 */
	public enum packetAction_enum {

		Other(1),
		Processed(2),
		Bypassed(3),
		Discarded(4);

		private final int	localValue;

		packetAction_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private packetAction_enum	packetAction;

	/**
	 * Description of the action when the value 1 ("Other") is specified for the property, PacketAction.
	 */
	private String				otherAction;

	public packetAction_enum getPacketAction() {
		return packetAction;
	}

	public void setPacketAction(packetAction_enum packetAction) {
		this.packetAction = packetAction;
	}

	public String getOtherAction() {
		return otherAction;
	}

	public void setOtherAction(String otherAction) {
		this.otherAction = otherAction;
	}
}
