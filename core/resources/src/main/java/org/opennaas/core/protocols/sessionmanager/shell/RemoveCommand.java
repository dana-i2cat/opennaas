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
import org.apache.felix.gogo.commands.Option;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * Remove a protocol session.
 * 
 * @author Pau Minoves
 * 
 */
@Command(scope = "protocols", name = "remove", description = "Removes a live connection from the pool, closing it.")
public class RemoveCommand extends GenericKarafCommand {

	@Option(name = "--all", aliases = { "-a" }, description = "Remove all active sessions.")
	boolean	optionAll;

	@Argument(name = "resourceType:resourceName", index = 0, required = true, description = "The resource owning the session.")
	String	resourceId;

	@Argument(name = "sessionId", index = 1, required = false, description = "The name of the session id that will be destroyed.")
	String	sessionId;

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("remove protocol");
		IProtocolManager protocolManager = getProtocolManager();
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

		IProtocolSessionManager sessionManager = protocolManager.getProtocolSessionManager(resourceIdentifier.getId());

		if (!optionAll && (sessionId == null || sessionId.contentEquals(""))) {
			printError("Either specify a session id or --all.");
		}
		if (optionAll) {
			for (String sessionID : sessionManager.getAllProtocolSessionIds()) {
				sessionManager.destroyProtocolSession(sessionID);
			}
		} else
			sessionManager.destroyProtocolSession(sessionId);
		printEndCommand();
		return null;
	}

}
