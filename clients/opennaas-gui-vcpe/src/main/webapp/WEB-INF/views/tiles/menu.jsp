<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib  uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ul>
	<sec:authorize access="hasRole('ROLE_NOC')">
		<li>
			<a href="<c:url value="/secure/noc/vcpeNetwork/create" />">
				<spring:message code="index.menu.vcpenetwork.create"/>
			</a>
		</li>
	</sec:authorize>
	<li>
		<a href="<c:url value="/secure/vcpeNetwork/list" />">
			<spring:message code="index.menu.vcpenetwork.list"/>
		</a>
	</li>
</ul>
