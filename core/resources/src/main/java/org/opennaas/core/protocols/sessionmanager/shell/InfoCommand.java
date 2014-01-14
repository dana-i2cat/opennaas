package org.opennaas.core.protocols.sessionmanager.shell;

/*
 * #%L
 * OpenNaaS :: Core :: Resources
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

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * List the device ids registere do the protocol manager
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "info", description = "Provide extended information on a protocol session.")
public class InfoCommand extends GenericKarafCommand {

	IProtocolManager	protocolManager	= null;

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The device owning the session.")
	String				resourceId;

	@Argument(name = "sessionId", index = 1, required = true, description = "The session to lookup.")
	String				sessionId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("protocols information");

		IResourceManager manager = getResourceManager();

		String[] argsRouterName = new String[2];
		try {
			argsRouterName = splitResourceName(resourceId);
		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}

		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

		protocolManager = getProtocolManager();
		IProtocolSessionManager protocolSessionManager = protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (protocolSessionManager == null) {
			printError("Wrong deviceId");
			return null;
		}

		IProtocolSession protocolSession = protocolSessionManager.getSessionById(sessionId, false);

		if (protocolSession == null) {
			printError("Unable to obtain a session with session id: " + sessionId);
			return null;
		}
		printSymbol(horizontalSeparator);
		printInfo("Protocol session context");
		for (String key : protocolSession.getSessionContext().getSessionParameters().keySet()) {
			printInfo(key + " = " + protocolSession.getSessionContext().getSessionParameters().get(key));
		}
		printSymbol(horizontalSeparator);
		printInfo("Protocol session self-description");

		printInfo(protocolSession.toString());
		printEndCommand();
		return null;
	}

}
