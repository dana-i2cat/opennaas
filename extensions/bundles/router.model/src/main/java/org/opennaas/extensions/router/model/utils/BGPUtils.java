package org.opennaas.extensions.router.model.utils;

import static com.google.common.collect.Iterables.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opennaas.extensions.router.model.AutonomousSystem;
import org.opennaas.extensions.router.model.BGPPeerGroup;
import org.opennaas.extensions.router.model.BGPPeerGroupService;
import org.opennaas.extensions.router.model.BGPProtocolEndpoint;
import org.opennaas.extensions.router.model.BGPService;
import org.opennaas.extensions.router.model.ComputerSystem;
import org.opennaas.extensions.router.model.EntriesInFilterList;
import org.opennaas.extensions.router.model.FilterEntryBase;
import org.opennaas.extensions.router.model.FilterList;
import org.opennaas.extensions.router.model.FilterOfPacketCondition;
import org.opennaas.extensions.router.model.IPAddressPrefixList;
import org.opennaas.extensions.router.model.ManagedElement;
import org.opennaas.extensions.router.model.PacketFilterCondition;
import org.opennaas.extensions.router.model.PolicyAction;
import org.opennaas.extensions.router.model.PolicyActionInPolicyRule;
import org.opennaas.extensions.router.model.PolicyCondition;
import org.opennaas.extensions.router.model.PolicyConditionInPolicyRule;
import org.opennaas.extensions.router.model.PolicyRule;
import org.opennaas.extensions.router.model.PolicySet;
import org.opennaas.extensions.router.model.PolicySetAppliesToElement;
import org.opennaas.extensions.router.model.PolicySetComponent;
import org.opennaas.extensions.router.model.PolicySetInSystem;
import org.opennaas.extensions.router.model.PrefixListFilterEntry;
import org.opennaas.extensions.router.model.RoutersInAS;
import org.opennaas.extensions.router.model.System;

public class BGPUtils {

	public static List<BGPService> getBGPServices(System system) {
		return system.getAllHostedServicesByType(new BGPService());
	}

	public static List<BGPPeerGroup> getBGPPeerGroups(BGPService service) {
		return (List<BGPPeerGroup>) service.getFromAssociatedElementsByType(BGPPeerGroupService.class);
	}

	public static List<BGPProtocolEndpoint> getBGPProtocolEndpoints(BGPService service) {
		List<BGPProtocolEndpoint> peps = new ArrayList<BGPProtocolEndpoint>(service.getProtocolEndpoint().size());
		for (BGPProtocolEndpoint bgpPep : filter(service.getProtocolEndpoint(), BGPProtocolEndpoint.class)) {
			peps.add(bgpPep);
		}
		return peps;
	}

	public static List<PolicySet> getAppliedPolicies(ManagedElement elem) {
		return (List<PolicySet>) elem.getFromAssociatedElementsByType(PolicySetAppliesToElement.class);
	}

	public static List<PolicySet> getPolicySetComponents(PolicySet composite) {
		return (List<PolicySet>) composite.getToAssociatedElementsByType(PolicySetComponent.class);
	}

	public static List<PolicySet> getPolicySetInSystem(System host) {
		return (List<PolicySet>) host.getToAssociatedElementsByType(PolicySetInSystem.class);
	}

	public static List<PolicyCondition> getPolicyConditionInPolicyRule(PolicyRule rule) {
		return (List<PolicyCondition>) rule.getToAssociatedElementsByType(PolicyConditionInPolicyRule.class);
	}

	public static List<PolicyAction> getPolicyActionInPolicyRule(PolicyRule rule) {
		return (List<PolicyAction>) rule.getToAssociatedElementsByType(PolicyActionInPolicyRule.class);
	}

	public static List<FilterList> getFilterOfPacketCondition(PacketFilterCondition condition) {
		return (List<FilterList>) condition.getFromAssociatedElementsByType(FilterOfPacketCondition.class);
	}

	public static List<FilterEntryBase> getEntriesInFilterList(FilterList list) {
		return (List<FilterEntryBase>) list.getToAssociatedElementsByType(EntriesInFilterList.class);
	}

	public static AutonomousSystem getASFromRouter(ComputerSystem router) {
		return (AutonomousSystem) router.getFirstFromAssociatedElementByType(RoutersInAS.class);
	}

	public static List<IPAddressPrefixList> getPrefixListsFromPoliciesInSystem(ComputerSystem system) {

		Map<String, IPAddressPrefixList> prefixLists = new HashMap<String, IPAddressPrefixList>();

		for (PolicySet policy : BGPUtils.getPolicySetInSystem(system)) {
			for (PolicySet rule : BGPUtils.getPolicySetComponents(policy)) {
				if (rule instanceof PolicyRule) {
					for (PolicyCondition condition : BGPUtils.getPolicyConditionInPolicyRule((PolicyRule) rule)) {
						if (condition instanceof PacketFilterCondition) {
							for (FilterList filterList : BGPUtils.getFilterOfPacketCondition((PacketFilterCondition) condition)) {
								for (FilterEntryBase entry : BGPUtils.getEntriesInFilterList(filterList)) {
									if (entry instanceof PrefixListFilterEntry) {
										IPAddressPrefixList prefixList = ((PrefixListFilterEntry) entry).getPrefixList();
										if (prefixList != null) {
											// Notice that using the map repeated elements are overridden
											prefixLists.put(prefixList.getElementName(), prefixList);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return new ArrayList<IPAddressPrefixList>(prefixLists.values());
	}

}
