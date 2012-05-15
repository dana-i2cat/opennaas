<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<html>
    <head>
        <title>Error page</title>
    </head>
    <body>
       <h2>Your application has generated an error</h2>
 	   
       <h3>Please check for the error given below</h3>
       <b>Exception message:</b><br> 
		    <s:actionerror />
    </body>
</html>