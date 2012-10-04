<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<table>
	<thead>
		<tr>
			<th>Id</th>
			<th>Name</th>
			<th>Logical Router 1</th>
			<th>Logical Router 2</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach varStatus="vs" items="${vcpeNetworkList}">
			<tr>
				<td>${vcpeNetworkList[vs.index].id}</td>
				<td>${vcpeNetworkList[vs.index].name}</td>
				<td>${vcpeNetworkList[vs.index].logicalRouter1.name}</td>
				<td>${vcpeNetworkList[vs.index].logicalRouter2.name}</td>
				
				<td><a href="<c:url value="/vcpeNetwork/edit?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">Edit</a></td>
				<td><a href="<c:url value="/vcpeNetwork/delete?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">Delete</a></td>
			</tr>
		</c:forEach>
	</tbody>
</table>