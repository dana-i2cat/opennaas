package org.opennaas.extensions.bod.autobahn.bod;

import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.geant.autobahn.administration.Mode;
import net.geant.autobahn.administration.ReservationType;
import net.geant.autobahn.useraccesspoint.PortType;
import net.geant.autobahn.useraccesspoint.Priority;
import net.geant.autobahn.useraccesspoint.ReservationRequest;
import net.geant.autobahn.useraccesspoint.Resiliency;
import net.geant.autobahn.useraccesspoint.ServiceRequest;

import org.joda.time.DateTime;
import org.opennaas.core.resources.action.ActionException;
import org.opennaas.extensions.bod.autobahn.model.AutobahnInterface;
import org.opennaas.extensions.bod.capability.l2bod.RequestConnectionParameters;
import org.opennaas.extensions.network.model.topology.Interface;

public class ParameterTranslator {

	public static ServiceRequest
			createServiceRequest(RequestConnectionParameters request)
					throws ActionException, DatatypeConfigurationException
	{
		if (!((AutobahnInterface) request.interface1).isLocal()) {
			throw new ActionException("First interface must be local to this Autobahn domain");
		}

		boolean processNow =
				request.startTime.isBefore(DateTime.now().plusSeconds(10));

		ReservationRequest reservationRequest = new ReservationRequest();
		reservationRequest.setStartPort(getPortType((AutobahnInterface) request.interface1, request.vlanid1));
		reservationRequest.setEndPort(getPortType((AutobahnInterface) request.interface2, request.vlanid2));
		reservationRequest.setStartTime(ParameterTranslator.toXMLCalendar(request.startTime));
		reservationRequest.setEndTime(ParameterTranslator.toXMLCalendar(request.endTime));
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

	public static RequestConnectionParameters createRequestParameters(ReservationType reservationRequest, List<AutobahnInterface> interfaces) {

		Interface interface1 = ParameterTranslator.getAutobahnInterfaceFromPortType(reservationRequest.getStartPort(), interfaces);
		Interface interface2 = ParameterTranslator.getAutobahnInterfaceFromPortType(reservationRequest.getEndPort(), interfaces);
		int srcVlanId = -1;
		if (reservationRequest.getUserStartVlan() != 0)
			srcVlanId = reservationRequest.getUserStartVlan();
		int dstVlanId = -1;
		if (reservationRequest.getUserEndVlan() != 0)
			dstVlanId = reservationRequest.getUserEndVlan();
		long capacity = reservationRequest.getCapacity();
		DateTime startTime = ParameterTranslator.toDateTime(reservationRequest.getStartTime());
		DateTime endTime = ParameterTranslator.toDateTime(reservationRequest.getEndTime());

		RequestConnectionParameters parameters = new RequestConnectionParameters(interface1, interface2, capacity, srcVlanId, dstVlanId, startTime,
				endTime);
		return parameters;
	}

	private static PortType getPortType(AutobahnInterface i, int vlan)
	{
		PortType originalPort = i.getPortType();

		PortType port = new PortType();
		port.setAddress(originalPort.getAddress());
		port.setDescription(originalPort.getDescription());
		port.setIsClient(originalPort.isIsClient());
		port.setIsIdcp(originalPort.isIsIdcp());

		if (vlan == -1) {
			port.setMode(Mode.UNTAGGED);
		} else {
			port.setMode(Mode.VLAN);
			port.setVlan(vlan);
		}
		return port;
	}

	public static Interface getAutobahnInterfaceFromPortType(PortType port, List<AutobahnInterface> interfaces) {
		for (AutobahnInterface iface : interfaces) {
			if (areEquals(iface.getPortType(), port)) {
				return iface;
			}
		}
		return null;
	}

	private static boolean areEquals(PortType one, PortType other) {

		if (one == null) {
			if (other != null) {
				return false;
			} else {
				return true;
			}
		} else if (other == null) {
			return false;
		}

		if (one.getAddress() == null && other.getAddress() != null)
			return false;
		if (one.getAddress() != null)
			if (!one.getAddress().equals(other.getAddress()))
				return false;

		if (one.getMode() == null && other.getMode() != null)
			return false;
		if (one.getMode() != null)
			if (!one.getMode().equals(other.getMode()))
				return false;

		// if (one.getVlan() != other.getVlan())
		// return false;

		if (one.getDescription() == null && other.getDescription() != null)
			return false;
		if (one.getDescription() != null)
			if (!one.getDescription().equals(other.getDescription()))
				return false;

		// if (one.isIsIdcp() != other.isIsIdcp())
		// return false;
		//
		// if (one.isIsClient() != other.isIsClient())
		// return false;

		return true;
	}

	public static AutobahnInterface createInterface(PortType port, boolean isLocal)
	{
		AutobahnInterface i = new AutobahnInterface();
		i.setPortType(port);
		i.setName(port.getAddress());
		i.setLocal(isLocal);
		return i;
	}

	public static int getVlan(PortType port) {
		if (port.getMode() == null)
			return port.getVlan();

		if (port.getMode().equals(Mode.UNTAGGED))
			return -1;

		return port.getVlan();
	}

	public static XMLGregorianCalendar toXMLCalendar(DateTime dt)
			throws DatatypeConfigurationException
	{
		DatatypeFactory factory = DatatypeFactory.newInstance();
		return factory.newXMLGregorianCalendar(dt.toGregorianCalendar());
	}

	public static DateTime toDateTime(XMLGregorianCalendar calendar)
	{
		return new DateTime(calendar.toGregorianCalendar().getTime());
	}

}
