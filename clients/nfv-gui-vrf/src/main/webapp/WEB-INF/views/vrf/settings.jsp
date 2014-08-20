<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<script>
    document.getElementById("ui-id-4").className += " ui-state-highlight";
</script>
<div id="settings" class="ui-widget-content ui-corner-all routTable padding">
    <h3>Settings</h3>
    <form:form action="" modelAttribute="settings" method="post" enctype="multipart/form-data">
        <div class="row">
            <form:label path="routingType">Select OpenNaaS routing mode: </form:label>
            <form:radiobutton cssStyle="label" path="routingType" value="static"/>Static
            <form:radiobutton cssStyle="label" path="routingType" value="dijkstra"/>Dijkstra
            <br/>
        </div>
        <%--    <c:if test="${!empty settings.addShellMode}">
                ${settings.addShellMode}
            </c:if>--%>
        <div class="row">
            <form:label path="addShellMode">Open hosts shell in a: </form:label>
            <form:radiobutton cssStyle="label" path="addShellMode" value="tab"/>Tab
            <form:radiobutton cssStyle="label" path="addShellMode" value="window"/>Window
        </div>
        <div class="row">
            <form:label path="colorDynamicRoutes">Select dynamic route color: </form:label>
            <%--                <form:radiobutton cssStyle="label" path="colorDynamicRoutes" value="#81DAF5"/>Blue
                        <form:radiobutton cssStyle="label" path="colorDynamicRoutes" value="green"/>Green
            --%>            
            <form:input id="color1" class="color" name="color1" type="text" path="colorDynamicRoutes" value="${settings.colorDynamicRoutes}"/>
        </div>
        <style type="text/css">
            #color1{
                float: left; display: inline; 
            }
            .colorPicker-picker{
                float: left; position: absolute;
                display: inline; margin-left: 3px;
            }
        </style>
        <div class="row">
            <form:label path="genNetResName">Generic Network Resource Name: </form:label>
            <form:input path="genNetResName" />
        </div>
        <div class="row">
            <span class="formw">
                <input id="button2" type="submit" value="Save" style="float: right;"/>
            </span>
        </div>
    </form:form>
</div>
<hr/>
<iframe style="border: 2px red solid;" src="//84.88.40.153:4200/opennaas" id="OpenNaaS" name="OpenNaaS" frameBorder="0" width="100%" height="500"></iframe>

<script language="JavaScript" type="text/JavaScript">
    /*
    if(getONRouteMode() == "static"){
        document.getElementById("static").checked = true;
        document.getElementById("dynamic").checked = false;
    }else{
        document.getElementById("dynamic").checked = true;
        document.getElementById("static").checked = false;
    }
    $( "input:disabled" ).val( "this is it" );
     */
    var type = getURLParameter("type");
    
    function getONRouteMode(){
        var result;
        $.ajax({
            type: "GET",
            url: "getONRouteMode",
            success: function (data) {
                $('#ajaxUpdate').html(data);
                result = data;
                document.getElementById("routingType").value = data;
            }
        });
        return result;
    }
    function setONRouteMode(mode){
        if( mode == "Static")
            mode = "static";
        else
            mode = "dijkstra";
        $.ajax({
            type: "GET",
            url: "setONRouteMode/" + mode,
            success: function (data) {
                $('#ajaxUpdate').html(data);
            }
        });
        document.getElementById("routingType").value = mode;
        //        getONRouteMode();
    }
</script>

<div class="modal"></div>
