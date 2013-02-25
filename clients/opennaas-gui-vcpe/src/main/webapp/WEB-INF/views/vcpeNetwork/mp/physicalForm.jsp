<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasRole('ROLE_NOC')">
	<form:form modelAttribute="physicalInfrastructure" action="logical" method="post">
		<form:hidden  path="templateType"/>
		<form:hidden  path="physicalRouterMaster.name"/>
		<form:hidden  path="physicalRouterBackup.name"/>
		<!-- Graphical view -->
		<div id="home_body">
			<!-- WAN -->
			<div id="wan">
				<h2><spring:message code="physical.wan"/></h2>
				<div style="=width:780px;">
					<div id="cpe_core">
						<h3><spring:message code="physical.routerCore"/>: ${physicalInfrastructure.physicalRouterCore.name}</h3>
						<div>
							<div id="config" class="ui-widget-content">
								<c:forEach items="${physicalInfrastructure.physicalRouterCore.interfaces}" varStatus="vs" var="item">											
									<c:choose>
										<c:when test="${item.type == 'Loopback'}">
											<div>
												<b><spring:message code="interface"/>:&nbsp;</b>
												<form:label path="physicalRouterCore.interfaces[${vs.index}].name" >${item.name}</form:label>
												<form:hidden path="physicalRouterCore.interfaces[${vs.index}].name"/>										
												<form:hidden path="physicalRouterCore.interfaces[${vs.index}].type"/>		
												<form:hidden path="physicalRouterCore.interfaces[${vs.index}].templateName"/>													
											</div>		
										</c:when>
									</c:choose>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>	
			<!-- WAN Interfaces -->
			<div id="wan_master" class="ui-widget-content">	
				<label><spring:message code="physical.routerMaster.up"/></label>
				<div class="ui-widget-content config_content">								
					<c:forEach items="${physicalInfrastructure.physicalRouterMaster.interfaces}" varStatus="vs" var="item">											
						<c:choose>
							<c:when test="${item.type == 'Up'}">
								<div>
									<b><spring:message code="interface"/>:&nbsp;</b>
									<form:label path="physicalRouterMaster.interfaces[${vs.index}].name" >${item.name}</form:label>
									<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].name"/>
									<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].type"/>
									<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].templateName"/>			
								</div>		
							</c:when>
						</c:choose>
					</c:forEach>
				</div>										
			</div>
			<div id="wan_backup" class="ui-widget-content">				
				<label><spring:message code="physical.routerBackup.up"/></label>
				<div class="ui-widget-content config_content">
					<c:forEach items="${physicalInfrastructure.physicalRouterBackup.interfaces}" varStatus="vs" var="item">											
						<c:choose>
							<c:when test="${item.type == 'Up'}">
								<div>
									<b><spring:message code="interface"/>:&nbsp;</b>
									<form:label path="physicalRouterBackup.interfaces[${vs.index}].name" >${item.name}</form:label>
									<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].name"/>								
									<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].type"/>	
									<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].templateName"/>	
								</div>		
							</c:when>
						</c:choose>
					</c:forEach>
				</div>		
			</div>
			<!-- Routers -->
			<div id="cpe_master">
				<h3><spring:message code="physical.routerMaster"/>: ${physicalInfrastructure.physicalRouterMaster.name}</h3>
				<div>
					<div id="config" class="ui-widget-content">
						<c:forEach items="${physicalInfrastructure.physicalRouterMaster.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Loopback'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterMaster.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].type"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].templateName"/>
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>
				</div>
			</div>
			<div id="cpe_backup">
				<h3><spring:message code="physical.routerBackup"/>: ${physicalInfrastructure.physicalRouterBackup.name}</h3>
				<div>
					<div id="config" class="ui-widget-content">
						<c:forEach items="${physicalInfrastructure.physicalRouterBackup.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Loopback'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterBackup.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].type"/>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].templateName"/>										
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>
				</div>
			</div>
			<!-- VCPE Interfaces -->
			<div id="cpe_interfaces">
				<div id="cpe_client_master" class="ui-widget-content">				
				<label><spring:message code="physical.routerMaster.down"/></label>
					<div class="ui-widget-content config_content">
						<c:forEach items="${physicalInfrastructure.physicalRouterMaster.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Down'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterMaster.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].type"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].templateName"/>
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>	
				</div>
				<div id="cpe_inter_master" class="ui-widget-content">
				<label><spring:message code="physical.routerMaster.inter"/></label>
					<div class="ui-widget-content config_content">
						<c:forEach items="${physicalInfrastructure.physicalRouterMaster.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Inter'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterMaster.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].type"/>
										<form:hidden path="physicalRouterMaster.interfaces[${vs.index}].templateName"/>
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>
				</div>
				<div id="cpe_inter_backup" class="ui-widget-content">
				<label><spring:message code="physical.routerBackup.inter"/></label>
					<div class="ui-widget-content config_content">
						<c:forEach items="${physicalInfrastructure.physicalRouterBackup.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Inter'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterBackup.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].type"/>								
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].templateName"/>
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>
				</div>
				<div id="cpe_client_backup" class="ui-widget-content">
				<label><spring:message code="physical.routerBackup.down"/></label>
					<div class="ui-widget-content config_content">
						<c:forEach items="${physicalInfrastructure.physicalRouterBackup.interfaces}" varStatus="vs" var="item">											
							<c:choose>
								<c:when test="${item.type == 'Down'}">
									<div>
										<b><spring:message code="interface"/>:&nbsp;</b>
										<form:label path="physicalRouterBackup.interfaces[${vs.index}].name" >${item.name}</form:label>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].name"/>
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].type"/>										
										<form:hidden path="physicalRouterBackup.interfaces[${vs.index}].templateName"/>
									</div>		
								</c:when>
							</c:choose>
						</c:forEach>
					</div>
				</div>
			</div>
			<!--  End Routers -->
			<!-- Start BoD -->
			<div id="cpe_bod">
				<h2><spring:message code="bod"/></h2>
			</div>
			<!-- End BoD  -->
			<!-- Start client -->
			<div id="cpe_client">
				<h2>Client</h2>
			</div>
			<!-- End client -->
		</div>
		
		<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.next"/>" />
	</form:form>
</sec:authorize>