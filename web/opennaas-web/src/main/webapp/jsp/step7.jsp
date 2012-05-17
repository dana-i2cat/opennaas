<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
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
	<h2>Set static route</h2>
	<s:form action="step7" >	
		<s:submit type="button" value="Next" theme="simple" />
	</s:form>
</body>
</html>
