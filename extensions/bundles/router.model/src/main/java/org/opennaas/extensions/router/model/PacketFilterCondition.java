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
 * PacketFilterCondition specifies packet selection criteria (via association to FilterLists) for firewall policies, IPsec policies and similar uses.
 * It is used as an anchor point to associate various types of filters with policy rules via the FilterOfPacketCondition association. By definition,
 * policy rules that aggregate PacketFilterCondition are assumed to operate against every packet received and/or transmitted from an ingress and/or
 * egress point. (Whether policy condition evaluation occurs at ingress or egress is specified by the Direction property in the associated
 * FilterList.) PacketFilterCondition MAY also be used to define the specific CredentialManagementService that validates the credentials carried in a
 * packet. This is accomplished using the association, AcceptCredentialFrom.<br>
 * <br>
 * <br>
 * <br>
 * Associated objects (such as FilterListsor Credential ManagementServices) represent components of the condition that MAY or MAY NOT apply at a given
 * rule evaluation. For example, an AcceptCredentialFrom evaluation is only performed when a credential is available to be evaluated and compared
 * against the list of trusted credential management services. Similarly, a PeerIDPayloadFilterEntry MAY only be evaluated when an ID payload is
 * available for checking. Condition components that do not have applicability at rule evaluation time, MUST be evaluated to TRUE.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PacketFilterCondition extends PolicyCondition {

	/**
	 * Default constructor
	 */
	public PacketFilterCondition() {
	}
}
