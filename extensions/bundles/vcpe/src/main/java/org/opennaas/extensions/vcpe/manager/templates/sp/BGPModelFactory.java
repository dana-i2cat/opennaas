package org.opennaas.extensions.vcpe.manager.templates.sp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.opennaas.extensions.router.model.AutonomousSystem;
import org.opennaas.extensions.router.model.BGPAction;
import org.opennaas.extensions.router.model.BGPPeerGroup;
import org.opennaas.extensions.router.model.BGPPeerGroupService;
import org.opennaas.extensions.router.model.BGPProtocolEndpoint;
import org.opennaas.extensions.router.model.BGPService;
import org.opennaas.extensions.router.model.BasicAction;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EntriesInFilterList;
import org.opennaas.extensions.router.model.FilterEntry;
import org.opennaas.extensions.router.model.FilterEntryBase;
import org.opennaas.extensions.router.model.FilterList;
import org.opennaas.extensions.router.model.FilterOfPacketCondition;
import org.opennaas.extensions.router.model.HostedBGPPeerGroup;
import org.opennaas.extensions.router.model.IPAddressPrefixList;
import org.opennaas.extensions.router.model.PacketFilterCondition;
import org.opennaas.extensions.router.model.PolicyAction;
import org.opennaas.extensions.router.model.PolicyActionInPolicyRule;
import org.opennaas.extensions.router.model.PolicyCondition;
import org.opennaas.extensions.router.model.PolicyConditionInPolicyRule;
import org.opennaas.extensions.router.model.PolicyGroup;
import org.opennaas.extensions.router.model.PolicyGroupInSystem;
import org.opennaas.extensions.router.model.PolicyRule;
import org.opennaas.extensions.router.model.PolicySet;
import org.opennaas.extensions.router.model.PolicySetAppliesToElement;
import org.opennaas.extensions.router.model.PolicySetComponent;
import org.opennaas.extensions.router.model.PrefixListFilterEntry;
import org.opennaas.extensions.router.model.ProvidesEndpoint;
import org.opennaas.extensions.router.model.RouteFilterEntry;
import org.opennaas.extensions.router.model.RoutersInAS;
import org.opennaas.extensions.router.model.utils.BGPUtils;

public class BGPModelFactory {

	private static final String	DEFAULT_PATH	= "/bgpModel.properties";

	private Properties			props;

	public BGPModelFactory(String path) throws IOException {
		if (path == null)
			path = DEFAULT_PATH;

		loadProperties(path);
	}

	public BGPModelFactory(Properties loadedProperties) {
		props = loadedProperties;
	}

	private void loadProperties(String path) throws IOException {
		props = new Properties();
		File file = new File(path);
		props.load(new FileInputStream(file));
	}

	public ComputerSystem createRouterWithBGP() {

		ComputerSystem router = new ComputerSystem();
		router.setName("test");

		AutonomousSystem as = new AutonomousSystem();
		as.setName(props.getProperty("as.asnum"));
		as.setASNumber(Integer.parseInt(as.getName()));
		RoutersInAS.link(as, router);

		List<BGPService> bgpServices = createBGPModel(router);
		for (BGPService bgpService : bgpServices) {
			router.addHostedService(bgpService);

			List<BGPPeerGroup> groups = BGPUtils.getBGPPeerGroups(bgpService);
			for (BGPPeerGroup group : groups) {
				HostedBGPPeerGroup.link(as, group);
				group.setSystemName(as.getName());
			}
		}

		List<BGPService> loadedServices = BGPUtils.getBGPServices(router);
		assert bgpServices.equals(loadedServices);

		for (BGPService service : loadedServices) {
			assert (BGPUtils.getBGPPeerGroups(service).size() == 1);
		}

		return router;
	}

	public List<BGPService> createBGPModel(ComputerSystem router) {

		List<BGPService> bgpServices = new ArrayList<BGPService>();

		Map<String, IPAddressPrefixList> prefixLists = loadPrefixLists();
		Map<String, PolicyGroup> policies = loadPolicies(prefixLists);

		for (PolicyGroup policy : policies.values()) {
			PolicyGroupInSystem.link(router, policy, 0);
		}

		for (int i = 0; i < Integer.parseInt(props.getProperty("bgp.groups.size")); i++) {

			BGPPeerGroup group = new BGPPeerGroup();
			group.setName(props.getProperty("bgp.group." + i + ".name"));
			group.setHoldTimeConfigured(Integer.parseInt(props.getProperty("bgp.group." + i + ".holdtime")));

			BGPService service = new BGPService();
			service.setRouterID(props.getProperty("bgp.routerid"));
			// keep all
			// path-selection cisco-non-deterministic
			// TODO what about prefix limits?

			BGPPeerGroupService.link(group, service);

			for (int j = 0; j < Integer.parseInt(props.getProperty("bgp.group." + i + ".sessions.size")); j++) {
				BGPProtocolEndpoint session = new BGPProtocolEndpoint();
				session.setDescription("description");
				session.setLocalIdentifier(service.getRouterID());
				session.setPeerIdentifier(props.getProperty("bgp.group." + i + ".session." + j + ".peername"));
				if ("external".equals(props.getProperty("bgp.group." + i + ".type"))) {
					session.setIsEBGP(true);
				} else {
					session.setIsEBGP(false);
				}
				session.setRemoteAS(Integer.parseInt(props.getProperty("bgp.group." + i + ".session." + j + ".peeras")));
				ProvidesEndpoint.link(service, session);

				int importPoliciesNum = Integer.parseInt(props.getProperty("bgp.group." + i + ".session." + j + ".import.policies.size"));
				if (importPoliciesNum > 0) {
					PolicyGroup importPolicyGroup = new PolicyGroup();
					importPolicyGroup.setElementName("import");

					for (int k = 0; k < importPoliciesNum; k++) {
						PolicySet policy = policies.get(props.getProperty("bgp.group." + i + ".session." + j + ".import.policy." + k + ".name"));
						PolicySetComponent.link(importPolicyGroup, policy, importPoliciesNum - k);
					}
					PolicySetAppliesToElement.link(importPolicyGroup, session); // TODO direction INPUT
				}

				int exportPoliciesNum = Integer.parseInt(props.getProperty("bgp.group." + i + ".session." + j + ".export.policies.size"));
				if (exportPoliciesNum > 0) {
					PolicyGroup exportPolicyGroup = new PolicyGroup();
					exportPolicyGroup.setElementName("export");
					for (int k = 0; k < exportPoliciesNum; k++) {
						PolicySet policy = policies.get(props.getProperty("bgp.group." + i + ".session." + j + ".export.policy." + k + ".name"));
						PolicySetComponent.link(exportPolicyGroup, policy, exportPoliciesNum - k);
					}
					PolicySetAppliesToElement.link(exportPolicyGroup, session); // TODO direction OUTPUT
				}
			}

			bgpServices.add(service);

		}

		return bgpServices;
	}

	private Map<String, PolicyGroup> loadPolicies(Map<String, IPAddressPrefixList> prefixLists) {

		Map<String, PolicyGroup> loadedPolicies = new HashMap<String, PolicyGroup>();

		for (int i = 0; i < Integer.parseInt(props.getProperty("policies.size")); i++) {

			PolicyGroup policy = new PolicyGroup();
			policy.setElementName(props.getProperty("policy." + i + ".name"));

			for (int j = 0; j < Integer.parseInt(props.getProperty("policy." + i + ".rules.size")); j++) {
				PolicyRule rule = new PolicyRule();
				rule.setElementName(props.getProperty("policy." + i + ".rule." + j + ".name"));
				PolicySetComponent.link(policy, rule, 0);

				for (int k = 0; k < Integer.parseInt(props.getProperty("policy." + i + ".rule." + j + ".conditions.size")); k++) {
					PolicyCondition condition = createPolicyCondition("policy." + i + ".rule." + j + ".condition." + k,
							props.getProperty("policy." + i + ".rule." + j + ".condition." + k + ".type"), prefixLists);
					PolicyConditionInPolicyRule.link(rule, condition);
				}
				for (int k = 0; k < Integer.parseInt(props.getProperty("policy." + i + ".rule." + j + ".actions.size")); k++) {
					PolicyAction action = createPolicyAction("policy." + i + ".rule." + j + ".action." + k,
							props.getProperty("policy." + i + ".rule." + j + ".action." + k + ".type"));
					PolicyActionInPolicyRule.link(rule, action);
				}
				assert (BGPUtils.getPolicyConditionInPolicyRule(rule).size() == Integer.parseInt(props
						.getProperty("policy." + i + ".rule." + j + ".conditions.size")));
				assert (BGPUtils.getPolicyActionInPolicyRule(rule).size() == Integer.parseInt(props
						.getProperty("policy." + i + ".rule." + j + ".actions.size")));
			}

			loadedPolicies.put(policy.getElementName(), policy);
		}

		return loadedPolicies;
	}

	private PolicyCondition createPolicyCondition(String name, String type, Map<String, IPAddressPrefixList> prefixLists) {

		PolicyCondition condition;
		if (type.equals("packetFilterCondition")) {
			condition = createPacketFilterCondition(name, prefixLists);
		} else {
			throw new UnsupportedOperationException("Could not create condition of type " + type);
		}
		return condition;
	}

	private PacketFilterCondition createPacketFilterCondition(String name, Map<String, IPAddressPrefixList> prefixLists) {

		PacketFilterCondition condition = new PacketFilterCondition();

		for (int i = 0; i < Integer.parseInt(props.getProperty(name + ".filterlists.size")); i++) {
			FilterList filterList = new FilterList();
			FilterOfPacketCondition.link(filterList, condition);

			for (int j = 0; j < Integer.parseInt(props.getProperty(name + ".filterlist." + i + ".entries.size")); j++) {
				FilterEntryBase filterEntry = createFilterEntry(name + ".filterlist." + i + ".entry." + j,
						props.getProperty(name + ".filterlist." + i + ".entry." + j + ".type"), prefixLists);
				EntriesInFilterList.link(filterList, filterEntry);
			}
		}
		return condition;
	}

	private FilterEntryBase createFilterEntry(String name, String type, Map<String, IPAddressPrefixList> prefixLists) {

		FilterEntryBase entry;
		if (type.equals("filterEntry")) {
			entry = createFilterEntry(name);
		} else if (type.equals("routeFilterEntry")) {
			entry = createRouteFilterEntry(name);
		} else if (type.equals("prefixListFilterEntry")) {
			entry = createPrefixListFilterEntry(name, prefixLists);
		} else {
			throw new UnsupportedOperationException("Could not create filterEntry of type " + type);
		}
		return entry;
	}

	private FilterEntryBase createPrefixListFilterEntry(String name, Map<String, IPAddressPrefixList> prefixLists) {

		PrefixListFilterEntry entry = new PrefixListFilterEntry();
		entry.setAction(PrefixListFilterEntry.Action.PERMIT);

		IPAddressPrefixList prefixList = prefixLists.get(props.getProperty(name + ".prefixlistname"));
		entry.setPrefixList(prefixList);

		return entry;
	}

	private FilterEntryBase createRouteFilterEntry(String name) {
		RouteFilterEntry entry = new RouteFilterEntry();
		entry.setAddress(props.getProperty(name + ".routefilter.address"));
		entry.setMatchOption(props.getProperty(name + ".routefilter.option"));
		return entry;
	}

	private FilterEntryBase createFilterEntry(String name) {
		FilterEntry entry = new FilterEntry();

		String trafficType = props.getProperty(name + ".trafficType");
		String matchType = props.getProperty(name + ".matchtype");
		String matchValue = props.getProperty(name + ".matchvalue");

		if (trafficType != null) {
			if (trafficType.equals("ipv4")) {
				entry.setTrafficType(FilterEntry.TrafficType.IPV4);
			} else if (trafficType.equals("ipv6")) {
				entry.setTrafficType(FilterEntry.TrafficType.IPV6);
			} else if (trafficType.equals("other")) {
				entry.setTrafficType(FilterEntry.TrafficType.OTHER);
			} else {
				entry.setTrafficType(FilterEntry.TrafficType.UNKNOWN);
			}
		}

		if (matchType != null) {
			if (matchType.equals("protocol")) {
				entry.setMatchConditionType(FilterEntry.MatchConditionType.PROTOCOL_TYPE);
			} else {
				entry.setMatchConditionType(FilterEntry.MatchConditionType.OTHER);
			}
		}

		entry.setMatchConditionValue(matchValue);

		return entry;
	}

	private PolicyAction createPolicyAction(String name, String type) {

		PolicyAction action;
		if (type.equals("baseAction")) {
			action = createBaseAction(name);
		} else if (type.equals("bgpAction")) {
			action = createBGPAction(name);
		} else {
			throw new UnsupportedOperationException("Could not create action of type " + type);
		}
		return action;
	}

	private PolicyAction createBGPAction(String name) {
		BGPAction action = new BGPAction();

		String actionType = props.getProperty(name + ".action");
		String actionValue = props.getProperty(name + ".actionvalue");

		BGPAction.Action bgpAction;
		if (actionType.equals("nexthop")) {
			bgpAction = BGPAction.Action.NEXT_HOP;
		} else {
			throw new UnsupportedOperationException("Invalid BGPAction type " + actionType);
		}

		action.setBGPAction(bgpAction);
		action.setBGPValue(actionValue);

		return action;
	}

	private PolicyAction createBaseAction(String name) {
		BasicAction action = new BasicAction();

		String actionType = props.getProperty(name + ".action");
		if (actionType.equals("permit")) {
			action.setAction(BasicAction.Action.PERMIT);
		} else {
			action.setAction(BasicAction.Action.DENY);
		}

		return action;
	}

	private Map<String, IPAddressPrefixList> loadPrefixLists() {

		Map<String, IPAddressPrefixList> loadedPrefixes = new HashMap<String, IPAddressPrefixList>();

		for (int i = 0; i < Integer.parseInt(props.getProperty("prefixlists.size")); i++) {
			IPAddressPrefixList prefixList = new IPAddressPrefixList();
			String name = props.getProperty("prefixlist." + i + ".name");
			prefixList.setElementName(name);

			int prefixesSize = Integer.parseInt(props.getProperty("prefixlist." + i + ".prefixes.size"));
			List<String> prefixes = new ArrayList<String>(prefixesSize);
			for (int j = 0; j < prefixesSize; j++) {
				prefixes.add(props.getProperty("prefixlist." + i + ".prefix." + j));
			}
			prefixList.setPrefixes(prefixes);

			loadedPrefixes.put(name, prefixList);
		}

		return loadedPrefixes;
	}
}
