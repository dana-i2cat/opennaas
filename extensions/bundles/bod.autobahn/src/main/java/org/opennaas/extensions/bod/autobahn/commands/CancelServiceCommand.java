package org.opennaas.extensions.bod.autobahn.commands;

import javax.xml.datatype.XMLGregorianCalendar;

import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.administration.ServiceType;
import net.geant.autobahn.useraccesspoint.Priority;
import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.ReservationResponse;
import net.geant.autobahn.useraccesspoint.Resiliency;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;
import net.geant.autobahn.useraccesspoint.ServiceResponse;
import net.geant.autobahn.useraccesspoint.State;

import static net.geant.autobahn.useraccesspoint.State.*;

import org.joda.time.DateTime;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.command.Response;

import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

public class CancelServiceCommand extends AutobahnCommand
{
	private final UserAccessPoint userAccessPoint;
	private final AutobahnLink link;
	private final String serviceId;

	private boolean cancelled = false;
	private boolean undone = false;

	public CancelServiceCommand(UserAccessPoint userAccessPoint,
								AutobahnLink link)
	{
		this.userAccessPoint = userAccessPoint;
		this.link = link;
		this.serviceId = link.getService().getBodID();

		setCommandId("cancel");
	}

	@Override
	public Response execute()
	{
		checkState(!cancelled && !undone);

		try {
			log.info("Cancelling reservation " + serviceId);

			userAccessPoint.cancelService(serviceId);
			cancelled = true;
			waitUntilOrFailure(CANCELLED);

			return okResponse("cancelService",
							  "Service " + serviceId + " cancelled");
		} catch (ActionException e) {
			return errorResponse("cancelService", e.getMessage());
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("cancelService", e.getMessage());
		}
	}

	@Override
	public Response undo()
	{
		checkState(!undone);

		if (!cancelled) {
			return okResponse("", "Nothing to undo");
		}

		try {
			Response response;
			if (!isBeforeNow(link.getReservation().getEndTime())) {
				String newServiceId =
					userAccessPoint.submitService(createServiceRequest());
				response =
					okResponse("submitService",
							   "Restored service " + serviceId + ". " +
							   "New service is " + newServiceId);
			} else {
				response =
					okResponse("submitService",
							   "Did not restore service " + serviceId +
							   "as it has expired already.");
			}
			undone = true;
				return response;
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("cancel", e.getMessage());
		}
	}

	private ServiceRequest createServiceRequest()
	{
		ReservationType reservation = link.getReservation();

		boolean processNow = isBeforeNow(reservation.getStartTime());

		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setStartPort(reservation.getStartPort());
		reservationRequest.setEndPort(reservation.getEndPort());
		reservationRequest.setStartTime(reservation.getStartTime());
		reservationRequest.setEndTime(reservation.getEndTime());
		reservationRequest.setDescription(reservation.getDescription());
		reservationRequest.setCapacity(reservation.getCapacity());
		reservationRequest.setBidirectional(reservation.isBidirectional());
		reservationRequest.setPriority(Priority.NORMAL);
		reservationRequest.setProcessNow(processNow);
		reservationRequest.setResiliency(Resiliency.NONE);

		ServiceType service = link.getService();
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setJustification(service.getJustification());
		serviceRequest.setUserName(service.getUser().getName());
		serviceRequest.getReservations().add(reservationRequest);

		return serviceRequest;
	}

	private boolean isBeforeNow(XMLGregorianCalendar calendar)
	{
		/* We add a 10 second margin to allow the request to be
		 * submitted and to allow minor clock skew.
		 */
		DateTime time = new DateTime(calendar.toGregorianCalendar());
		return time.isBefore(DateTime.now().plusSeconds(10));
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
					if (state.equals(CANCELLED)) {
						return;
					} else {
						throw new ActionException("Reservation cancelled: " +
											  reservation.getMessage());
					}
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