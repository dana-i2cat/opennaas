package org.opennaas.extensions.router.model;

/**
 * FilterOfPacketCondition associates a network traffic specification (i.e., a FilterList) with a PolicyRule's PacketFilterCondition.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class FilterOfPacketCondition extends Dependency {

	/**
	 * Default constructor
	 */
	public FilterOfPacketCondition() {
	}

	/**
	 * A FilterList describes the traffic selected by the PacketFilterCondition. A PacketFilterCondition is associated with one and only one
	 * FilterList, but that filter list may aggregate many filter entries.
	 */
	private FilterList				antecedent;

	/**
	 * The PacketFilterCondition that uses the FilterList as part of a PolicyRule.
	 */
	private PacketFilterCondition	dependent;

	public FilterList getAntecedent() {
		return antecedent;
	}

	public void setAntecedent(FilterList antecedent) {
		this.antecedent = antecedent;
	}

	public PacketFilterCondition getDependent() {
		return dependent;
	}

	public void setDependent(PacketFilterCondition dependent) {
		this.dependent = dependent;
	}

	public static FilterOfPacketCondition link(FilterList antecedent, PacketFilterCondition dependent) {
		FilterOfPacketCondition assoc = (FilterOfPacketCondition) Association.link(FilterOfPacketCondition.class, antecedent,
				dependent);

		assoc.setAntecedent(antecedent);
		assoc.setDependent(dependent);

		return assoc;
	}

}
