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
 * PolicySetComponent is a concrete aggregation that collects instances of the subclasses of PolicySet (i.e., PolicyGroups and PolicyRules). Instances
 * are collected in sets that use the same decision strategy. They are prioritized relative to each other, within the set, using the Priority property
 * of this aggregation.<br>
 * <br>
 * <br>
 * <br>
 * Together, the PolicySet.PolicyDecisionStrategy and PolicySet Component.Priority properties determine the processing for the groups and rules
 * contained in a PolicySet. A larger priority value represents a higher priority. Note that the Priority property MUST have a unique value when
 * compared with others defined for the same aggregating PolicySet. Thus, the evaluation of rules within a set is deterministically specified.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicySetComponent extends PolicyComponent {

	/**
	 * Default constructor
	 */
	public PolicySetComponent() {
	}

	// /**
	// * A PolicySet that aggregates other PolicySet instances.
	// */
	// private PolicySet groupComponent;
	//
	// /**
	// * A PolicySet aggregated into a PolicySet.
	// */
	// private PolicySet partComponent;

	/**
	 * A non-negative integer for prioritizing this PolicySet component relative to other elements of the same PolicySet. A larger value indicates a
	 * higher priority. The Priority property MUST have a unique value when compared with others defined for the same aggregating PolicySet.
	 */
	private int	priority;

	public PolicySet getGroupComponent() {
		return (PolicySet) super.getGroupComponent();
	}

	public void setGroupComponent(PolicySet groupComponent) {
		super.setGroupComponent(groupComponent);
	}

	public PolicySet getPartComponent() {
		return (PolicySet) super.getPartComponent();
	}

	public void setPartComponent(PolicySet partComponent) {
		super.setPartComponent(partComponent);
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public static PolicySetComponent link(PolicySet groupComponent, PolicySet partComponent, int priority) {
		PolicySetComponent assoc = (PolicySetComponent) Association.link(PolicySetComponent.class, groupComponent,
				partComponent);

		assoc.setGroupComponent(groupComponent);
		assoc.setPartComponent(partComponent);
		assoc.setPriority(priority);

		return assoc;
	}

}
