<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="span-12 last">

	<form:form modelAttribute="vcpeNetwork" action="vcpeNetwork" method="post">
		<fieldset>
			<legend>
				<spring:message code="vcpenetwork.create.legend" />
			</legend>
			
			<div style="margin-bottom: 50px">
				<div style="float: left;">
					<form:label for="name" path="name" cssErrorClass="error">
						<spring:message code="vcpenetwork.name" />
					</form:label>
					<br />
					<form:input path="name" />
					<form:errors path="name" />
				</div>
				
				<div style="float: left; margin-left: 10px;" >
					<form:label for="template" path="template" cssErrorClass="error">
						<spring:message code="vcpenetwork.template" />
					</form:label>
					<br />
					<form:select path="template">
							<form:option value="template1">Template 1</form:option>
							<form:option value="template2">Template 2</form:option>
							<form:option value="template3">Template 3</form:option>
					</form:select>
					<form:errors path="template" />
				</div>
			</div>
			
			<br/><hr/>
		
			<div style="float: left; margin:0px 0 20px 0px;">
				<h3><spring:message code="vcpenetwork.logicalrouter1" /></h3>
				<c:forEach items="${vcpeNetwork.logicalRouter1.interfaces}" varStatus="vs">
					<div class="field">
						<legend>Interface ${vs.index + 1}</legend>
						<div class="input">
							<spring:message code="logicalrouter.physicalinterface" />
							<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8"/>
							<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8"/>.
							<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="3"/>
							<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="3" />
							<spring:message code="logicalrouter.ipAddress" />
							<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="8"/>
							<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="8"/>
							<spring:message code="logicalrouter.vlan" />
							<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="3"/>
							<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="3" />
						</div>
					</div>
				</c:forEach>
			</div>

			<hr/>

			<div style="float: left; margin:5px 0 15px 0px;">
				<h3><spring:message code="vcpenetwork.logicalrouter2" /></h3>
				<c:forEach items="${vcpeNetwork.logicalRouter2.interfaces}" varStatus="vs">
					<div class="field">
						<legend>Interface ${vs.index + 1}</legend>
						<div class="input">
							<spring:message code="logicalrouter.physicalinterface" />
							<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8"/>
							<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8"/>.
							<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="3"/>
							<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="3" />
							<spring:message code="logicalrouter.ipAddress" />
							<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="8"/>
							<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="8"/>
							<spring:message code="logicalrouter.vlan" />
							<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="3"/>
							<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="3" />
						</div>
					</div>
				</c:forEach>
			</div>

			<hr/>

			<div>
				<input type="submit" value="<spring:message code="buttons.create"/>" />
			</div>
		</fieldset>
	</form:form>
</div>