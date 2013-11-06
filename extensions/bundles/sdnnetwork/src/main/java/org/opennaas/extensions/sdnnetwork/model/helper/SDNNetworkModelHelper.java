package org.opennaas.extensions.sdnnetwork.model.helper;

import java.util.List;

import org.opennaas.extensions.sdnnetwork.model.NetworkConnection;
import org.opennaas.extensions.sdnnetwork.model.Route;
import org.opennaas.extensions.sdnnetwork.model.SDNNetworkOFFlow;

public abstract class SDNNetworkModelHelper {

	public static boolean compareFlowsWithoutIds(SDNNetworkOFFlow firstFlow, SDNNetworkOFFlow secondFlow) {

		if (!firstFlow.getPriority().equals(secondFlow.getPriority()))
			return false;

		if (!compareRoutesWithoutIds(firstFlow.getRoute(), secondFlow.getRoute()))
			return false;

		if (!firstFlow.getMatch().equals(secondFlow.getMatch()))
			return false;

		if (!firstFlow.getActions().equals(secondFlow.getActions()))
			return false;

		return true;
	}

	private static boolean compareRoutesWithoutIds(Route firstRoute, Route secondRoute) {

		List<NetworkConnection> firstConnections = firstRoute.getNetworkConnections();
		List<NetworkConnection> secondConnections = secondRoute.getNetworkConnections();

		if (firstConnections.size() != secondConnections.size())
			return false;

		for (NetworkConnection connection : firstConnections)

			if (getNetworkConnectionByComponentsValues(secondConnections, connection) == null)
				return false;

		return true;
	}

	private static NetworkConnection getNetworkConnectionByComponentsValues(List<NetworkConnection> secondConnections, NetworkConnection connection) {

		for (NetworkConnection netConnection : secondConnections) {

			if ((netConnection.getName().equals(connection.getName())) && (netConnection.getDestination().equals(connection.getDestination()) && (netConnection
					.getSource().equals(connection.getSource()))))
				return netConnection;
		}

		return null;
	}
}
