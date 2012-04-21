package org.opennaas.extensions.bod.autobahn.commands;

import javax.xml.datatype.XMLGregorianCalendar;

import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.administration.ServiceType;
import net.geant.autobahn.useraccesspoint.Priority;
import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.Resiliency;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.joda.time.DateTime;

import org.opennaas.core.resources.action.ActionException;

import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.autobahn.model.AutobahnLink;

import static com.google.common.base.Preconditions.checkState;

public class ShutdownConnectionCommand extends AutobahnCommand
{
	private final UserAccessPoint userAccessPoint;
	private final AutobahnLink link;
	private final String serviceId;

	private boolean cancelled = false;
	private boolean undone = false;

	public ShutdownConnectionCommand(UserAccessPoint userAccessPoint,
									 AutobahnLink link)
	{
		this.userAccessPoint = userAccessPoint;
		this.link = link;
		this.serviceId = link.getService().getBodID();
	}

	@Override
	public void execute()
		throws ActionException
	{
		checkState(!cancelled && !undone);

		try {
			log.info("Cancelling reservation " + serviceId);

			userAccessPoint.cancelService(serviceId);
			cancelled = true;

			log.info("Cancelled service request " + serviceId);
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		}
	}

	@Override
	public void undo()
		throws ActionException
	{
		checkState(cancelled && !undone);
		try {
			ServiceRequest request = createServiceRequest();
			if (!isBeforeNow(link.getReservation().getEndTime())) {
				String newServiceId =
					userAccessPoint.submitService(request);
				log.warn("Restored cancelled service. New service ID is " +
						 newServiceId);
			} else {
				log.info("Did not restore cancelled service " + serviceId +
						 " as it had expired anyway");
			}
			undone = true;
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
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
}