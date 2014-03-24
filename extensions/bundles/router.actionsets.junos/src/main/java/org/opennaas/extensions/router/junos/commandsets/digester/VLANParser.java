package org.opennaas.extensions.router.junos.commandsets.digester;

/*
 * #%L
 * OpenNaaS :: Router :: Junos ActionSet
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

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;
import org.opennaas.extensions.router.model.BridgeDomain;
import org.opennaas.extensions.router.model.System;

/**
 * 
 * @author Adrian Rosello Rey (i2CAT)
 * 
 */
public class VLANParser extends DigesterEngine {

	class ParserRuleSet extends RuleSetBase {

		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester digester) {

			// FIXME the path pattern can't be global , must distinguish between
			// routers

			addObjectCreate("*/vlans/vlan", BridgeDomain.class);
			addCallMethod("*/vlans/vlan/name", "setElementName", 0);
			addCallMethod("*/vlans/vlan/vlan-id", "setVlanId", 0, new Class[] { Integer.TYPE });
			addCallMethod("*/vlans/vlan/description", "setDescription", 0);
			addCallMethod("*/vlans/vlan/interface", "addNetworkPort", 0);

			// add BridgeDomain to model when closing tag
			addSetNext("*/vlans/vlan", "addBridgeDomain");

		}
	}

	private System	model;

	public VLANParser(System model) {
		ruleSet = new ParserRuleSet();
		setModel(model);
	}

	public System getModel() {
		return model;
	}

	public void setModel(System model) {
		this.model = model;
	}

	public void addBridgeDomain(BridgeDomain domain) {

		model.addHostedCollection(domain);

	}

}
