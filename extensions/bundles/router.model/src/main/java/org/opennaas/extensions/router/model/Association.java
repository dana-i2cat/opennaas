package org.opennaas.extensions.router.model;

/*
 * #%L
 * OpenNaaS :: CIM Model
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

import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

@XmlSeeAlso({
		Component.class,
		Dependency.class
})
public class Association {

	private ManagedElement	from, to;

	public void setFrom(ManagedElement from) {
		this.from = from;
	}

	@XmlTransient
	public ManagedElement getFrom() {
		return from;
	}

	public void setTo(ManagedElement to) {
		this.to = to;
	}

	public ManagedElement getTo() {
		return to;
	}

	// public static Association link(ManagedElement from, ManagedElement to) {
	// Association assoc = new Association();
	//
	// assoc.setFrom(from);
	// assoc.setTo(to);
	//
	// assoc.getFrom().addToAssociation(assoc);
	// assoc.getTo().addFromAssociation(assoc);
	//
	// return assoc;
	// };

	public static Association link(Class<? extends Association> clazz, ManagedElement from, ManagedElement to) {
		Association assoc = null;

		try {
			assoc = clazz.newInstance();

			assoc.setFrom(from);
			assoc.setTo(to);

			assoc.getFrom().addToAssociation(assoc);
			assoc.getTo().addFromAssociation(assoc);

			return assoc;

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	};

	/**
	 * Remove this association from both ManagedElements. Also, this association object becomes unusable and must be dropped. Use link() to
	 * reestablish the association.
	 */
	public void unlink() {
		assert this.to != null;
		assert this.from != null;

		this.getTo().removeFromAssociation(this);
		this.getFrom().removeToAssociation(this);

		// Lets free references so we make this association unusable (and GC can do its stuff).
		this.to = null;
		this.from = null;
	}
}
