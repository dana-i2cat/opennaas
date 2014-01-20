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
 * PolicyActionInPolicyAction is used to represent the compounding of policy actions into a higher-level policy action.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyActionInPolicyAction extends PolicyActionStructure {

	/**
	 * Default constructor
	 */
	public PolicyActionInPolicyAction() {
	}

	/**
	 * This property represents the CompoundPolicyAction that contains one or more PolicyActions.
	 */
	private CompoundPolicyAction	groupComponent;

	/**
	 * This property holds the name of a PolicyAction contained by one or more CompoundPolicyActions.
	 */
	private PolicyAction			partComponent;

	public CompoundPolicyAction getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(CompoundPolicyAction groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyAction getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyAction partComponent) {
		this.partComponent = partComponent;
	}
}
