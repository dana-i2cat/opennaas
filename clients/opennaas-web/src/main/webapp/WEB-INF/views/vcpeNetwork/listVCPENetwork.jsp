<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<table>
	<thead>
		<tr>
			<th>Name</th>
			<th>Logical Router 1</th>
			<th>Logical Router 2</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach varStatus="vs" items="${vcpeNetworkList}">
			<tr>
				<td>${vcpeNetworkList[vs.index].name}</td>
				<td>${vcpeNetworkList[vs.index].logicalRouter1.name}</td>
				<td>${vcpeNetworkList[vs.index].logicalRouter2.name}</td>

				<td><a href="<c:url value="/secure/vcpeNetwork/view?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">View</a></td>				
				<sec:authorize access="hasRole('ROLE_CLIENT')">
					<sec:authorize access="!hasRole('ROLE_NOC')">
						<td><a href="<c:url value="/secure/vcpeNetwork/updateIpsForm?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">Update Ip's</a></td>
					</sec:authorize>		
				</sec:authorize>				
				<sec:authorize access="hasRole('ROLE_NOC')">
					<td><a href="<c:url value="/secure/noc/vcpeNetwork/edit?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">Edit</a></td>
					<td><a href="<c:url value="/secure/noc/vcpeNetwork/delete?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">Delete</a></td>
				</sec:authorize>
			</tr>
		</c:forEach>
	</tbody>
</table>