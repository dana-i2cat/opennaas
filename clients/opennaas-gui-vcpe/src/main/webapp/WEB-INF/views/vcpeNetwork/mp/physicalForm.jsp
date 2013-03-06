<%@ page import="org.opennaas.extensions.vcpe.manager.templates.mp.TemplateConstants;"%>
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<c:set var="ROUTER_1_PHY_IFACE_UP1"  value='<%= TemplateConstants.ROUTER_1_PHY_IFACE_UP1 %>' />
<c:set var="ROUTER_1_PHY_IFACE_UP2"  value='<%= TemplateConstants.ROUTER_1_PHY_IFACE_UP2 %>' />

<sec:authorize access="hasRole('ROLE_NOC')">
	<form:form modelAttribute="physicalInfrastructure" action="logical" method="post">
		<form:hidden  path="templateType"/>
		<form:hidden  path="physicalRouter.name"/>
		<form:hidden  path="physicalRouter.templateName"/>
		<!-- Graphical view -->
		<div id="mpPhysicalForm">	
			<!-- Routers -->
			<div id="network_wan1">
				<h3><spring:message code="mp.physical.wan1"/></h3>
			</div>
			<div id="network_wan2">
				<h3><spring:message code="mp.physical.wan2"/></h3>
			</div>
				
			<div id="vcpe_mp_physical">	
				<h3>VCPE</h3>
				<div>
					<!-- WAN Interfaces -->
					<div id="iface_router_up1" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<c:forEach items="${physicalInfrastructure.physicalRouter.interfaces}" varStatus="vs" var="item">											
									<c:choose>
									<c:when test="${item.templateName == ROUTER_1_PHY_IFACE_UP1}">
											<div>
												<form:label path="physicalRouter.interfaces[${vs.index}].name" >${item.name}</form:label>									
												<form:hidden path="physicalRouter.interfaces[${vs.index}].type"/>
												<form:hidden path="physicalRouter.interfaces[${vs.index}].name"/>		
												<form:hidden path="physicalRouter.interfaces[${vs.index}].templateName"/>													
											</div>		
										</c:when>
									</c:choose>
								</c:forEach>
							</div>		
						</div>										
					</div>
					<div id="iface_router_up2" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<c:forEach items="${physicalInfrastructure.physicalRouter.interfaces}" varStatus="vs" var="item">											
									<c:choose>
										<c:when test="${item.templateName == ROUTER_1_PHY_IFACE_UP2}">
											<div>
												<form:label path="physicalRouter.interfaces[${vs.index}].name" >${item.name}</form:label>									 
												<form:hidden path="physicalRouter.interfaces[${vs.index}].type"/>
												<form:hidden path="physicalRouter.interfaces[${vs.index}].name"/>			
												<form:hidden path="physicalRouter.interfaces[${vs.index}].templateName"/>													
											</div>		
										</c:when>
									</c:choose>
								</c:forEach>	
							</div>		
						</div>										
					</div>
					<!-- Router -->
					<div id="phy_router">
						<h3><spring:message code="mp.physical.physicalrouter"/>: ${physicalInfrastructure.physicalRouter.name}</h3>
						<div>			
							<div id="config" class="ui-widget-content">
								<b><spring:message code="mp.physical.loopback"/></b><br>
								<div class="ui-widget-content config_content">	
									<c:forEach items="${physicalInfrastructure.physicalRouter.interfaces}" varStatus="vs" var="item">											
										<c:choose>
											<c:when test="${item.type == 'Loopback'}">
												<div>
													<form:label path="physicalRouter.interfaces[${vs.index}].name" >${item.name}</form:label>									
													<form:hidden path="physicalRouter.interfaces[${vs.index}].type"/>	
													<form:hidden path="physicalRouter.interfaces[${vs.index}].name"/>		
													<form:hidden path="physicalRouter.interfaces[${vs.index}].templateName"/>													
												</div>		
											</c:when>
										</c:choose>
									</c:forEach>
								</div>
							</div>		
							<div id="config" class="ui-widget-content">
								<b><spring:message code="mp.physical.logicaltunnel"/></b><br>
								<div class="ui-widget-content config_content">	
								<c:forEach items="${physicalInfrastructure.physicalRouter.interfaces}" varStatus="vs" var="item">											
									<c:choose>
										<c:when test="${item.type == 'Logicaltunnel'}">
											<div>
												<form:label path="physicalRouter.interfaces[${vs.index}].name" >${item.name}</form:label>									
												<form:hidden path="physicalRouter.interfaces[${vs.index}].type"/>
												<form:hidden path="physicalRouter.interfaces[${vs.index}].name"/>		
												<form:hidden path="physicalRouter.interfaces[${vs.index}].templateName"/>													
											</div>		
										</c:when>
									</c:choose>
								</c:forEach>
								</div>
							</div>		
						</div>
					</div>
					<div id="iface_router_down" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b><spring:message code="interface"/>:&nbsp;</b>
									<c:forEach items="${physicalInfrastructure.physicalRouter.interfaces}" varStatus="vs" var="item">											
									<c:choose>
										<c:when test="${item.type == 'Down'}">
											<div>
												<b><spring:message code="interface"/>:&nbsp;</b>
												<form:label path="physicalRouter.interfaces[${vs.index}].name" >${item.name}</form:label>								
												<form:hidden path="physicalRouter.interfaces[${vs.index}].type"/>
												<form:hidden path="physicalRouter.interfaces[${vs.index}].name"/>		
												<form:hidden path="physicalRouter.interfaces[${vs.index}].templateName"/>													
											</div>		
										</c:when>
									</c:choose>
								</c:forEach>
							</div>		
						</div>										
					</div>
				</div>
			</div>
		<div id="network_client">
			<h3><spring:message code="mp.physical.client"/></h3>
		</div>
	</div>

	<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.next"/>" />
	</form:form>
</sec:authorize>