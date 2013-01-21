<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>


<ul id="_menu">
	<sec:authorize access="hasRole('ROLE_NOC')">
		<li><a href="#"><spring:message code="menu.create" /></a>
			<ul>
				<li>
					<a href="<c:url value="/secure/noc/vcpeNetwork/physical?templateType=template.vcpe" />">
						<spring:message code="menu.vcpe" />
					</a>
				</li>
				<li>
					<a class="link_confirm" href="<c:url value="/secure/noc/vcpeNetwork/physical?templateType=template.unic" />">
						<spring:message code="menu.unic" />
					</a>
				</li>
			</ul>
		</li>	
		<hr/>
	</sec:authorize>
	
	<c:forEach varStatus="vs" items="${vcpeNetworkList}">
		<li><a href="#">${vcpeNetworkList[vs.index].name}</a>
			<ul>
				<sec:authorize access="hasRole('ROLE_NOC')">
					<li>
						<a href="<c:url value="/secure/noc/vcpeNetwork/edit?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">
							<spring:message code="menu.edit" />
						</a>
					</li>
					<li>
						<a class="link_confirm" href="<c:url value="/secure/noc/vcpeNetwork/delete?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">
							<spring:message code="menu.delete" />
						</a>
					</li>
				</sec:authorize>

				<sec:authorize access="hasRole('ROLE_CLIENT')">
					<li>
						<a href="<c:url value="/secure/vcpeNetwork/updateIpsForm?vcpeNetworkId=${vcpeNetworkList[vs.index].id}" />">
							<spring:message code="menu.update" />
						</a>
					</li>
				</sec:authorize>
			</ul>
		</li>
		<li class="ui-icon ui-icon-circle-plus" style="margin-top: 4px; margin-left: 4px;">
			Icon
		</li>
	</c:forEach>
</ul>

