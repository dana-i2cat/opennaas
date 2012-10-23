<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<div class="span-15 last">

	<form:form modelAttribute="VCPENetwork" action="create" method="post">
		<fieldset>
			<legend>
				<spring:message code="vcpenetwork.create.legend" />
			</legend>
			
			<div class="demo">
				<div id="tabs">
					<ul>
						<li><a href="#tabs-1"><spring:message code="vcpenetwork.basicfields" /></a></li>
						<li><a href="#tabs-2"><spring:message code="vcpenetwork.logicalrouter1" /></a></li>
						<li><a href="#tabs-3"><spring:message code="vcpenetwork.logicalrouter2" /></a></li>
					</ul>
					<div id="tabs-1">
						<div style="margin-bottom: 50px">
							<div style="float: left;">
								<form:label for="name" path="name" cssErrorClass="error">
									<spring:message code="vcpenetwork.name" />
								</form:label>
								<br />
								<form:input path="name" />
								<form:errors path="name" />
							</div>
							
							<div style="float: left; margin-left: 10px;">
								<form:label for="clientIpRange" path="clientIpRange" cssErrorClass="error">
									<spring:message code="vcpenetwork.clientIpRange" />
								</form:label>
								<br />
								<form:input path="clientIpRange" size="12" />
								<form:errors path="clientIpRange" />
							</div>

							<div style="float: left; margin-left: 10px;" >
								<form:label for="template" path="template" cssErrorClass="error">
									<spring:message code="vcpenetwork.template" />
								</form:label>
								<br />
								<form:select path="template">
										<form:option value="basic.template">Basic Template </form:option>
								</form:select>
								<form:errors path="template" />
							</div>
						</div>
					</div>
					<div id="tabs-2">
						<div>
							<c:forEach items="${VCPENetwork.logicalRouter1.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName}</label>
									<div class="input">
										<spring:message code="logicalrouter.physicalinterface" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].name" size="8" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].name" size="8"/>.
										<form:input path="logicalRouter1.interfaces[${vs.index}].port" size="2" readonly="true"/>
										<form:errors path="logicalRouter1.interfaces[${vs.index}].port" size="2" />
										<spring:message code="logicalrouter.ipAddress" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="10" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].ipAddress" size="10"/>
										<spring:message code="logicalrouter.vlan" />
										<form:input path="logicalRouter1.interfaces[${vs.index}].vlan" size="2" />
										<form:errors path="logicalRouter1.interfaces[${vs.index}].vlan" size="2" />
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
					<div id="tabs-3">
						<div>
							<c:forEach items="${VCPENetwork.logicalRouter2.interfaces}" varStatus="vs" var="item">
								<div class="field">
									<label>${item.labelName}</label>
									<div class="input">
										<spring:message code="logicalrouter.physicalinterface" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].name" size="8" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].name" size="8"/>.
										<form:input path="logicalRouter2.interfaces[${vs.index}].port" size="2" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].port" size="2" />
										<spring:message code="logicalrouter.ipAddress" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="10" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].ipAddress" size="10"/>
										<spring:message code="logicalrouter.vlan" />
										<form:input path="logicalRouter2.interfaces[${vs.index}].vlan" size="2" />
										<form:errors path="logicalRouter2.interfaces[${vs.index}].vlan" size="2" />
									</div>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			
			</div>

			<div style="margin-top: 15px">
				<hr/>
				<input type="submit" value="<spring:message code="buttons.create"/>" />
			</div>
		</fieldset>
	</form:form>
</div>