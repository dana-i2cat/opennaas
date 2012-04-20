package org.opennaas.extensions.bod.autobahn.bod;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConfigurationException;

import net.geant.autobahn.useraccesspoint.Priority;
import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.Resiliency;
import net.geant.autobahn.useraccesspoint.ServiceRequest;
import net.geant.autobahn.useraccesspoint.UserAccessPoint;
import net.geant.autobahn.useraccesspoint.UserAccessPointException_Exception;

import org.opennaas.core.resources.action.ActionException;
import org.opennaas.core.resources.action.ActionResponse;
import org.opennaas.core.resources.protocol.IProtocolSessionManager;
import org.opennaas.core.resources.protocol.ProtocolException;

import org.opennaas.extensions.bod.actionsets.dummy.ActionConstants;
import org.opennaas.extensions.bod.autobahn.AutobahnAction;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;

import org.opennaas.extensions.network.model.topology.Interface;

public class RequestConnectionAction extends AutobahnAction
{
	public final static String ACTIONID = ActionConstants.REQUESTCONNECTION;

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
			log.info("Requesting connection between " + params);

			UserAccessPoint userAccessPoint =
				getUserAccessPointService(protocolSessionManager);

			String serviceId =
				userAccessPoint.submitService(createServiceRequest());

			log.info("Submitted service request " + serviceId);

			return ActionResponse.okResponse(getActionID());
		} catch (DatatypeConfigurationException e) {
			throw new ActionException(e);
		} catch (UserAccessPointException_Exception e) {
			throw new ActionException(e);
		} catch (ProtocolException e) {
			throw new ActionException(e);
		}
	}

	protected AutobahnInterface getInterface(int index)
	{
		List<Interface> interfaces = (List<Interface>) params;
		return (AutobahnInterface) interfaces.get(index);
	}

	protected ServiceRequest createServiceRequest()
		throws DatatypeConfigurationException
	{
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar tomorrow = new GregorianCalendar();
		tomorrow.add(GregorianCalendar.DAY_OF_MONTH, 1);

		DatatypeFactory factory = DatatypeFactory.newInstance();

		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setStartPort(getInterface(0).getPortType());
		reservationRequest.setEndPort(getInterface(1).getPortType());
		reservationRequest.setStartTime(factory.newXMLGregorianCalendar(now));
		reservationRequest.setEndTime(factory.newXMLGregorianCalendar(tomorrow));
		reservationRequest.setDescription("Submitted by OpenNaaS");
		reservationRequest.setCapacity(10000000);
		reservationRequest.setBidirectional(true);
		reservationRequest.setPriority(Priority.NORMAL);
		reservationRequest.setProcessNow(true);
		reservationRequest.setResiliency(Resiliency.NONE);

		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setJustification("Submitted by OpenNaaS");
		serviceRequest.setUserName(System.getProperty("user.name"));
		serviceRequest.getReservations().add(reservationRequest);

		return serviceRequest;
	}
}