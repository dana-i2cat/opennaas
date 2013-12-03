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
    
    function Validate() {
        var x=document.forms["frm"]["listRoutes[0].sourceAddress"].value;
        if (x==null || x==""){
            alert("First name must be filled out");
            return true;
        }
    }

    count = 1;
    function addCtrl(){
        var appendTxt = "<tr>";
                
        appendTxt = appendTxt + "<td><input class=\"ipInput\" id=\"listCtrl" + count + ".controllerIp\" name=\"listRoutes[" + count + "].controllerIp\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"controllerPort\" id=\"listCtrl" + count + ".controllerPort\" name=\"listRoutes[" + count + "].controllerPort\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"macInput\" id=\"listCtrl" + count + ".macAddress\" name=\"listRoutes[" + count + "].macAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td></td>";
        appendTxt = appendTxt + "<td></td>";
        appendTxt = appendTxt + "<td class=\"td-last\"><input style=\"float:right\" class=\"deleteButton\" type=\"button\" value=\"Delete\"/></td>";
        $("#Routes tr:last").after(appendTxt);
        count = count + 1;
        $(".deleteButton").button();
    }
    function fill(){
        if(count==3)
            return;
        $("#listCtrl0\\.controllerIp").val("192.168.0.6");
        $("#listCtrl0\\.controllerPort").val("8080");
        $("#listCtrl0\\.macAddress").val("00:00:00:00:00:00:00:01");
        addCtrl();
        $("#listCtrl1\\.controllerIp").val("192.168.0.6");
        $("#listCtrl1\\.controllerPort").val("8081");
        $("#listCtrl1\\.macAddress").val("00:00:00:00:00:00:00:02");
        addCtrl();
        $("#listCtrl2\\.controllerIp").val("192.168.0.10");
        $("#listCtrl2\\.controllerPort").val("8083");
        $("#listCtrl2\\.macAddress").val("00:00:00:00:00:00:00:03");
        addCtrl();
        $("#listCtrl3\\.controllerIp").val("192.168.0.10");
        $("#listCtrl3\\.controllerPort").val("8084");
        $("#listCtrl3\\.macAddress").val("00:00:00:00:00:00:00:04");
        count = 3;
    }
</script>

<form:form modelAttribute="insertCtrlInfo" name="frm" method="post"> 
    <div class="config4">
        <table id="Routes"><thead><tr>
                <th><form:label path="">Controller IP</form:label></th>
                <th><form:label path="">Controller Port</form:label></th>
                <th><form:label path="">Switch Mac</form:label></th>
                <th></th>
                <th></th>
                <th></th>
            </tr></thead>
            <tr>
                <td><form:input class="ipInput" path="listCtrl[0].controllerIp" type="text" value="192.168.1.5"/></td>                    
                <td><form:input class="controllerPort" path="listCtrl[0].controllerPort" type="text" value="8080"/></td>
                <td><form:input class="macInput" path="listCtrl[0].macAddress" type="text" value="00:00:00:00:00:00:00:10"/></td>
                <th></th>
                <th></th>
                <td class="td-last"><input style="float:right" class="deleteButton" type="button" value="Delete"/></td>
            </tr>

        </table>
        <input style="margin-right: 11.5px" class="addCtrlInfoButton" onClick="addCtrl()" type="button" value="Add" name="addRoute"/>
         <input id="submitUpdateConfig" class="button" type="submit" value="Update" />
    </div>
</form:form>
<br/><br/>
<input style="margin-right: 11.5px" class="addCtrlInfoButton" onClick="fill()" type="button" value="Default Values" name="addDefaultValues"/>