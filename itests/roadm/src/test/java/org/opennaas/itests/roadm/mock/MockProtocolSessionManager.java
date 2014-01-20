package org.opennaas.itests.roadm.mock;

/*
 * #%L
 * OpenNaaS :: iTests :: ROADM
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

import java.util.List;
import java.util.Set;

import org.opennaas.core.protocols.sessionmanager.ListResponse;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.protocol.ProtocolSessionContext;

public class MockProtocolSessionManager implements IProtocolSessionManager {

	@Override
	public void destroyProtocolSession(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getAllProtocolSessionIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getResourceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLocked(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IProtocolSession obtainSession(ProtocolSessionContext arg0, boolean arg1) throws ProtocolException {

		return newMockWonesysProtocolSession();
	}

	@Override
	public IProtocolSession getSessionById(String arg0, boolean arg1) throws ProtocolException {
		return newMockWonesysProtocolSession();
	}

	@Override
	public IProtocolSession obtainSessionByProtocol(String arg0, boolean arg1) throws ProtocolException {
		return newMockWonesysProtocolSession();
	}

	@Override
	public void registerContext(ProtocolSessionContext arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseSession(IProtocolSession arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void releaseSession(String arg0) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterContext(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterContext(ProtocolSessionContext context) throws ProtocolException {
		// TODO Auto-generated method stub

	}

	public IProtocolSession newMockWonesysProtocolSession() {

		ProtocolSessionContext sessionContext = new ProtocolSessionContext();
		sessionContext.addParameter("protocol.mock", "true");

		return new SessionMock(sessionContext, "01");
	}

	@Override
	public List<ProtocolSessionContext> getRegisteredContexts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ListResponse getAllProtocolSessionIdsWS() {
		// TODO Auto-generated method stub
		return null;
	}
}
