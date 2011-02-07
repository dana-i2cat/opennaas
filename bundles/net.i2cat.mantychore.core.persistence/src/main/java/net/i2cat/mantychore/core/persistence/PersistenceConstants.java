/* 
 * Copyright 2008, Inocybe Technologies inc.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package net.i2cat.mantychore.core.persistence;
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
