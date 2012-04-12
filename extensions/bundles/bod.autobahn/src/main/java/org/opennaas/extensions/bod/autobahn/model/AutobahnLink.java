package org.opennaas.extensions.bod.autobahn.model;

import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.administration.ServiceType;

import org.opennaas.extensions.network.model.topology.Link;

public class AutobahnLink extends Link
{
	private ServiceType service;
	private ReservationType reservation;

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
}