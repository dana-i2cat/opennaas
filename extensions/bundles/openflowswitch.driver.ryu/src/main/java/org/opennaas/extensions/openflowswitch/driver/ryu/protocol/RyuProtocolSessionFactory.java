package org.opennaas.extensions.openflowswitch.driver.ryu.protocol;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionFactory;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

/**
 * {@link IProtocolSessionFactory} for {@link RyuProtocolSession}s
 * 
 * @author Julio Carlos Barrera
 *
 */
public class RyuProtocolSessionFactory implements IProtocolSessionFactory {

	private static final Log	log	= LogFactory.getLog(RyuProtocolSessionFactory.class);

	public RyuProtocolSessionFactory() {
		super();
		log.info("RYU Protocol Session Factory created");
	}

	@Override
	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext context) throws ProtocolException {
		RyuProtocolSession session = new RyuProtocolSession(sessionID, context);
		return session;
	}

}
