<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- Please Wait dialog -->
<div id="pleaseWait" title="<spring:message code="progressbar.title" />">
	<p><spring:message code="progressbar.description" /></p>
	<p align="center">
		<img src='<c:url value="/resources/images/ajax-loader.gif"/>'>
	</p>
</div>