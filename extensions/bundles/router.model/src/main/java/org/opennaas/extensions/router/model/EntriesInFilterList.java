package org.opennaas.extensions.router.model;

/**
 * This is a specialization of the CIM_Component aggregation which is used to define a set of filter entries (subclasses of FilterEntryBase) that are
 * aggregated by a particular FilterList.
 * 
 * @version 2.20.1
 */
/*
 * @Generated(value="org.dmtf.cim.TranslateCIM", comments="TranslateCIM version 0.9.1", date="2012-11-19T12:22:55+0100")
 */
public class EntriesInFilterList extends Component {

	/**
	 * Default constructor
	 */
	public EntriesInFilterList() {
	}

	/**
	 * The FilterList, which aggregates the set of FilterEntries.
	 */
	private FilterList		groupComponent;

	/**
	 * Any subclass of FilterEntryBase which is a part of the FilterList.
	 */
	private FilterEntryBase	partComponent;

	/**
	 * The order of the Entry relative to all others in the FilterList. The only permissible value is zero - indicating that all the Entries are ANDed
	 * together.
	 */
	private int				entrySequence	= 0;

	public FilterList getGroupComponent() {
		return groupComponent;
	}

	public void setGroupComponent(FilterList groupComponent) {
		this.groupComponent = groupComponent;
	}

	public FilterEntryBase getPartComponent() {
		return partComponent;
	}

	public void setPartComponent(FilterEntryBase partComponent) {
		this.partComponent = partComponent;
	}

	public int getEntrySequence() {
		return entrySequence;
	}

	public void setEntrySequence(int entrySequence) {
		this.entrySequence = entrySequence;
	}

	public static EntriesInFilterList link(FilterList groupComponent, FilterEntryBase
			partComponent) {

		EntriesInFilterList assoc = (EntriesInFilterList) Association.link(EntriesInFilterList.class, groupComponent, partComponent);
		assoc.setGroupComponent(groupComponent);
		assoc.setPartComponent(partComponent);
		// because 0 is the only permissible value, entySequence is not offered as a parameter
		assoc.setEntrySequence(0);

		return assoc;
	}

}
