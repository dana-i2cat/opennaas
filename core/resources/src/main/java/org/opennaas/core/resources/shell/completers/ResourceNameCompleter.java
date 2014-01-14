package org.opennaas.core.resources.shell.completers;

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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.karaf.shell.console.Completer;
import org.apache.karaf.shell.console.completer.StringsCompleter;
import org.opennaas.core.resources.Activator;
import org.opennaas.core.resources.IResource;

public class ResourceNameCompleter implements Completer {

	private Log	log	= LogFactory.getLog(ResourceNameCompleter.class);

	@Override
	public int complete(String buffer, int cursor, List<String> candidates) {

		StringsCompleter delegate = new StringsCompleter();

		try {
			List<IResource> list = Activator.getResourceManagerService().listResources();

			for (IResource resource : list) {
				String value = resource.getResourceDescriptor().getInformation().getType()
						+ ":" + resource.getResourceDescriptor().getInformation().getName();
				delegate.getStrings().add(value);
			}
		} catch (Exception e) {
			// log exception and ignore it
			// (completer would have no options for completion))
			log.error(e);
		}

		return delegate.complete(buffer, cursor, candidates);
	}
}
