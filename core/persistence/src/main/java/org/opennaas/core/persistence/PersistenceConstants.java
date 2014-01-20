package org.opennaas.core.persistence;

/*
 * #%L
 * OpenNaaS :: Core :: JPA Persistence
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * Constants for the service properties of EntityManagers registered in the OSGi Registry.
 * 
 * @author Valery Abu-Eid (Dynamic Java.org)
 * @author Mathieu Lemay
 * 
 */
public class PersistenceConstants {
	/** Property for Persistence Unit Name **/
	public static final String	PERSISTENCE_UNIT_PROPERTY			= "persistenceUnit";
	/** Boolean indicating if this Manager is Dynamic or a static instance **/
	public static final String	IS_DYNAMIC_FACTORY_PROPERTY			= "isDynamicFactory";
	/** Symbolic Name of Owner Bundle **/
	public static final String	OWNER_BUNDLE_SYMBOLIC_NAME_PROPERTY	= "ownerBundleSymbolicName";
	/** Version of the Owner Bundle **/
	public static final String	OWNER_BUNDLE_VERSION_PROPERTY		= "ownerBundleVersion";

}
