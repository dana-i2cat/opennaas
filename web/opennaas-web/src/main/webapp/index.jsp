<%@ page language="java"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>OpenNaaS TNC Demo</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link type="text/css" rel="stylesheet" href="css/login.css" />
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
<s:actionerror />
<table align="center" style="margin-top: 50px;">
	<tr align="center">
		<td valign="middle" align="center" width="100%">

		<table cellspacing="0" cellpadding="0" border="0">
			<tr>
				<td width="7"><img src='images/login/white_01.gif' width="7" height="7" alt=''></td>
				<td background='images/login/white_02.gif'><img src='images/login/white_02.gif' width="7" height="7" alt=''></td>
				<td width="7"><img src='images/login/white_03.gif' width="7" height="7""alt=''></td>
			</tr>
			<tr>
				<td background='images/login/white_04.gif'><img src='images/login/white_04.gif' width="7" height="7" alt=''></td>
				<td bgcolor='white'>
					<table border="0" cellspacing="4" cellpadding="2">
						<tr>
							<td colspan="2" align="center"></td>
						</tr>
						<tr>							
							<td colspan="2" align="center"><span class='mainTitle'>OpenNaas Login</span></td>
						</tr>					
						<tr>
							<td colspan="2" ></td>
						</tr>
						<s:form action="login">
							<tr >
								<td align="center"> 
									<s:textfield name="userName" label="User" labelposition="left" theme="xhtml"/>
								</td>
							</tr>
							<tr>
								<td align="center"> 
									<s:password name="password" label="Password" labelposition="left" theme="xhtml"/>
								</td>
							</tr>
							<tr>
								<td colspan="2" align="center"> 
									<br><br><br>
									<s:submit id="submitLogin" name="loginForm:submit" type="submit" value="Enter" ></s:submit>
								</td>
							</tr>
						</s:form>
						<tr>
							<td colspan="2"></td>
						</tr>
					</table>
				</td>
				<td background='images/login/white_06.gif'><img src='images/login/white_06.gif' width="7" height="7" alt=''>
				</td>
			</tr>
			<tr>
				<td width="7"><img src='images/login/white_07.gif' width="7" height="7" alt=''></td>
				<td background='images/login/white_08.gif'><img src='images/login/white_08.gif' width="7" height="7" alt=''></td>
				<td width="7"><img src='images/login/white_09.gif' width="7"height="7" alt=''></td>
			</tr>
		</table>
	</td>
	</tr>
</table>
</body>
</html>

