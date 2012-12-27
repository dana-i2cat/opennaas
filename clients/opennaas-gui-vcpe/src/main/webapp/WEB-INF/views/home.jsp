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
		<h2><spring:message code="home.wan"/></h2>
		<div style="=width:780px;">
			<div id="cpe_core">
				<h3><spring:message code="home.routerCore"/></h3>
				<div>
					<div id="config" class="ui-widget-content">
						<p><b>Interface:</b> lo0.100</p>
						<p><b>IP:</b> 193.1.190.141/30</p>
					</div>
				</div>
			</div>
		</div>
	</div>	
	<!-- WAN Interfaces -->
	<div id="wan_master" class="ui-widget-content">	
		<label>WAN Master</label>
		<div class="ui-widget-content config_content">
			<div><b>Interface:</b> <spring:message code="home.wan_master.interface"/></div>
			<br>
			<div><b>IP:</b> <spring:message code="home.wan_master.ip"/></div>
		</div>										
	</div>
	<div id="wan_backup" class="ui-widget-content">				
		<label>WAN Backup</label>	
		<div class="ui-widget-content config_content">
			<div><b>Interface:</b> <spring:message code="home.wan_backup.interface"/></div>
			<br>
			<div><b>IP:</b> <spring:message code="home.wan_backup.ip"/></div>
		</div>		
	</div>
	<!-- Routers -->
	<div id="cpe_master">
		<h3><spring:message code="home.routerMaster"/></h3>
		<div>
			<div id="config" class="ui-widget-content">
				<p><b>Interface:</b> lo0.100</p>
				<p><b>IP:</b> 193.1.190.141/30</p>
			</div>
		</div>
	</div>
	<div id="cpe_backup">
		<h3><spring:message code="home.routerBackup"/></h3>
		<div>
			<div id="config" class="ui-widget-content">
				<p><b>Interface:</b> lo0.0</p>
				<p><b>IP:</b> 193.1.190.141/30</p>
			</div>
		</div>
	</div>
	<!-- VCPE Interfaces -->
	<div id="cpe_interfaces">
		<div id="cpe_client_master" class="ui-widget-content">				
			<label>Client Master</label>
			<div class="ui-widget-content config_content">
				<div><b>Interface:</b> <spring:message code="home.cpe_client_master.interface"/> </div>
				<br>
				<div><b>IP:</b> <spring:message code="home.cpe_client_master.ip"/></div>
			</div>	
		</div>
		<div id="cpe_inter_master" class="ui-widget-content">
			<label>Inter Master</label>
			<div class="ui-widget-content config_content">
				<div><b>Interface:</b> <spring:message code="home.cpe_inter_master.interface"/></div>
				<br>
				<div><b>IP:</b> <spring:message code="home.cpe_inter_master.ip"/></div>
			</div>
		</div>
		<div id="cpe_inter_backup" class="ui-widget-content">
			<label>Inter Backup</label>
			<div class="ui-widget-content config_content">
				<div><b>Interface:</b> <spring:message code="home.cpe_inter_backup.interface"/></div>
				<br>
				<div><b>IP:</b> <spring:message code="home.cpe_inter_backup.ip"/></div>
			</div>
		</div>
		<div id="cpe_client_backup" class="ui-widget-content">
			<label>Client Backup</label>
			<div class="ui-widget-content config_content">
				<div><b>Interface:</b> <spring:message code="home.cpe_client_backup.interface"/></div>
				<br>
				<div><b>IP:</b> <spring:message code="home.cpe_client_backup.ip"/></div>
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