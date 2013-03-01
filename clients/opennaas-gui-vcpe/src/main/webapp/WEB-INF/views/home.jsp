<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ol id="selectable">
	<li id="sp_vcpe" class="ui-widget-content template">
			<div class="title"><spring:message code="home.template.sp_vcpe.title"/></div>
			<div class="description"><spring:message code="home.template.sp_vcpe.description"/></div>
			<div class="template_image">
				<img src="<c:url value="/resources/images/templates/sp_vcpe.png" />"  title="VCPE" width="300" alt="Single Provider">
			</div>
	</li>
	<li id="mp_vcpe" class="ui-widget-content template">
			<div class="title"><spring:message code="home.template.mp_vcpe.title"/></div>
			<div class="description"><spring:message code="home.template.mp_vcpe.description"/></div>
	 		<div class="template_image">
	 			<img src="<c:url value="/resources/images/templates/mp_vcpe.png" />"  title="UNIC" width="300" alt="Multiple Networks">
	 		</div>
	</li>
</ol>
<br>
<div id="buttonLine">
	<input id="selectTemplateButton" class="button" type="submit" value="<spring:message code="buttons.next"/>" />
</div>

<%@include file="noTemplate.jsp"%>

