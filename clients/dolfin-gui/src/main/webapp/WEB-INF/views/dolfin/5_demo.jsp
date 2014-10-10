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
    var shellMode = "${settings.addShellMode}";
</script>
<div id="animaton_topology" class="topology ui-widget-content ui-corner-all">
    <c:if test="${!empty topologyName}">
        <p onmousedown="cleanDrag()" id="chart" ></p>
    </c:if>
    <c:if test="${empty topologyName}">
        <h3><spring:message code="topology.notdefined"/></h3><br/>        
    </c:if>
</div>
<script src="<c:url value="/resources/js/topology/0_base.js" />"></script>
<script src="<c:url value="/resources/js/topology/3_animation.js" />"></script>

<c:if test="${settings.addShellMode=='tab'}">
    <div id="tabs">
        <ul>
            <li><a href="#tabs-1">Tab1</a> <span class="ui-icon ui-icon-close" role="presentation">Remove Tab</span></li>
        </ul>
        <div id="tabs-1">
        <!--    <div id="animation_log" class="ui-widget-content ui-corner-all padding">-->
<!--        <h3>Log OpenNaaS</h3>
        <div id="log" class="ui-corner-all" align="center"></div>
-->
        </div>
    </div>
</c:if>

<script>
    //var tabContent = '<iframe src="//84.88.40.153/webconsole/webconsole.php" id="gadget{id}" name="gadget{id}" frameborder="1" width="100%" height="500"></iframe>',
    var tabContent = '<iframe src="//84.88.40.153:4200/{hostName}" id="gadget{id}" name="gadget{id}" frameborder="1" width="100%" height="500"></iframe>',
            tabTemplate = "<li><a href='{href}'>{label}</a> <span class='ui-icon ui-icon-close' role='presentation'>Remove Tab</span></li>",
            tabCounter = 2;

$("#add_tab")
            .button()
            .click(function() {
                addTab();
            });
    var tabs = $("#tabs").tabs();
    function addTab(hostName) {
        var label = "Shell " + tabCounter,
                id = "tabs-" + tabCounter,
                li = $(tabTemplate.replace(/\{href\}/g, "#" + id).replace(/\{label\}/g, label)),
//        tabContent = $( tabContent.replace( /#\{id\}/g, id ) ),
                tabContentId = tabContent.replace(/\{id\}/g, tabCounter);
                tabContentId = tabContent.replace(/\{hostName\}/g, hostName);

        tabs.find(".ui-tabs-nav").append(li);
        tabs.append("<div id='" + id + "'>" + tabContentId + "</div>");
        tabs.tabs("refresh");
        tabCounter++;

    }

    // close icon: removing the tab on click
    tabs.delegate("span.ui-icon-close", "click", function() {
        var panelId = $(this).closest("li").remove().attr("aria-controls");
        $("#" + panelId).remove();
        tabs.tabs("refresh");
    });

    tabs.bind("keyup", function(event) {
        if (event.altKey && event.keyCode === $.ui.keyCode.BACKSPACE) {
            var panelId = tabs.find(".ui-tabs-active").remove().attr("aria-controls");
            $("#" + panelId).remove();
            tabs.tabs("refresh");
        }
    });
    
</script>