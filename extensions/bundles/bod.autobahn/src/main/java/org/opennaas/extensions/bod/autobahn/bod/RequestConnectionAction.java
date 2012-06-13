package org.opennaas.extensions.bod.autobahn.bod;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import net.geant.autobahn.administration.Mode;
import net.geant.autobahn.useraccesspoint.Priority;
import net.geant.autobahn.useraccesspoint.PortType;
import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.Resiliency;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;

import org.joda.time.DateTime;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.commands.IAutobahnCommand;
import org.opennaas.extensions.bod.autobahn.commands.SubmitServiceCommand;
import org.opennaas.extensions.bod.autobahn.commands.Transaction;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.capability.l2bod.L2BoDActionSet;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;

import org.opennaas.extensions.network.model.topology.Interface;

public class RequestConnectionAction extends AutobahnAction
{
	public final static String ACTIONID = L2BoDActionSet.REQUEST_CONNECTION;

	public RequestConnectionAction()
	{
		setActionID(ACTIONID);
	}

	@Override
	public ActionResponse
		execute(IProtocolSessionManager protocolSessionManager)
		throws ActionException
	{
		try {
			UserAccessPoint userAccessPoint =
				getUserAccessPointService(protocolSessionManager);
			ServiceRequest serviceRequest =
				createServiceRequest((RequestConnectionParameters) params);
			IAutobahnCommand command =
				new SubmitServiceCommand(userAccessPoint, serviceRequest);

			Transaction.getInstance().add(command);

			return ActionResponse.okResponse(getActionID());
		} catch (DatatypeConfigurationException e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	protected ServiceRequest
		createServiceRequest(RequestConnectionParameters request)
		throws ActionException, DatatypeConfigurationException
	{
		if (!((AutobahnInterface) request.interface1).isLocal()) {
			throw new ActionException("First interface must be local to this Autobahn domain");
		}

		boolean processNow =
			request.startTime.isBefore(DateTime.now().plusSeconds(10));

		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setStartPort(getPortType(request.interface1, request.vlanid));
		reservationRequest.setEndPort(getPortType(request.interface2, request.vlanid));
		reservationRequest.setStartTime(toXMLCalendar(request.startTime));
		reservationRequest.setEndTime(toXMLCalendar(request.endTime));
		reservationRequest.setDescription("Submitted by OpenNaaS");
		reservationRequest.setCapacity(request.capacity);
		reservationRequest.setBidirectional(true);
		reservationRequest.setPriority(Priority.NORMAL);
		reservationRequest.setProcessNow(processNow);
		reservationRequest.setResiliency(Resiliency.NONE);

		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setJustification("Submitted by OpenNaaS");
		serviceRequest.setUserName(System.getProperty("user.name"));
		serviceRequest.getReservations().add(reservationRequest);

		return serviceRequest;
	}

	private PortType getPortType(Interface i, int vlan)
	{
		PortType port = ((AutobahnInterface) i).getPortType();
		if (vlan == -1) {
			port.setMode(Mode.UNTAGGED);
		} else {
			port.setMode(Mode.VLAN);
			port.setVlan(vlan);
		}
		return port;
	}

	private XMLGregorianCalendar toXMLCalendar(DateTime dt)
		throws DatatypeConfigurationException
	{
		DatatypeFactory factory = DatatypeFactory.newInstance();
		return factory.newXMLGregorianCalendar(dt.toGregorianCalendar());
	}
}