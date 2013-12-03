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
    
    function contentDisp(url){
        var result = "";
        $.ajax({
            url : "controllerStatus/"+url,
            async: false,
            success : function (data) {
                $("#dynamicContent").html(data);
                result = data;                 
            }
        });
        return result;
    }
    function ConvertJsonToTable(parsedJson, tableId, tableClassName, linkText){
        //Patterns for links and NULL valu
        var italic = '<i>{0}</i>';
        var link = linkText ? '<a href="{0}" target="_blank">' + linkText + '</a>' :
            '<a href="{0}" >{0}</a>';

        //Pattern for table                          
        var idMarkup = tableId ? ' id="' + tableId + '"' :
            '';

        var classMarkup = tableClassName ? ' class="' + tableClassName + '"' : '';

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
                //for (var key in parsedJson)
                //alert(key);
                
            
                headers = array_keys(parsedJson[0]);
                headers[0] = "Switch DPIP";
                headers[1] = "Controller";
                headers[2] = "Status";
                headers[3] = "Url";
                for (i = 0 ; i < headers.length; i++){
                    thCon += thRow.format(headers[i]);
                }
        
            }
            //    headers = array_keys(parsedJson[0]);
            th = th.format(tr.format(thCon));

            // Create table rows from Json data
            if(isStringArray){
                for (var key in parsedJson){
                    tbCon += tdRow.format(parsedJson[key]);
                    trCon += tr.format(tbCon);
                    tbCon = '';
                }
            }else{
                if(headers){
                    for (var key in parsedJson){
                        for (j = 0; j < headers.length; j++){
                            if (j==0){
                                tbCon += tdRow.format(key);
                            }else if(j==1){
                                tbCon += tdRow.format(parsedJson[key]);
                            }else if(j==2){
                                response = contentDisp(parsedJson[key]);
                                if (response == "Online"){
                                    tbCon += tdRow.format("<img alt='online' src='/opennaas-routing-nfv/resources/images/online.png'>");
                                }else{
                                    tbCon += tdRow.format("<img alt='offline' src='/opennaas-routing-nfv/resources/images/offline.png'>");
                                }
                            }else{
                                tbCon += tdRow.format(link.format("http://"+parsedJson[key]+"/ui/index.html"));
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
    var test = ${json};
    objectArray = test;
    var jsonHtmlTable = ConvertJsonToTable(objectArray, 'jsonTable', null, 'Go to');
    document.write(jsonHtmlTable);
   
</script>

<table id="jsonTable" class="tablesorter">
</table> 