<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div id="animaton_topology" class="topology ui-widget-content ui-corner-all">
    <%--<c:if test="${!empty topologyName}">--%>
        <p id="chart" ></p>
    <%--</c:if>--%>
    <c:if test="${empty topologyName}">
        <br/><h3>No topology file loaded</h3><br/>
    </c:if>
</div>
<script src="<c:url value="/resources/js/topology/base.js" />"></script>
<script src="<c:url value="/resources/js/topology/animation.js" />"></script>
<script>
    $.ajax({
        type: 'GET',
        url : "getStreamInfo/",
        async: false,
        success : function (data) {
            if(data !== ""){
                returnedRoutes = data;
                streamPacket(eval('(' + returnedRoutes + ')'));
            }
        }
    });
</script>

<div id="animation_log" class="ui-widget-content ui-corner-all padding">
    <h3>Log OpenNaaS</h3>
    <div id="log" class="ui-corner-all" align="center"></div>

<script>
    setInterval(function(){ 
        $.ajax({
            type: 'GET',
            url : "getLog/",
            async: false,
            success : function (data) {
                if(data !== ""){
                    var image = '<img src="<c:url value="/resources/images/arrow_icon.svg.png" />" width="18px"/>';
                    document.getElementById('log').innerHTML = image+data;
                }else{
                    document.getElementById('log').innerHTML = data;
                }
                result = data;                 
            }
        });
    }, 5000);//5000
</script>
</div>