package org.opennaas.extensions.bod.autobahn.commands;

import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.command.Response;

import static com.google.common.base.Preconditions.checkState;

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
			return okResponse("submitService",
							  "Service " + serviceId + " submitted");
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("submit", e.getMessage());
		}
	}

	@Override
	public Response undo()
	{
		checkState(serviceId != null);

		try {
			userAccessPoint.cancelService(serviceId);
			return okResponse("cancelService",
							  "Service " + serviceId + " cancelled");
		} catch (UserAccessPointException_Exception e) {
			return errorResponse("submit", e.getMessage());
		}
	}
}