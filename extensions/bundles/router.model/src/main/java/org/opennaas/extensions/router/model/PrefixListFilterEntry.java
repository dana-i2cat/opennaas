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

public class PrefixListFilterEntry extends FilterEntryBase {

	private IPAddressPrefixList	prefixList;

	public enum Action {
		PERMIT,
		DENY
	}

	private Action	action;

	/**
	 * This defines whether prefixes matching prefixList should we permitted (white list filter) or denied (blacklist filter).
	 * 
	 * @return int current action property value
	 * @exception Exception
	 */
	public Action getAction() {

		return this.action;
	} // getAction

	/**
	 * This method sets the PrefixListFilterEntry.action property value. This property is described as follows:
	 * 
	 * This defines whether prefixes matching prefixList should we permitted (white list filter) or denied (blacklist filter).
	 * 
	 * @param int new action property value
	 * @exception Exception
	 */
	public void setAction(Action action) {

		this.action = action;
	} // setAction

	public IPAddressPrefixList getPrefixList() {
		return prefixList;
	}

	public void setPrefixList(IPAddressPrefixList prefixList) {
		this.prefixList = prefixList;
	}

}
