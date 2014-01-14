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
 * An association that links a PolicyGroup to the System in whose scope the Group is defined.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyGroupInSystem extends PolicySetInSystem {

	/**
	 * Default constructor
	 */
	public PolicyGroupInSystem() {
	}

	// /**
	// * The System in whose scope a PolicyGroup is defined.
	// */
	// private System antecedent;
	//
	// /**
	// * A PolicyGroup named within the scope of a System.
	// */
	// private PolicyGroup dependent;

	public System getAntecedent() {
		return super.getAntecedent();
	}

	public void setAntecedent(System antecedent) {
		super.setAntecedent(antecedent);
	}

	public PolicyGroup getDependent() {
		return (PolicyGroup) super.getDependent();
	}

	public void setDependent(PolicyGroup dependent) {
		super.setDependent(dependent);
	}

	public static PolicyGroupInSystem link(System antecedent, PolicyGroup dependent, int priority) {

		PolicyGroupInSystem assoc = (PolicyGroupInSystem) Association.link(PolicyGroupInSystem.class, antecedent, dependent);
		assoc.setAntecedent(antecedent);
		assoc.setDependent(dependent);
		assoc.setPriority(priority);

		return assoc;
	}

}
