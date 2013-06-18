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
    function addRout(){
        var appendTxt = "<tr>";

                
        appendTxt = appendTxt + "<td><input class=\"ipInput\" id=\"listRoutes" + count + ".sourceAddress\" name=\"listRoutes[" + count + "].sourceAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"ipInput\" id=\"listRoutes" + count + ".destinationAddress\" name=\"listRoutes[" + count + "].destinationAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"macInput\" id=\"listRoutes" + count + ".switchInfo.macAddress\" name=\"listRoutes[" + count + "].switchInfo.macAddress\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"portInput\" id=\"listRoutes" + count + ".switchInfo.inputPort\" name=\"listRoutes[" + count + "].switchInfo.inputPort\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td><input class=\"portInput\" id=\"listRoutes" + count + ".switchInfo.outputPort\" name=\"listRoutes[" + count + "].switchInfo.outputPort\" type=\"text\" value=\"\"/></td>";
        appendTxt = appendTxt + "<td class=\"td-last\"><input class=\"deleteButton\" type=\"button\" value=\"Delete\"/></td>";
        $("#Routes tr:last").after(appendTxt);
        count = count + 1;
        $(".deleteButton").button();
    }
    
    function fill(){
        if(count==3)
            return;
        $("#listRoutes0\\.sourceAddress").val("192.168.1.2");
        $("#listRoutes0\\.destinationAddress").val("192.168.2.3");
        $("#listRoutes0\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
        $("#listRoutes0\\.switchInfo\\.inputPort").val("1");
        $("#listRoutes0\\.switchInfo\\.outputPort").val("2");
        addRout();
        $("#listRoutes1\\.sourceAddress").val("192.168.2.3");
        $("#listRoutes1\\.destinationAddress").val("192.168.1.2");
        $("#listRoutes1\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:01");
        $("#listRoutes1\\.switchInfo\\.inputPort").val("2");
        $("#listRoutes1\\.switchInfo\\.outputPort").val("1");
        addRout();
        $("#listRoutes2\\.sourceAddress").val("192.168.1.2");
        $("#listRoutes2\\.destinationAddress").val("192.168.2.3");
        $("#listRoutes2\\.switchInfo\\.macAddress").val("00:00:00:00:00:00:00:02");
        $("#listRoutes2\\.switchInfo\\.inputPort").val("2");
        $("#listRoutes2\\.switchInfo\\.outputPort").val("1");
     }
        
</script>

<form:form modelAttribute="insertRoutes" name="frm" method="post" onSubmit="return Validate();"> 
    <div class="config4">
        <table id="Routes"><tr>
                <th><form:label path="">Source Address</form:label></th>
                <th><form:label path="">Destination Address</form:label></th>
                <th><form:label path="">Switch Mac</form:label></th>
                <th><form:label path="">Input Port</form:label></th>
                <th><form:label path="">Output Port</form:label></th>
                <th></th>
            </tr>
            <tr>
                <td><form:input class="ipInput" path="listRoutes[0].sourceAddress" type="text" value="1"/></td>                    
                <td><form:input class="ipInput" path="listRoutes[0].destinationAddress" type="text" value="2"/></td>
                <td><form:input class="macInput" path="listRoutes[0].switchInfo.macAddress" type="text" value="3"/></td>
                <td><form:input class="portInput" path="listRoutes[0].switchInfo.inputPort" type="text" value="4"/></td>
                <td><form:input class="portInput" path="listRoutes[0].switchInfo.outputPort" type="text" value="5"/></td>
                <td class="td-last"><input class="deleteButton" type="button" value="Delete"/>
                </td>
            </tr>

        </table>
        <input class="addRouteButton" onClick="addRout()" type="button" value="Add" name="addRoute"/>
         <input id="submitUpdateConfig" class="button" type="submit" value="Update" />
    </div>
</form:form>
<br/><br/>
 <input class="addRouteButton" onClick="fill()" type="button" value="Default Values" name="addDefaultValues"/>