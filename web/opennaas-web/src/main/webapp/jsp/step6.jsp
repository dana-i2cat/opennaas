<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
	<link type="text/css" rel="stylesheet" href="css/styles.css" />
</head>
<body>
	<div id="logout">
		<s:if test="%{#session.OPENNAAS_USER != null}">
			<s:label value="%{#session.OPENNAAS_USER.userName}" theme="simple" />&nbsp;|
			<s:url action="logout" id="logout"/>
			<s:a href="%{logout}">
				<s:text name="Logout"/>
			</s:a>
			<img title="Logout" alt="Logout" src="images/login/logout.gif"/>
		</s:if>
		<s:else>
			<s:text name="Disconnected"/>
		</s:else>
	</div>
	<div id="image">
		<img src='images/img6.png'>
	</div>
	<div id="buttons">
		<s:form action="step6" >	
			<s:submit type="button" value="Do It >>" theme="simple" />
		</s:form>
	</div>
</body>
</html>
