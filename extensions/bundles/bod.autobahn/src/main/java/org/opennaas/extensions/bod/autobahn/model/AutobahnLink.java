package org.opennaas.extensions.bod.autobahn.model;

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