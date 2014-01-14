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
 * CompoundPolicyCondition is used to represent compound conditions formed by aggregating simpler policy conditions. Compound conditions are
 * constructed by associating subordinate condition terms together using the PolicyConditionInPolicyCondition aggregation.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class CompoundPolicyCondition extends PolicyCondition {

	/**
	 * Default constructor
	 */
	public CompoundPolicyCondition() {
	}

	/**
	 * Indicates whether the list of CompoundPolicyConditions associated with this PolicyRule is in disjunctive normal form (DNF) or conjunctive
	 * normal form (CNF). The default value is 1 ("DNF").
	 */
	public enum conditionListType_enum {

		DNF(1),
		CNF(2);

		private final int	localValue;

		conditionListType_enum(int enumValue) {
			this.localValue = enumValue;
		}
	}

	private conditionListType_enum	conditionListType;

	public conditionListType_enum getConditionListType() {
		return conditionListType;
	}

	public void setConditionListType(conditionListType_enum conditionListType) {
		this.conditionListType = conditionListType;
	}
}
