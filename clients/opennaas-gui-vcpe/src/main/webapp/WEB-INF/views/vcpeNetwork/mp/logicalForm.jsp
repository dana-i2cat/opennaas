<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<sec:authorize access="hasRole('ROLE_NOC')">
	<form:form modelAttribute="logicalInfrastructure" action="logical" method="post">
			<form:hidden  path="templateType"/>
			<!-- Graphical view -->
			<div id="mpLogicalForm">	
				<!-- Wan 1 -->
				<div id="network_wan1">
					<h3>WAN 1</h3>
					<div>			
						<div id="config" class="ui-widget-content">
							<form:label for="" path="" cssErrorClass="error">
								AS NUM
							</form:label>
							<form:input path="" size="8"/><br>
							<form:label for="" path="" cssErrorClass="error">
								IP Address Range
							</form:label>
							<form:input path="" size="8"/>
						</div>		
					</div>
				</div>
				<!-- Wan 1 -->
				<div id="network_wan2">
					<h3>WAN 2</h3>
					<div>			
						<div id="config" class="ui-widget-content">
							<form:label for="" path="" cssErrorClass="error">
								AS NUM
							</form:label>
							<form:input path="" size="8"/><br>
							<form:label for="" path="" cssErrorClass="error">
								IP Address Range
							</form:label>
							<form:input path="" size="8"/>
						</div>		
					</div>
				</div>

				<!-- WAN Interfaces -->
				<div id="iface_down_netprovider1" class="ui-widget-content">	
					<label>Interface:&nbsp;</label>
					<div class="ui-widget-content config_content">								
						<div>
							<b>Interface:&nbsp;</b>
							<span>ge-2/0/0</span>
						</div>		
					</div>										
				</div>
				<div id="iface_down_netprovider2" class="ui-widget-content">	
					<label>Interface:&nbsp;</label>
					<div class="ui-widget-content config_content">								
						<div>
							<b>Interface:&nbsp;</b>
							<span>ge-2/0/0</span>
						</div>		
					</div>										
				</div>
				
				<!-- Init VCPE -->
				
				<div id="vcpe_mp_logical">	
					<h3>VCPE</h3>
					<div>
					<div id="iface_up_lrprovider1" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>
							</div>		
						</div>										
					</div>
					<div id="iface_up_lrprovider2" class="ui-widget-content">	
						<label>Interface:&nbsp;</label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>
							</div>		
						</div>										
					</div>


					<!--Logical Router Provider1-->
					<div id="lrprovider1">
						<h3>Logical Router Provider 1</h3>
						<div>			
							<div id="iface_lo_lrprovider1" class="ui-widget-content">	
								<label>Interface:&nbsp;</label>
								<div class="ui-widget-content config_content">								
									<div>
										<b>Interface:&nbsp;</b>
										<span>ge-2/0/0</span>
									</div>		
								</div>										
							</div>	
						</div>
					</div>
	
					<!--Logical Router Provider2-->
					<div id="lrprovider2">
						<h3>Logical Router Provider 2</h3>
						<div>			
							<div id="iface_lrprovider2_lo" class="ui-widget-content">	
								<label>Interface:&nbsp;</label>
								<div class="ui-widget-content config_content">								
									<div>
										<b>Interface:&nbsp;</b>
										<span>ge-2/0/0</span>
									</div>		
								</div>										
							</div>	
						</div>
					</div>
					
					<div id="iface_lrprovider1_down" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>	
							</div>		
						</div>										
					</div>
					
					<div id="iface_lrprovider2_down" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>	
							</div>		
						</div>										
					</div>
					
					<div id="iface_client_up1" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>	
							</div>		
						</div>										
					</div>
					
					<div id="iface_client_up2" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>	
							</div>		
						</div>										
					</div>
					
					<!--Logical Router Client-->
					<div id="lrclient">
						<h3>Logical Router Client</h3>
						<div>			
							<div id="iface_lrprovider2_lo" class="ui-widget-content">	
								<label>Interface:&nbsp;</label>
								<div class="ui-widget-content config_content">								
									<div>
										<b>Interface:&nbsp;</b>
										<span>ge-2/0/0</span>
									</div>		
								</div>										
							</div>	
						</div>
					</div>
	
					<div id="iface_client_down" class="ui-widget-content">	
						<label><spring:message code="interface"/></label>
						<div class="ui-widget-content config_content">								
							<div>
								<b>Interface:&nbsp;</b>
								<span>ge-2/0/0</span>	
							</div>		
						</div>										
					</div>
				</div>
			</div>
			<!-- End VCPE -->

			<div id="iface_up_netclient" class="ui-widget-content">	
				<label><spring:message code="interface"/></label>
				<div class="ui-widget-content config_content">								
					<div>
						<b>Interface:&nbsp;</b>
						<span>ge-2/0/0</span>	
					</div>		
				</div>										
			</div>

			<div id="network_client">
				<h3>Wan Client</h3>
				<div>			
					<div id="config" class="ui-widget-content">
						<form:label for="" path="" cssErrorClass="error">
							AS NUM
						</form:label>
						<form:input path="" size="8"/><br>
						<form:label for="" path="" cssErrorClass="error">
							IP Address Range
						</form:label>
						<form:input path="" size="8"/>
					</div>		
				</div>
			</div>
		</div>
	</form:form>
</sec:authorize>