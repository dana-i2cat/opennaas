<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<script language="JavaScript" type="text/JavaScript">
    var scnCount = ${fn:length(config.scnInterfaces)};

    function addRoute(){
    var appendTxt = "<tr>";
    appendTxt = appendTxt + "<td><input id=\"scnInterfaces" + scnCount + ".scnInterfacesType\" name=\"scnInterfaces[" + scnCount + "].scnInterfacesType\" type=\"text\" value=\"\"/></td>";
    appendTxt = appendTxt + "<td><input id=\"scnInterfaces" + scnCount + ".scnInterfacesAddress\" name=\"scnInterfaces[" + scnCount + "].scnInterfacesAddress\" type=\"text\" value=\"\"/></td>";
    appendTxt = appendTxt + "<td><select class=\"single-select\" id=\"scnInterfaces" + scnCount + ".scnInterfacesStatus\" name=\"scnInterfaces[" + scnCount + "].scnInterfacesStatus\"><option value=\"Enable\">Enable</option><option value=\"Disable\" selected=\"selected\">Disable</option></select></td>";
    appendTxt = appendTxt + "<td class=\"td-last\"><input class=\"deleteButton\" id=\"deleteButton\" type=\"button\" value=\"Delete\"/></td>";
    $("#Routes tr:last").after(appendTxt);
    scnCount = scnCount + 1;
    $(".deleteButton").button();
    $(".single-select").multiselect({
    multiple: false,
    header: false,
    minWidth: "70",
    height: "60",
    noneSelectedText: "Select",
    selectedList: 1
    });
    }
</script>

<form:form action="/secure/noc/nfvRouting/insertRoute" modelAttribute="route" name="frm" method="post" enctype="multipart/form-data" onSubmit="return Validate();">
    <div class="config4">
        <table id="Routes"><tr>
                <th><form:label path="sourceAddress">Source Address</form:label></th>
                <th><form:label path="destinationAddress">Destination Address</form:label></th>
                <th><form:label path="switchInfo.macAddress">Switch Mac</form:label></th>
                <th><form:label path="switchInfo.inputPort">Input Port</form:label></th>
                <th><form:label path="switchInfo.outputPort">Output Port</form:label></th>
                <th></th>
            </tr>
            <tr>
                <td><form:input class="ipInput" path="sourceAddress" type="text" value=""/></td>                    
                <td><form:input class="ipInput" path="sourceAddress" type="text" value=""/></td>
                <td><form:input class="macInput" path="sourceAddress" type="text" value=""/></td>
                <td><form:input class="portInput" path="sourceAddress" type="text" value=""/></td>
                <td><form:input class="portInput" path="sourceAddress" type="text" value=""/></td>
                <td class="td-last"><input id="deleteButton" type="button" value="Delete"/>
                </td>
            </tr>

        </table>
        <input id="addButton" onClick="addRoute()" type="button" value="Add" name="addRoute"/>
    </div>
</form:form>