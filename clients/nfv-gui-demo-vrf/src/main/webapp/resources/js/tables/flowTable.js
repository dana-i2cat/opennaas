function ConvertJsonToFlowTable(parsedJson, tableId, tableClassName, linkText) {
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

        headers = array_keys(parsedJson[0]);
//        headers[0] = "Name";
        headers[0] = "srcIp";
        headers[1] = "dstIp";
        headers[2] = "Ether-type";
        headers[3] = "InPort";
        headers[4] = "OutPort";

        for (i = 0; i < headers.length; i++)
            thCon += thRow.format(headers[i]);

        headers = array_keys(parsedJson.floodlightOFFlows.floodlightOFFlow[0]);
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
                for (i = 0; i < parsedJson.floodlightOFFlows.floodlightOFFlow.length; i++) {
//                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].name);
                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].match.srcIp);
                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].match.dstIp);
                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].match.etherType);
                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].match.ingressPort);
                    tbCon += tdRow.format(parsedJson.floodlightOFFlows.floodlightOFFlow[i].actions.value);
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
