package org.opennaas.extensions.bod.autobahn.commands;

import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.ReservationResponse;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.ServiceResponse;
import net.geant.autobahn.useraccesspoint.State;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.command.Response;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;
import static net.geant.autobahn.useraccesspoint.State.*;

public class SubmitServiceCommand extends AutobahnCommand
{
	private final UserAccessPoint userAccessPoint;
	private final ServiceRequest serviceRequest;

	private String serviceId;

	public SubmitServiceCommand(UserAccessPoint userAccessPoint,
								ServiceRequest serviceRequest)
	{
		this.userAccessPoint = userAccessPoint;
		this.serviceRequest = serviceRequest;
		setCommandId("submit");
	}

	@Override
	public Response execute()
	{
		checkState(serviceId == null);

		try {
			serviceId = userAccessPoint.submitService(serviceRequest);

			if (getOnlyElement(serviceRequest.getReservations()).isProcessNow()) {
				waitUntilOrFailure(ACTIVE);
			} else {
				waitUntilOrFailure(SCHEDULED);
			}

			return okResponse("submitService",
							  "Service " + serviceId + " submitted");
		} catch (ActionException e) {
			return errorResponse("submit", e.getMessage());
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("submit", e.getMessage());
		}
	}

	@Override
	public Response undo()
	{
		if (serviceId == null) {
			return okResponse("", "Nothing to undo");
		}

		try {
			userAccessPoint.cancelService(serviceId);
			return okResponse("cancelService",
							  "Service " + serviceId + " cancelled");
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("submit", e.getMessage());
		}
	}

	private void waitUntilOrFailure(State state)
		throws ActionException, UserAccessPointException_Exception
	{
		log.debug("Waiting for Service " + serviceId + " to be " + state);
		State lastState = State.ACCEPTED;
		while (true) {
			ReservationResponse reservation = getReservation();
			if (! (reservation.getState().equals(lastState))) {
				lastState = reservation.getState();
				log.debug("Service " + serviceId + " state updated to " + lastState);
			}
			switch (reservation.getState()) {
			case CANCELLED:
				throw new ActionException("Reservation cancelled: " +
										  reservation.getMessage());
			case FAILED:
				throw new ActionException("Reservation failed: " +
										  reservation.getMessage());
			default:
				break;
			}

			if (reservation.getState().ordinal() >= state.ordinal()) {
				return;
			}

			try {
				Thread.currentThread().sleep(500);
			} catch (InterruptedException e) {
				throw new ActionException("Reservation was interrupted", e);
			}
		}
	}

	private ReservationResponse getReservation()
		throws UserAccessPointException_Exception
	{
		ServiceResponse service = userAccessPoint.queryService(serviceId);
		return getOnlyElement(service.getReservations());
	}
}