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
                if(isStringArray)
                    thCon += thRow.format('value');
                else{
                    // If JSON data is an object array, headers are automatically computed
                    if(typeof(parsedJson[0]) == 'object'){
                        headers = array_keys(parsedJson[0]);
                        headers[0] = "Id";
                        headers[1] = "Source IP";
                        headers[2] = "Destination IP";
                        headers[3] = "Switch";
                        if(type=='subnet'){
                            headers[4] = "Output Port";
                            headers[5] = "Live time";
                        }else{
                            headers[4] = "Input Port";
                            headers[5] = "Output Port";
                            headers[6] = "Live time";
                        }

                        for (i = 0; i < headers.length; i++)
                            thCon += thRow.format(headers[i]);
                }
            }
            headers = array_keys(parsedJson[0]);
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

                    for (i = 0; i < parsedJson.length; i++){
                        for (j = 0; j < headers.length; j++){
                            var value = parsedJson[i][headers[j]];
                            var isUrl = urlRegExp.test(value) || javascriptRegExp.test(value);

                            if(isUrl)   // If value is URL we auto-create a link
                                tbCon += tdRow.format(link.format(value));
                            else{
                                if(value){
                                    if(typeof(value) == 'object'){
                                        //for supporting nested tables
                                        if(type=='subnet'){
                                            if(j == 1 || j == 2) 
                                                tbCon += tdRow.format(value.ipAddress);
                                            if(j == 3){
                                                tbCon += tdRow.format(value.macAddress);
                                                tbCon += tdRow.format(value.outputPort);
                                            } 
                                        }else{
                                            tbCon += tdRow.format(value.macAddress);
                                            tbCon += tdRow.format(value.inputPort);
                                            tbCon += tdRow.format(value.outputPort);
                                        }
        //                            		tbCon += tdRow.format(ConvertJsonToTable(eval(value.data), value.tableId, value.tableClassName, value.linkText));
                                    } else {
                                        tbCon += tdRow.format(value);
                                    }
                                } else {    // If value == null we format it like PhpMyAdmin NULL values
                                    tbCon += tdRow.format(italic.format(value).toUpperCase());
                                }
                            }
                        }
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

    function array_keys(input, search_value, argStrict)
    {
    var search = typeof search_value !== 'undefined', tmp_arr = [], strict = !!argStrict, include = true, key = '';

    if (input && typeof input === 'object' && input.change_key_case) { // Duck-type check for our own array()-created PHPJS_Array
    return input.keys(search_value, argStrict);
    }

    for (key in input)
    {
    if (input.hasOwnProperty(key))
    {
    include = true;
    if (search)
    {
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
//    var objectArray = [{"sourceAddress":"192.168.1.3","destinationAddress":"192.168.1.30","switchInfo":{"numberPorts":0,"listPorts":["1"],"inputPort":"1","outputPort":"2","macAddress":"00:00:00:00:00:00:00:01"},"timeToLive":null},{"sourceAddress":"192.168.1.30","destinationAddress":"192.168.1.3","switchInfo":{"numberPorts":0,"listPorts":["1"],"inputPort":"1","outputPort":"2","macAddress":"00:00:00:00:00:00:00:01"},"timeToLive":null}];
    var test = ${json};
    var type = getURLParameter("type");
    var objectArray = test;
    var jsonHtmlTable = ConvertJsonToTable(objectArray, 'jsonTable', null, 'Download');
    document.write(jsonHtmlTable);
    
    function getURLParameter(name) {
        return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
    }
</script>

Routing table...
${json}

<table id="jsonTable" class="tablesorter">

</table>
