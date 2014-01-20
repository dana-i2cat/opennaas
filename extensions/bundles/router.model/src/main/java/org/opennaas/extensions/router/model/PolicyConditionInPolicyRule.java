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
 * A PolicyRule aggregates zero or more instances of the PolicyCondition class, via the PolicyConditionInPolicyRule association. A Rule that
 * aggregates zero Conditions is not valid; it may, however, be in the process of being defined. Note that a PolicyRule should have no effect until it
 * is valid.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyConditionInPolicyRule extends PolicyConditionStructure {

	/**
	 * Default constructor
	 */
	public PolicyConditionInPolicyRule() {
	}

	/**
	 * This property represents the PolicyRule that contains one or more PolicyConditions.
	 */
	private PolicyRule		groupComponent;

	/**
	 * This property holds the name of a PolicyCondition contained by one or more PolicyRules.
	 */
	private PolicyCondition	partComponent;

	public PolicyRule getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(PolicyRule groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyCondition getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyCondition partComponent) {
		this.partComponent = partComponent;
	}

	public static PolicyConditionInPolicyRule link(PolicyRule groupComponent, PolicyCondition partComponent) {
		PolicyConditionInPolicyRule assoc = (PolicyConditionInPolicyRule) Association.link(PolicyConditionInPolicyRule.class, groupComponent,
				partComponent);

		assoc.setGroupComponent(groupComponent);
		assoc.setPartComponent(partComponent);

		return assoc;
	}

}
