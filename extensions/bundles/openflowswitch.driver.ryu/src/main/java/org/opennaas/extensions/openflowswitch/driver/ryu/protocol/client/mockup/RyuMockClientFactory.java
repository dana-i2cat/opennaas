package org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.mockup;

/*
 * #%L
 * OpenNaaS :: OpenFlow Switch :: Ryu driver v3.14
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

import org.opennaas.core.resources.protocol.ProtocolSessionContext;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.IRyuStatsClient;
import org.opennaas.extensions.openflowswitch.driver.ryu.protocol.client.RyuClientFactory;

/**
 * Ryu mock client factory
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuMockClientFactory extends RyuClientFactory {

	@Override
	public IRyuStatsClient createClient(ProtocolSessionContext sessionContext) {
		return new RyuMockClient();
	}

}
