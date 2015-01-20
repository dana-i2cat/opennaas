package org.opennaas.extensions.genericnetwork.model.portstatistics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * #%L
 * OpenNaaS :: Generic Network
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

/**
 * 
 * @author Isart Canyameres Gimenez (i2cat)
 *
 */
@XmlRootElement(namespace = "opennaas.api")
@XmlAccessorType(XmlAccessType.FIELD)
public class TimePeriod {

	private long	init;
	private long	end;

	public TimePeriod() {
	}

	public TimePeriod(long init, long end) {
		super();
		this.init = init;
		this.end = end;
	}

	public long getInit() {
		return init;
	}

	public void setInit(long init) {
		this.init = init;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public boolean includes(long timestamp) {
		return (init <= timestamp && end >= timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + (int) (init ^ (init >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimePeriod other = (TimePeriod) obj;
		if (end != other.end)
			return false;
		if (init != other.init)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TimePeriod [init=" + init + ", end=" + end + "]";
	}
}
