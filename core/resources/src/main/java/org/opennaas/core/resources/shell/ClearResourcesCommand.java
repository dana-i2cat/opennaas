package org.opennaas.core.resources.shell;

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

import org.apache.felix.gogo.commands.Command;

@Command(scope = "resource", name = "clear", description = "Remove all resources in the platform (stopping active ones)")
public class ClearResourcesCommand extends GenericKarafCommand {

	@Override
	protected Object doExecute() throws Exception {
		printInitCommand("clear resources");
		try {
			getResourceManager().destroyAllResources();
		} catch (Exception e) {
			printError("Error clearing resources");
			printError(e);
		}
		printEndCommand();
		return null;
	}
}
