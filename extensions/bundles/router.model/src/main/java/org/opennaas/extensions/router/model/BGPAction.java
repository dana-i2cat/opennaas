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

public class BGPAction extends PolicyAction {

	public enum Action {
		ORIGIN,
		AS_PATH,
		NEXT_HOP,
		MULTI_EXIT_DISC,
		LOCAL_PREF,
		ATOMIC_AGGREGATE,
		AGGREGATOR,
		COMMUNITY,
		ORIGINATOR_ID,
		CLUSTER_LIST
	}

	private Action	bGPAction;

	/**
	 * This method returns the BGPAction.bGPAction property value. This property is described as follows:
	 * 
	 * This defines one or more BGP-specific attributes that should be used to modify this routing update.
	 * 
	 * @return int current bGPAction property value
	 * @exception Exception
	 */
	public Action getBGPAction() {

		return this.bGPAction;
	} // getBGPAction

	/**
	 * This method sets the BGPAction.bGPAction property value. This property is described as follows:
	 * 
	 * This defines one or more BGP-specific attributes that should be used to modify this routing update.
	 * 
	 * @param int new bGPAction property value
	 * @exception Exception
	 */
	public void setBGPAction(Action bGPAction) {

		this.bGPAction = bGPAction;
	} // setBGPAction

	/**
	 * The following constants are defined for use with the ValueMap/Values qualified property bGPValue.
	 */
	private String	bGPValue;

	/**
	 * This method returns the BGPAction.bGPValue property value. This property is described as follows:
	 * 
	 * The value for the corresponding BGPAction.
	 * 
	 * @return String current bGPValue property value
	 * @exception Exception
	 */
	public String getBGPValue() {

		return this.bGPValue;
	} // getBGPValue

	/**
	 * This method sets the BGPAction.bGPValue property value. This property is described as follows:
	 * 
	 * The value for the corresponding BGPAction.
	 * 
	 * @param String
	 *            new bGPValue property value
	 * @exception Exception
	 */
	public void setBGPValue(String bGPValue) {

		this.bGPValue = bGPValue;
	} // setBGPValue

}
