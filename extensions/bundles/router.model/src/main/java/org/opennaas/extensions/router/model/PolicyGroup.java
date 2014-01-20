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
 * An aggregation of PolicySet instances (PolicyGroups and/or PolicyRules) that have the same decision strategy and inherit policy roles. PolicyGroup
 * instances are defined and named relative to the CIM_System that provides their context.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class PolicyGroup extends PolicySet {

	/**
	 * Default constructor
	 */
	public PolicyGroup() {
	}

	/**
	 * The scoping System's CreationClassName.
	 */
	private String	systemCreationClassName;

	/**
	 * The scoping System's Name.
	 */
	private String	systemName;

	/**
	 * CreationClassName indicates the name of the class or the subclass used in the creation of an instance. When used with the other key properties
	 * of this class, this property allows all instances of this class and its subclasses to be uniquely identified.
	 */
	private String	creationClassName;

	/**
	 * A user-friendly name of this PolicyGroup.
	 */
	private String	policyGroupName;

	public String getSystemCreationClassName() {
		return systemCreationClassName;
	}

	public void setSystemCreationClassName(String systemCreationClassName) {
		this.systemCreationClassName = systemCreationClassName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getCreationClassName() {
		return creationClassName;
	}

	public void setCreationClassName(String creationClassName) {
		this.creationClassName = creationClassName;
	}

	public String getPolicyGroupName() {
		return policyGroupName;
	}

	public void setPolicyGroupName(String policyGroupName) {
		this.policyGroupName = policyGroupName;
	}
}
