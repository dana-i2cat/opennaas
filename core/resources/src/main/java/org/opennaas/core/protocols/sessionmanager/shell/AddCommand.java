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
import org.opennaas.core.protocols.sessionmanager.ProtocolSessionManager;
import org.opennaas.core.resources.IResourceIdentifier;
import org.opennaas.core.resources.IResourceManager;
import org.opennaas.core.resources.protocol.IProtocolManager;
import org.opennaas.core.resources.protocol.IProtocolSession;
import org.opennaas.core.resources.protocol.ProtocolException;
import org.opennaas.core.resources.shell.GenericKarafCommand;

/**
 * @author Pau Minoves
 */
@Command(scope = "protocols", name = "add", description = "Brings up a live connection from the pool with the given protocol if there is none.")
public class AddCommand extends GenericKarafCommand {

	@Argument(name = "resourceType:resourceName", required = true, index = 0, description = "The resource owning the session to create.")
	String	resourceId;

	@Argument(name = "protocol", index = 1, required = true, description = "The protocol to use. This argument defines the factory and context used to create this session.")
	String	protocol;

	@Override
	protected Object doExecute() throws Exception {

		IResourceManager manager = getResourceManager();

		printInitCommand("adding protocol");

		String[] argsRouterName = new String[2];
		try {
			argsRouterName = splitResourceName(resourceId);
		} catch (Exception e) {
			printError(e.getMessage());
			printEndCommand();
			return -1;
		}
		IResourceIdentifier resourceIdentifier = manager.getIdentifierFromResourceName(argsRouterName[0], argsRouterName[1]);

		IProtocolManager protocolManager = getProtocolManager();
		ProtocolSessionManager sessionManager = (ProtocolSessionManager) protocolManager.getProtocolSessionManager(resourceIdentifier.getId());
		IProtocolSession availableSession;
		try {
			availableSession = sessionManager.obtainSessionByProtocol(protocol, false);
			printInfo("Available session: " + availableSession.getSessionId());
		} catch (ProtocolException e) {
			printError("Faild to add session", e);
		}
		printEndCommand();
		return null;
	}
}
