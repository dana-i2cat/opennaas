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

    String.prototype.format = function(){
    var args = arguments;

    return this.replace(/{(\d+)}/g, function(match, number){
    return typeof args[number] != 'undefined' ? args[number] :
    '{' + number + '}';
    });
    };

    function ConvertJsonToTable(parsedJson, tableId, tableClassName, linkText){
        //Patterns for links and NULL value
        var italic = '<i>{0}</i>';
        var link = linkText ? '<a href="{0}">' + linkText + '</a>' :
        '<a href="{0}">{0}</a>';

        //Pattern for table                          
        var idMarkup = tableId ? ' id="' + tableId + '"' :
        '';

        var classMarkup = tableClassName ? ' class="' + tableClassName + '"' :
        '';

        var tbl = '<table border="1" cellpadding="1" cellspacing="1"' + idMarkup + classMarkup + '>{0}{1}</table>';

        //Patterns for table content
        var th = '<thead>{0}</thead>';
        var tb = '<tbody>{0}</tbody>';
        var tr = '<tr>{0}</tr>';
        var thRow = '<th>{0}</th>';
        var tdRow = '<td>{0}</td>';
        var thCon = '';
        var tbCon = '';
        var trCon = '';

        if (parsedJson){
            var isStringArray = typeof(parsedJson[0]) == 'string';
            var headers;

            // Create table headers from JSON data
            // If JSON data is a simple string array we create a single table header
/*                if(isStringArray)
                    thCon += thRow.format('value');
                else{
*/                    // If JSON data is an object array, headers are automatically computed
//                    if(typeof(parsedJson[0]) == 'object'){
                        headers = array_keys(parsedJson[0]);
                        headers[0] = "Id";
                        headers[1] = "Source IP";
                        headers[2] = "Destination IP";
                        headers[3] = "Switch";
                        headers[4] = "Input Port";
                        headers[5] = "Output Port";
                        headers[6] = "Live time";
                        headers[7] = "Action";

                        for (i = 0; i < headers.length; i++)
                            thCon += thRow.format(headers[i]);
//                }
//            }
            headers = array_keys(parsedJson.routeTable[0]);
            th = th.format(tr.format(thCon));

            // Create table rows from Json data
            if(isStringArray){
                for (i = 0; i < parsedJson.length; i++) {
                    tbCon += tdRow.format(parsedJson[i]);
                    trCon += tr.format(tbCon);
                    tbCon = '';
                }
            }else{
                if(headers){
                    var urlRegExp = new RegExp(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig);
                    var javascriptRegExp = new RegExp(/(^javascript:[\s\S]*;$)/ig);

                    for (i = 0; i < parsedJson.routeTable.length; i++){
                        console.log(parsedJson.routeTable[i]);
                        for (j = 0; j < headers.length; j++){
                            var value = parsedJson.routeTable[i][headers[j]];
                            var isUrl = urlRegExp.test(value) || javascriptRegExp.test(value);

                            if(isUrl)   // If value is URL we auto-create a link
                                tbCon += tdRow.format(link.format(value));
                            else{
                                if(value){
                                    if(typeof(value) == 'object'){
                                        //for supporting nested tables
                                        tbCon += tdRow.format(value.macAddress);
                                        tbCon += tdRow.format(value.inputPort);
                                        tbCon += tdRow.format(value.outputPort);
        //                            	tbCon += tdRow.format(ConvertJsonToTable(eval(value.data), value.tableId, value.tableClassName, value.linkText));
                                    } else {
                                        if(j==0)
                                            var rowId = value;
                                        tbCon += tdRow.format(value);
                                    }
                                } else {    // If value == null we format it like PhpMyAdmin NULL values
                                    tbCon += tdRow.format(value);
                                }
                            }
                        }
                        tbCon += tdRow.format("<input onclick='del("+rowId+"); return false;' class='deleteButton' type='button' value='Delete'/>");
                        trCon += tr.format(tbCon);
                        tbCon = '';
                    }
                }
            }
            tb = tb.format(trCon);
            tbl = tbl.format(th, tb);

            return tbl;
        }
        return null;
    }

    function array_keys(input, search_value, argStrict){
        var search = typeof search_value !== 'undefined', tmp_arr = [], strict = !!argStrict, include = true, key = '';

        if (input && typeof input === 'object' && input.change_key_case) { // Duck-type check for our own array()-created PHPJS_Array
            return input.keys(search_value, argStrict);
        }

    for (key in input){
        if (input.hasOwnProperty(key)){
            include = true;
        if (search){
            if (strict && input[key] !== search_value)
                include = false;
            else if (input[key] != search_value)
                include = false;
        } 
        if (include)
            tmp_arr[tmp_arr.length] = key;
        }
    }
    return tmp_arr;
    }
    var test = ${json};
//    var version = ${request.getParameter("type")};
    var type = getURLParameter("type");
    var objectArray = test;
    var jsonHtmlTable = ConvertJsonToTable(objectArray, 'jsonTable', null, 'Download');
    document.write(jsonHtmlTable);
    
    function getURLParameter(name) {
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
    }
    function removeAll(){
        var table = document.getElementById("jsonTable");
        for ( var i = 1; row = table.rows[i]; i++ ) {
            row = table.rows[i];       
            col = row.cells[0];
            del(col.firstChild.nodeValue);
            //alert(col.firstChild.nodeValue);
        }
        for(var i = table.rows.length - 1; i > 0; i--){
            table.deleteRow(i);
        }
    }
    function del(id, version){
        var result = "";
        $.ajax({
            type: 'POST',
            url : "deleteRoute/"+id+"?type="+getURLParameter("type"),
            async: false,
            success : function (data) {
                $("#dynamicContent").html(data);
                result = data;                 
            }
        });
        if(document.getElementById("removedOk") == null){
            $("<div id='removedOk' class='success'>Removed correctly.</div>" ).insertAfter( "#header_menu").before("<br>");
            $('.success').next('br').remove();
            setTimeout(function() {
                //$('.success').remove();
                $('.success').slideUp("slow", function() { $('.success').remove();});
                //$('.success').fadeOut(300, function(){ $(this).remove();});
            }, 3000);
        }
        
        return result;
    }
</script>

<table id="jsonTable" class="tablesorter">

</table>
    <c:if test="${!empty json}">
        <input style="margin-right: 11.5px" class="addRouteButton" onClick="removeAll()" type="button" value="Remove all table" name="Remove all table"/>
    </c:if>
