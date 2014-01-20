package org.opennaas.extensions.router.opener.protocol;

/*
 * #%L
 * OpenNaaS :: Router :: Opener ActionSet
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

public class OpenerProtocolSessionFactory implements IProtocolSessionFactory {

	Log	log	= LogFactory.getLog(OpenerProtocolSessionFactory.class);

	public OpenerProtocolSessionFactory() {
		super();
		log.info("OPENER Protocol Session Factory created");
	}

	public IProtocolSession createProtocolSession(String sessionID, ProtocolSessionContext protocolSessionContext) throws ProtocolException {
		OpenerProtocolSession session = new OpenerProtocolSession(sessionID, protocolSessionContext);
		return session;
	}

}
