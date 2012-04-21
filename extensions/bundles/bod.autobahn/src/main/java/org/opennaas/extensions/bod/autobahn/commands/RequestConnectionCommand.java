package org.opennaas.extensions.bod.autobahn.commands;

import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.opennaas.core.resources.action.ActionException;

import static com.google.common.base.Preconditions.checkState;

public class RequestConnectionCommand extends AutobahnCommand
{
	private final UserAccessPoint userAccessPoint;
	private final ServiceRequest serviceRequest;

	private String serviceId;

	public RequestConnectionCommand(UserAccessPoint userAccessPoint,
									ServiceRequest serviceRequest)
	{
		this.userAccessPoint = userAccessPoint;
		this.serviceRequest = serviceRequest;
	}

	@Override
	public void execute()
		throws ActionException
	{
		checkState(serviceId == null);

		try {
			for (ReservationRequest reservation: serviceRequest.getReservations()) {
				log.info("Submitting request for connection between " +
						 reservation.getStartPort().getAddress() + " and " +
						 reservation.getEndPort().getAddress());
			}

			serviceId = userAccessPoint.submitService(serviceRequest);

			log.info("Submitted service request " + serviceId);
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public void undo()
		throws ActionException
	{
		checkState(serviceId != null);

		log.info("Cancelling service request " + serviceId);
		try {
			userAccessPoint.cancelService(serviceId);
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		}
	}
}