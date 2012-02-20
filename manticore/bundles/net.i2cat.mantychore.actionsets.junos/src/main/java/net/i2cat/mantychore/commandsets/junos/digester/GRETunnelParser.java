package net.i2cat.mantychore.commandsets.junos.digester;

import java.util.HashMap;

import net.i2cat.mantychore.model.GRETunnelConfiguration;
import net.i2cat.mantychore.model.GRETunnelEndpoint;
import net.i2cat.mantychore.model.GRETunnelService;
import net.i2cat.mantychore.model.System;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

public class GRETunnelParser extends DigesterEngine {

	private System				model;

	private GRETunnelService	greTunnelService;

	class ParserRuleSet extends RuleSetBase {

		@Override
		public void addRuleInstances(Digester digester) {
			addGRETunnelRuleInstances(digester);

		}

		private void addGRETunnelRuleInstances(Digester digester) {
			addObjectCreate("*/interfaces/interface/unit", GRETunnelEndpoint.class);
			addObjectCreate("*/interfaces/interface/unit/tunnel", GRETunnelConfiguration.class);
			addMyRule("*/interfaces/interface/unit/tunnel/source", "setSourceAddress", 0);
			addMyRule("*/interfaces/interface/unit/tunnel/destination", "setDestinationAddres", 0);
			addMyRule("*/interfaces/interface/unit/tunnel/key", "setGRETunnelKey", 0);
			addSetNext("*/interfaces/interface/unit/tunnel", "addGRETunnelConfiguration");
			addSetNext("*/interfaces/interface/unit", "addGRETunnelEndpoint");
		}
	}

	public GRETunnelParser(System routerModel) {
		ruleSet = new ParserRuleSet();
		setModel(routerModel);
	}

	private void setModel(System model) {
		this.model = model;
	}

	public System getModel() {
		return model;
	}

	public void init() {
		push(this);
		mapElements = new HashMap<String, Object>();
	}

	public void setSourceAddress(String ip) {
		Object obj = peek(0);
		assert (obj instanceof GRETunnelConfiguration);
		((GRETunnelConfiguration) obj).setSourceAddress(ip);

	}

	public void setDestinationAddress(String ip) {
		Object obj = peek(0);
		assert (obj instanceof GRETunnelConfiguration);
		((GRETunnelConfiguration) obj).setDestinationAddress(ip);
	}

	public void setGRETunnelKey(int key) {
		Object obj = peek(0);
		assert (obj instanceof GRETunnelConfiguration);
		((GRETunnelConfiguration) obj).setKey(key);
	}

	public void addGRETunnelConfiguration(GRETunnelConfiguration greTunnelConfiguration) {

		GRETunnelService greTunnelService = obtainGRETunnelService();
		greTunnelService.addGRETunnelConfiguration(greTunnelConfiguration);

	}

	public GRETunnelService obtainGRETunnelService() {
		if (greTunnelService != null)
			return greTunnelService;

		greTunnelService = new GRETunnelService();
		model.addHostedService(greTunnelService);

		return greTunnelService;
	}

	public void addGRETunnelEndpoint() {

	}
}