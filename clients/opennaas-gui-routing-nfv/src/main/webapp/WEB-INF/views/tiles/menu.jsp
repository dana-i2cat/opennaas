<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>


<ul id="_menu">
    <li>
	<a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv4" />">
            <spring:message code="menu.RouteTable" />
    	</a>
        <ul>
            <li>
                <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv4" />">
                    IPv4 Table
                </a>
            </li>
            <li>
                <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=IPv6" />">
                    IPv6 Table
                </a>
            </li>
            <li>
                <a href="<c:url value="${url}/secure/noc/nfvRouting/getRouteTable?type=subnet" />">
                    Subnets Table
                </a>
            </li>
        </ul>
    </li>
    <li>
	<a href="<c:url value="${url}/secure/noc/nfvRouting/insertRoute" />">
            <spring:message code="menu.insertRoute" />
    	</a>
    </li>
    <li>
	<a href="<c:url value="${url}/secure/noc/nfvRouting/insertCtrlInfo" />">
            <spring:message code="menu.insertController" />
    	</a>
    </li>
    	<c:forEach varStatus="vs" items="${sessionScope.vcpeNetworkList}">
		<c:choose>
			<c:when test="${sessionScope.vcpeNetworkList[vs.index].templateType == 'sp_vcpe'}">				
				<c:set var="url" value="/secure/noc/vcpeNetwork/singleProvider" />
			</c:when>
			<c:otherwise>
				<c:set var="url" value="/secure/noc/vcpeNetwork/multipleProvider" />
			</c:otherwise>
		</c:choose>
		<li><a href="#">${sessionScope.vcpeNetworkList[vs.index].name}</a>
			<ul>
				<sec:authorize access="hasRole('ROLE_NOC')">
					<li>
						<a href="<c:url value="${url}/edit?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
							<spring:message code="menu.edit" />
						</a>
					</li>
					<li>
						<a class="link_confirm" href="<c:url value="${url}/delete?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
							<spring:message code="menu.delete" />
						</a>
					</li>
				</sec:authorize>

				
				<sec:authorize access="hasRole('ROLE_CLIENT')">
					<c:if test="${sessionScope.vcpeNetworkList[vs.index].templateType == 'sp_vcpe'}">				
						<li>
							<a href="<c:url value="${url}/updateIpsForm?vcpeNetworkId=${sessionScope.vcpeNetworkList[vs.index].id}" />">
								<spring:message code="menu.update" />
							</a>
						</li>
					</c:if>
				</sec:authorize>
			</ul>
		</li>
	</c:forEach>
</ul>

