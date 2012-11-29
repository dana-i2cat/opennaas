<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!-- Graphical view -->
<div id="home_body">
	<!-- WAN -->
	<div id="wan">
		<h2><spring:message code="home.wan"/></h2>
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
	<!-- WAN Interfaces -->
	<div id="wan_master" class="ui-widget-content">	
		<label>WAN Master</label>
		<div class="ui-widget-content config_content">
			<p><b>Interface:</b> ge-2/0/0.12</p>
			<p><b>IP:</b> 193.1.190.133/30</p>
		</div>										
	</div>
	<div id="wan_backup" class="ui-widget-content">				
		<label>WAN Backup</label>	
		<div class="ui-widget-content config_content">
			<p><b>Interface:</b> ge-1/0/7.76</p>
			<p><b>IP:</b> 193.1.190.129/30</p>
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
				<p><b>Interface:</b> </p>
				<p><b>IP:</b> 193.1.190.162/28</p>
			</div>	
		</div>
		<div id="cpe_inter_master" class="ui-widget-content">
			<label>Inter Master</label>
			<div class="ui-widget-content config_content">
				<p><b>Interface:</b> ge-2/0/1.70</p>
				<p><b>IP:</b> 193.1.190.137/30</p>
			</div>
		</div>
		<div id="cpe_inter_backup" class="ui-widget-content">
			<label>Inter Backup</label>
			<div class="ui-widget-content config_content">
				<p><b>Interface:</b> ge-1/0/7.75</p>
				<p><b>IP:</b> 193.1.190.137/30</p>
			</div>
		</div>
		<div id="cpe_client_backup" class="ui-widget-content">
			<label>Client Backup</label>
			<div class="ui-widget-content config_content">
				<p><b>Interface:</b> ge-1/0/7.75</p>
				<p><b>IP:</b> 193.1.190.163/28</p>
			</div>
		</div>
	</div>
	<!--  End Routers -->
	<!-- Start BoD -->
	<div id="cpe_bod">
		<h2><spring:message code="bod"/></h2>
		<div>&nbsp;</div>
	</div>
	<!-- End BoD  -->
	<!-- Start client -->
	<div id="cpe_client">
		<h2>Client</h2>
		<div class="ui-widget-content">
			&nbsp;
		</div>
	</div>
	<!-- End client -->
</div>