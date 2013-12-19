function ConvertJsonToRouteTable(parsedJson, tableId, tableClassName, linkText) {
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

    if (parsedJson) {
        var isStringArray = typeof (parsedJson[0]) == 'string';
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
        headers[4] = "Action";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        headers = array_keys(parsedJson.routeTable[0]);
        th = th.format(tr.format(thCon));

        // Create table rows from Json data
        if (isStringArray) {
            for (i = 0; i < parsedJson.length; i++) {
                tbCon += tdRow.format(parsedJson[i]);
                trCon += tr.format(tbCon);
                tbCon = '';
            }
        } else {
            if (headers) {
                var urlRegExp = new RegExp(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig);
                var javascriptRegExp = new RegExp(/(^javascript:[\s\S]*;$)/ig);

                for (i = 0; i < parsedJson.routeTable.length; i++) {
                    var rowId = parsedJson.routeTable[i]['id'];
                    tbCon += tdRow.format(parsedJson.routeTable[i]['id']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['sourceAddress']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['destinationAddress']);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].inputPort);
                    tbCon += tdRow.format(parsedJson.routeTable[i]['switchInfo'].outputPort);
                    console.log(parsedJson.routeTable[i]);
                    /*for (j = 0; j < headers.length; j++){
                     var value = parsedJson.routeTable[i][headers[j]];
                     var isUrl = urlRegExp.test(value) || javascriptRegExp.test(value);
                     
                     
                     if(isUrl)   // If value is URL we auto-create a link
                     tbCon += tdRow.format(link.format(value));
                     else{
                     if(value){
                     if(typeof(value) == 'object'){
                     //for supporting nested tables
                     //tbCon += tdRow.format(value.macAddress);
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
                     }*/
                    tbCon += tdRow.format("<input onclick='del(" + rowId + "); return false;' class='deleteButton' type='button' value='Delete'/>");
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