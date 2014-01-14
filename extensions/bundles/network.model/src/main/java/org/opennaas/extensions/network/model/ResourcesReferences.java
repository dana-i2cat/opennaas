package org.opennaas.extensions.network.model;

/*
 * #%L
 * OpenNaaS :: Network :: Model
 * %%
 * Copyright (C) 2007 - 2014 Fundació Privada i2CAT, Internet i Innovació a Catalunya
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;

/**
 * Class to store a mapping between resources in network topology and resources in ResourceManager.
 * 
 * This mapping is made through networkModel names and resource ids. Hence, keys of this map should be resource names, while values should be resource
 * IDs.
 * 
 * @author isart
 */
public class ResourcesReferences extends HashMap<String, String> {

	private static final long	serialVersionUID	= -84597832755877390L;

}
