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
 * CIM_PolicyInSystem is a generic association used to establish dependency relationships between Policies and the Systems that host them. These
 * Systems may be ComputerSystems where Policies are 'running' or they may be Policy Repositories where Policies are stored. This relationship is
 * similar to the concept of CIM_Services being dependent on CIM_Systems as defined by the HostedService association.<br>
 * <br>
 * <br>
 * <br>
 * Cardinality is Max (1) for the Antecedent/System reference since Policies can only be hosted in at most one System context. Some subclasses of the
 * association will further refine this definition to make the Policies Weak to Systems. Other subclasses of PolicyInSystem will define an optional
 * hosting relationship. Examples of each of these are the PolicyRuleInSystem and PolicyConditionIn PolicyRepository associations, respectively.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public abstract class PolicyInSystem extends HostedDependency {

	/**
	 * Default constructor
	 */
	public PolicyInSystem() {
	}

	/**
	 * The hosting System.
	 */
	private System	antecedent;

	/**
	 * The hosted Policy.
	 */
	private Policy	dependent;

	public System getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(System antecedent) {
		this.antecedent = antecedent;
	}

	public Policy getDependent() {
		return dependent;
	}

	public void setDependent(Policy dependent) {
		this.dependent = dependent;
	}

}
