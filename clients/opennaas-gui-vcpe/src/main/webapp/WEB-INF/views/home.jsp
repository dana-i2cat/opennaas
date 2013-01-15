<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasRole('ROLE_NOC')">			
			
<!-- Graphical view -->
<div id="home_body">
	<!-- WAN -->
	<div id="wan">
		<h2><spring:message code="physical.wan"/></h2>
		<div style="=width:780px;">
			<div id="cpe_core">
				<h3><spring:message code="physical.routerCore"/></h3>
				<div>
					<div id="config" class="ui-widget-content">
						<p><b><spring:message code="interface"/>:</b> lo0.100</p>
					</div>
				</div>
			</div>
		</div>
	</div>	
	<!-- WAN Interfaces -->
	<div id="wan_master" class="ui-widget-content">	
		<label><spring:message code="physical.routerMaster.up"/></label>
		<div class="ui-widget-content config_content">
			<div><b><spring:message code="interface"/>:</b> <spring:message code="physical.wan_master.interface"/></div>
		</div>										
	</div>
	<div id="wan_backup" class="ui-widget-content">				
		<label><spring:message code="physical.routerBackup.up"/></label>
		<div class="ui-widget-content config_content">
			<div><b><spring:message code="interface"/>:</b> <spring:message code="physical.wan_backup.interface"/></div>
		</div>		
	</div>
	<!-- Routers -->
	<div id="cpe_master">
		<h3><spring:message code="physical.routerMaster"/></h3>
		<div>
			<div id="config" class="ui-widget-content">
				<p><b><spring:message code="interface"/>:</b> lo0.100</p>
			</div>
		</div>
	</div>
	<div id="cpe_backup">
		<h3><spring:message code="physical.routerBackup"/></h3>
		<div>
			<div id="config" class="ui-widget-content">
				<p><b><spring:message code="interface"/>:</b> lo0.0</p>
			</div>
		</div>
	</div>
	<!-- VCPE Interfaces -->
	<div id="cpe_interfaces">
		<div id="cpe_client_master" class="ui-widget-content">				
		<label><spring:message code="physical.routerMaster.down"/></label>
			<div class="ui-widget-content config_content">
				<div><b><spring:message code="interface"/>:</b> <spring:message code="physical.cpe_client_master.interface"/> </div>
			</div>	
		</div>
		<div id="cpe_inter_master" class="ui-widget-content">
		<label><spring:message code="physical.routerMaster.inter"/></label>
			<div class="ui-widget-content config_content">
				<div><b><spring:message code="interface"/>:</b> <spring:message code="physical.cpe_inter_master.interface"/></div>
			</div>
		</div>
		<div id="cpe_inter_backup" class="ui-widget-content">
		<label><spring:message code="physical.routerBackup.inter"/></label>
			<div class="ui-widget-content config_content">
				<div><b><spring:message code="interface"/>:</b><spring:message code="physical.cpe_inter_backup.interface"/></div>
			</div>
		</div>
		<div id="cpe_client_backup" class="ui-widget-content">
		<label><spring:message code="physical.routerBackup.down"/></label>
			<div class="ui-widget-content config_content">
				<div><b><spring:message code="interface"/>:</b> <spring:message code="physical.cpe_client_backup.interface"/></div>
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
</sec:authorize>


<sec:authorize access="hasRole('ROLE_CLIENT')">
	<div>&nbsp;</div>
</sec:authorize>