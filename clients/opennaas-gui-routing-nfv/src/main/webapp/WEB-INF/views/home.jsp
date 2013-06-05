<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<ol id="selectable">
	List Routing resources...
</ol>
<br>
<div id="buttonLine">
	<input id="selectTemplateButton" class="button" type="submit" value="<spring:message code="buttons.next"/>" />
</div>

<%@include file="noTemplate.jsp"%>

