<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- Please Wait dialog -->
<div id="pleaseWait" title="Please Wait...">
	<p>This window will close automatically when the process finishes.</p>
	<p align="center">
		<img src='<c:url value="/resources/images/ajax-loader.gif"/>'>
	</p>
</div>