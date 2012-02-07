package net.i2cat.mantychore.commandsets.junos.digester;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class ProtocolsParser extends DigesterEngine {

	class ParserRuleSet extends RuleSetBase {
		private String	prefix	= "";

		protected ParserRuleSet() {

		}

		protected ParserRuleSet(String prefix) {
			this.prefix = prefix;
		}

		@Override
		public void addRuleInstances(Digester arg0) {
			// FIXME the path pattern can't be global , must distinguish between
			// routers
			// TODO add rules to parser protocols bgp ospf
		}
	}

	public ProtocolsParser() {
		ruleSet = new ParserRuleSet();
	}

	public ProtocolsParser(String prefix) {
		ruleSet = new ParserRuleSet(prefix);
	}

}
