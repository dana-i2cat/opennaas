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

public class ListLogicalRoutersParser extends DigesterEngine {

	private static int	id	= 0;

	class ParserRuleSet extends RuleSetBase {
		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester arg0) {
			addMyRule("*/logical-systems/name", "setName", 0);
		}
	}

	public ListLogicalRoutersParser() {
		ruleSet = new ParserRuleSet();
		// id = 0;
	}

	public ListLogicalRoutersParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

	/* Parser methods */

	public void setName(String logicalName) {
		id++;
		mapElements.put(String.valueOf(id), logicalName);

	}

	public String toPrint() {

		String str = "" + '\n';
		for (String key : mapElements.keySet()) {

			str += "- Logical names " + mapElements.get(key) + '\n';
		}
		return str;
	}
}
