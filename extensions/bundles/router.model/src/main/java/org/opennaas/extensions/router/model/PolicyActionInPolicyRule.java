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
 * A PolicyRule aggregates zero or more instances of the PolicyAction class, via the PolicyActionInPolicyRule association. A Rule that aggregates zero
 * Actions is not valid--it may, however, be in the process of being entered into a PolicyRepository or being defined for a System. Alternately, the
 * actions of the policy may be explicit in the definition of the PolicyRule. Note that a PolicyRule should have no effect until it is valid.<br>
 * <br>
 * <br>
 * <br>
 * The Actions associated with a PolicyRule may be given a required order, a recommended order, or no order at all. For Actions represented as
 * separate objects, the PolicyActionInPolicyRule aggregation can be used to express an order.<br>
 * <br>
 * <br>
 * <br>
 * This aggregation does not indicate whether a specified action order is required, recommended, or of no significance; the property SequencedActions
 * in the aggregating instance of PolicyRule provides this indication.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyActionInPolicyRule extends PolicyActionStructure {

	/**
	 * Default constructor
	 */
	public PolicyActionInPolicyRule() {
	}

	/**
	 * This property represents the PolicyRule that contains one or more PolicyActions.
	 */
	private PolicyRule		groupComponent;

	/**
	 * This property holds the name of a PolicyAction contained by one or more PolicyRules.
	 */
	private PolicyAction	partComponent;

	public PolicyRule getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(PolicyRule groupComponent) {
		this.groupComponent = groupComponent;
	}

	public PolicyAction getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(PolicyAction partComponent) {
		this.partComponent = partComponent;
	}

	public static PolicyActionInPolicyRule link(PolicyRule groupComponent, PolicyAction partComponent) {
		PolicyActionInPolicyRule assoc = (PolicyActionInPolicyRule) Association.link(PolicyActionInPolicyRule.class, groupComponent,
				partComponent);

		assoc.setGroupComponent(groupComponent);
		assoc.setPartComponent(partComponent);

		return assoc;
	}
}
