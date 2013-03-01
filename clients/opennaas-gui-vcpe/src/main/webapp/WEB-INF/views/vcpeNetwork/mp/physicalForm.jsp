<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasRole('ROLE_NOC')">
<%-- 	<form:form modelAttribute="physicalInfrastructure" action="logical" method="post"> --%>
<%-- 		<form:hidden  path="templateType"/> --%>
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
						<label><spring:message code="interface"/>:&nbsp;</label>
						<div class="ui-widget-content config_content">								
							<div>
								<b><spring:message code="interface"/>:&nbsp;</b>
								<div>ge-2/0/0</div>	
							</div>		
						</div>										
					</div>
					<div id="iface_router_up2" class="ui-widget-content">	
						<label><spring:message code="interface"/>:&nbsp;</label>
						<div class="ui-widget-content config_content">								
							<div>
								<b><spring:message code="interface"/>:&nbsp;</b>
								<div>ge-2/0/0</div>	
							</div>		
						</div>										
					</div>
					<!-- Router -->
					<div id="phy_router">
						<h3><spring:message code="mp.physical.physicalrouter"/></h3>
						<div>			
							<div id="config" class="ui-widget-content">
								<b><spring:message code="mp.physical.loopback"/></b><br>
								<div >Physical interface name: ge-2/0/0</div>
							</div>		
							<div id="config" class="ui-widget-content">
								<b><spring:message code="mp.physical.logicaltunnel"/></b><br>
								<div >Logical Tunel interface name: ge-2/0/0</div>
							</div>		
						</div>
					</div>
					<div id="iface_router_down" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b><spring:message code="interface"/>:&nbsp;</b>
								<div>ge-2/0/0</div>	
							</div>		
						</div>										
					</div>
				</div>
			</div>
		<div id="network_client">
			<h3>Client LAN</h3>
		</div>
	</div>

		<input id="submitButton" class="button" type="submit" value="<spring:message code="buttons.next"/>" />
<%-- 	</form:form> --%>
</sec:authorize>