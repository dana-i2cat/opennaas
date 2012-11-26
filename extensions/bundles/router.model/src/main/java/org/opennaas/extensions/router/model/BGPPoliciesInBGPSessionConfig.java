package org.opennaas.extensions.router.model;

import java.io.Serializable;

/**
 * This association describes the relationship between BGPPeerFiltering and BGPPolicies related to it, illustrating the group of policies to apply in
 * a particular filtering. Using this association, an instance of BGPPeerFiltering may be linked to many BGPPolicies instances, illustrating the group
 * of policies to apply for filtering. Using this association, an instance of BGPPolicies may be linked to many BGPPeerFiltering instances, as
 * BGPPolicies may be reused.
 */
public class BGPPoliciesInBGPSessionConfig extends Association implements Serializable {

	public BGPPoliciesInBGPSessionConfig() {
	};

	/**
	 * This method create an Association of the type BGPPoliciesInBGPPeerFiltering between one BGPPeerFiltering object and BGPPolicy object
	 */
	public static BGPPoliciesInBGPSessionConfig link(BGPSessionConfig config, Policy
			policy, Direction direction) {

		BGPPoliciesInBGPSessionConfig assoc = (BGPPoliciesInBGPSessionConfig) Association.link(BGPPoliciesInBGPSessionConfig.class, config, policy);
		assoc.setDirection(direction);
		return assoc;
	}// link

	public enum Direction {
		INPUT,
		OUTPUT,
		BOTH
	}

	private Direction	direction;

	/**
	 * This method returns the BGPPoliciesInBGPSessionConfig.direction property value. This property is described as follows:
	 * 
	 * This defines whether this BGPPoliciesInBGPSessionConfig is used for input, output, or both input and output filtering.
	 * 
	 * @return int current direction property value
	 * @exception Exception
	 */
	public Direction getDirection() {

		return this.direction;
	} // getDirection

	/**
	 * This method sets the BGPPoliciesInBGPSessionConfig.direction property value. This property is described as follows:
	 * 
	 * This defines whether this BGPPoliciesInBGPSessionConfig is used for input, output, or both input and output filtering.
	 * 
	 * @param int new direction property value
	 * @exception Exception
	 */
	public void setDirection(Direction direction) {

		this.direction = direction;
	} // setDirection

} // Class BGPPoliciesInBGPPeerFiltering

