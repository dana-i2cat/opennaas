package org.opennaas.extensions.bod.autobahn.model;

/*
 * #%L
 * OpenNaaS :: BoD :: Autobahn driver
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

import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.administration.ServiceType;

import org.opennaas.extensions.bod.autobahn.ReservationState;
import org.opennaas.extensions.bod.capability.l2bod.BoDLink;

public class AutobahnLink extends BoDLink
{
	private ServiceType		service;
	private ReservationType	reservation;

	public void setReservation(ReservationType reservation)
	{
		this.reservation = reservation;
	}

	public ReservationType getReservation()
	{
		return reservation;
	}

	public void setService(ServiceType service)
	{
		this.service = service;
	}

	public ServiceType getService()
	{
		return service;
	}

	@Override
	public String toString()
	{
		return "start=" + reservation.getStartTime() + "," +
				"end=" + reservation.getEndTime() + "," +
				"capacity=" + reservation.getCapacity() / 1000000.0 + "MB/s," +
				"state=" + ReservationState.valueOf(reservation.getState());
	}
}