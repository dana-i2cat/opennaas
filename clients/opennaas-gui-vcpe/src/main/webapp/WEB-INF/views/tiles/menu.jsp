<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>


<ul id="_menu">
	<c:set var="userArea" value="/secure/vcpeNetwork" />
	<c:set var="nocArea" value="/secure/noc/vcpeNetwork" />
	<c:set var="adminArea" value="/secure/admin/vcpeNetwork" />
	<c:forEach varStatus="vs" items="${sessionScope.vcpeNetworkList}">
		<c:choose>
			<c:when test="${sessionScope.vcpeNetworkList[vs.index].templateType == 'sp_vcpe'}">
				<c:set var="templateUrl" value="singleProvider" />
			</c:when>
			<c:otherwise>
				<c:set var="templateUrl" value="multipleProvider" />
			</c:otherwise>
		</c:choose>
		<li><a href="#">${sessionScope.vcpeNetworkList[vs.index].name}</a>
			<ul>
				<c:if test="${sessionScope.vcpeNetworkList[vs.index].templateType == 'mp_vcpe'}">		
						<li>
							<a href="<c:url value="${userArea}/${templateUrl}/edit?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
								<spring:message code="menu.edit" />
							</a>
						</li>
						<sec:authorize access="hasRole('ROLE_ADMIN')">
							<li>
								<a class="link_confirm" href="<c:url value="${adminArea}/${templateUrl}/delete?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
									<spring:message code="menu.delete" />
								</a>
							</li>
						</sec:authorize>
				</c:if>
				<c:if test="${sessionScope.vcpeNetworkList[vs.index].templateType == 'sp_vcpe'}">	
					<sec:authorize access="hasRole('ROLE_NOC')">
						<li>
							<a href="<c:url value="${userArea}/${templateUrl}/edit?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
								<spring:message code="menu.edit" />
							</a>
						</li>
						<li>
							<a class="link_confirm" href="<c:url value="${adminArea}/${templateUrl}/delete?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
								<spring:message code="menu.delete" />
							</a>
						</li>
					</sec:authorize>
					<sec:authorize access="hasRole('ROLE_USER')">
							<li>
								<a href="<c:url value="${userArea}/${templateUrl}/updateIpsForm?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
									<spring:message code="menu.update" />
								</a>
							</li>
					</sec:authorize>
				</c:if>
			</ul>
		</li>
	</c:forEach>
</ul>

