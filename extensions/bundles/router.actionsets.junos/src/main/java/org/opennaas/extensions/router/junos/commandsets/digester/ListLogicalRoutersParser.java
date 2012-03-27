package org.opennaas.extensions.router.junos.commandsets.digester;

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
