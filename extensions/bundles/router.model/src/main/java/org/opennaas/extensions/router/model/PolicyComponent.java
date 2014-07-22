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
 * CIM_PolicyComponent is a generic association used to establish 'part of' relationships between the subclasses of CIM_Policy. For example, the
 * PolicyConditionInPolicyRule association defines that PolicyConditions are part of a PolicyRule.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyComponent extends Component {

	/**
	 * Default constructor
	 */
	public PolicyComponent() {
	}

	/**
	 * The parent Policy in the association.
	 */
	private Policy	groupComponent;

	/**
	 * The child/part Policy in the association.
	 */
	private Policy	partComponent;

	public Policy getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(Policy groupComponent) {
		this.groupComponent = groupComponent;
	}

	public Policy getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(Policy partComponent) {
		this.partComponent = partComponent;
	}

}
