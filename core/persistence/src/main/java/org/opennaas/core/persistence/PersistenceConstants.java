package org.opennaas.core.persistence;
/**
 * Constants for the service properties of EntityManagers registered
 * in the OSGi Registry.
 *
 * @author Valery Abu-Eid (Dynamic Java.org)
 * @author Mathieu Lemay
 *
 */
public class PersistenceConstants {
	/** Property for Persistence Unit Name**/
	public static final String PERSISTENCE_UNIT_PROPERTY = "persistenceUnit";
	/** Boolean indicating if this Manager is Dynamic or a static instance **/
	public static final String IS_DYNAMIC_FACTORY_PROPERTY = "isDynamicFactory";
	/** Symbolic Name of Owner Bundle **/
	public static final String OWNER_BUNDLE_SYMBOLIC_NAME_PROPERTY = "ownerBundleSymbolicName";
	/** Version of the Owner Bundle **/
	public static final String OWNER_BUNDLE_VERSION_PROPERTY = "ownerBundleVersion";

}
