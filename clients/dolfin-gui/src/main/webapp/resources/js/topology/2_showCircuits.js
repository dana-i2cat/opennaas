/*
 * Config Route page. Show the topology and the Routing Table.
 *
 */
var file = "config";
document.getElementById("ui-id-1").className += " ui-state-highlight";
var selectedCircuit = null;
var portsIdMap = portIdsMap();


function runtime(node, links) {
    var test = d3.selectAll('.link2').data(links.filter(function(l) {
        return l.type === "new_link";
    }));

    test.on('mouseover', mouseOverLink);

    node
        .on('mouseover', mouseOverImage)
        .on('dblclick', function (d) {
            shellMode = "window";
            console.log(shellMode);
            if(d.type == "host"){
                if(shellMode == "tab"){
                    addTab(d.name);
                }else if(shellMode == "window"){
                    var myWindow = window.open(openHostShell(d.name));
                    myWindow.document.write('<html><head><title>Host '+d.name+'</title></head><body height="100%" width="100%"><iframe src="' + openHostShell(d.name) + '" height="95%" width="100%" style="border: 0;"></iframe></body></html>');
                }
            }
    });
}

/**
 * Show the black line
 * @returns {undefined}
 */
function mousemove() {
    if (!mousedown_node)
        return;
    // update drag line
    drag_line.attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);

}
// app starts here
svg.on('mousemove', mousemove);

/**
 * Get Circuit to OpenNaaS
 * @param {type} circuitId
 * @returns {data}
 */
function getAllocatedCircuit(circuitId) {
    console.log("ajax/getCircuitSwitches/" + circuitId);
    var response;
    $.ajax({
        type: "GET",
        async: false,
        url: "ajax/getCircuitSwitches/" + circuitId,
        success: function(data) {
            response = data;
        }
    });
/*    if (circuitId == 3) {
        response = '{"guiSwitches":[{"name":"s1"},{"name":"s2"},{"name":"s3"}]}';
    } else if (circuitId == 2) {
        response = '{"guiSwitches":[{"name":"s1"},{"name":"s2"},{"name":"s3"},{"name":"s1"}]}';
    }
*/
//    response = eval('(' + response + ')');

    if (response == null) {
        cleanDisplayedLink();
    }
    return response;
}

/**
 * Remove the displayed links of other obtained routes.
 * @returns {undefined}
 */
function cleanDisplayedLink() {
    console.log("Clean displayed route links. Using link2 class.");
    var list = links.filter(function(l) {
        return l.type !== "static";
    });
    for (var i = 0; i < list.length; i++) {
        for (var j = 0; j < links.length; j++) {
            if (links[j] === list[i])
                links.splice(j, 1);
        }
    }
    updateLinks();
    //$(".link2").remove();
}
/**
 * Show the tipsy dialog
 * @returns {undefined}
 */
function mouseOverImage() {
    $('.node').tipsy({
        fade: false,
        html: true,
        gravity: $.fn.tipsy.autoNS, //north(n)/west(w)/dynamic
        title: function() {
            var d = this.__data__;
            var info = "<b>Id:</b> " + d.name + "<br>";
            if (d.type === "switch") {
                info += "<b>DPID:</b> " + d.dpid + "<br>";
                info += "<b>Controller:</b> " + d.controller + "<br>";
            } else if (d.type === "controller") {
                info += "<b>IP:</b> " + d.controller + "<br>";
            } else if (d.type === "host") {
                info += "<b>IP:</b> " + d.ip + "<br>";
                info += "<b>Connected with:</b> " + d.SW + "<br>";
            }
            return info;
        }
    });
}

function showGraphicRoute(circuitId, json, type) {
    if (type === 0)
        cleanDisplayedLink();
    console.log("Drawing circuit: " + circuitId);
    var link;
    //request to OpenNaaS
    var returnedRoutes = eval('(' + getAllocatedCircuit(circuitId) + ')');
    returnedRoutes = returnedRoutes.guiSwitches;
    console.log("Before: " + JSON.stringify(returnedRoutes));
//    returnedRoutes = [{name: 's1'}, {name: 's2'}, {name: 's3'},{name: 's1'}, {ip: '192.168.3.3'}];
/*    if (circuitId == 3) {
        returnedRoutes = [{"name": "s1"}, {"name": "s2"}, {"name": "s3"}, {"name": "s4"}];
    } else if (circuitId == 2) {
        returnedRoutes = [{"name": "s1"}, {"name": "s4"}];
    }
    */
   returnedRoutes = convertNameOfSwitch(returnedRoutes);
   console.log(returnedRoutes);
   
    try {
//        returnedRoutes = sortReturnedRoutes(returnedRoutes);
    } catch (e) {
        if (returnedRoutes == null || returnedRoutes != 'undefined') {
            d3.selectAll('.dragline').attr('d', 'M0,0L0,0');
            return;
        }
    }
    
    var newSource = nodes.filter(function(n) {
        return n.name === returnedRoutes[0].name;
    })[0];//first switch
    var newTarget = nodes.filter(function(n) {
        return n.name === returnedRoutes[returnedRoutes.length - 1].name;
    })[0];//last switch

//obtain first link (host to switch) using the ingressPort of the Circuit
//    var ingressPort = json.trafficFilter.ingressPort;//is a string, l.srcPort is a Number
    var ingressPort = json.route.networkConnections[1].source._id;
    var re = new RegExp(/h(\w+)[-.](\w+)/);
//    if(returnedRoutes[0].name.match(re)){
    if(json.route.networkConnections[0].source._id.match(re)){
        console.log(re.exec(json.route.networkConnections[0].source._id)[1]);
        var host = "h"+re.exec(json.route.networkConnections[0].source._id)[1];
        var newSource = nodes.filter(function(n) { return n.name === host;})[0];//first switch
    }
    var firstLink = linkGivenSwitchAndPort(findPort(ingressPort), newSource);
    console.log(firstLink);

//obain last Link (last switch to last node (host)) using the lastPort of the last NetworkConnection
    var finalOutPort = json.route.networkConnections[json.route.networkConnections.length - 2].destination._id;//is a string, l.srcPort is a Number
    var lastLink = linkGivenSwitchAndPort(findPort(finalOutPort), newTarget);
    console.log(lastLink);

    console.log("After sort: " + JSON.stringify(returnedRoutes));
/*    if (firstLink.target === newSource) {
        newSource = firstLink.source;
    } else {
        newSource = firstLink.target;
    }*/
console.log(json.route.networkConnections[json.route.networkConnections.length -1].destination._id);
console.log(re.exec(json.route.networkConnections[json.route.networkConnections.length -1].destination._id)[1]);
lastNode = nodes.filter(function(n){ return n.name == "h"+re.exec(json.route.networkConnections[json.route.networkConnections.length -1].destination._id)[1]})[0];
console.log(lastNode);
    if (lastLink.target.type === "host") {
        lastNode = lastLink.target;
    } else if (lastLink.source.type === "host") {
        lastNode = lastLink.source;
    }

    if (typeof returnedRoutes !== 'undefined') {
        for (var i = 0; i < returnedRoutes.length; i++) {//i=1 because the first position is the source
            var obj = returnedRoutes[i];
            for (var key in obj) {
//                console.log("Json key: " + key + " Json value: " + obj[key]);
                dest1 = nodes.filter(function(n) {
                    return n.name === obj[key];
                })[0];
                if (key === "name") {
                    dest1 = nodes.filter(function(n) {
                        return n.name === obj[key];
                    })[0];
                } else if (key === "ip") {
                    dest1 = nodes.filter(function(n) {
                        return n.ip === obj[key];
                    })[0];
                    //highlight( source.ip, target.ip );
                }
                link = {};
                link = {source: newSource, target: dest1, type: "new_path" + newSource.id + "" + dest1.id, circuitId: circuitId};
                console.log(link);
                var contained = links.filter(function(l) { return l.type == link.type;});
                console.log(contained);
                if (contained.length === 0) {
                    console.log("PUSHING LINK");
                    console.log(link);
                    links.push(link);
                } else {
                    console.log("CONTAINED");
                    console.log(contained[0].circuitId.length);
                    if (contained[0].circuitId != circuitId) {
                        
                        var index = links.indexOf(links.filter(function(l){return l.type == link.type;})[0]);
                        console.log("INDEX: "+index);
                        console.log(links[index]);
                        links.splice(index,1);
                        
                        link = {};
                        var newCircuitId = circuitId + "" + contained[0].circuitId;
                        link = {source: newSource, target: dest1, type: "new_path" + newSource.id + "" + dest1.id, circuitId: newCircuitId};
                        console.log(link);
                        links.push(link);
                        console.log(d3.select("#"+link.type));
                        d3.select("#"+link.type).attr("class", 'new_link'+newCircuitId+' link2');
                    }
                    /*if(contained[0].circuitId != circuitId && contained[0].circuitId.length === 1){
                     console.log("diferent circuit");
                     var index = links.indexOf(links.filter(function(l){return l.type == link.type;})[0]);
                     /*                        console.log(index);
                     console.log(contained[0]);
                     console.log(links[index]);
                     console.log(circuitId+""+contained[0].circuitId);
                     links[index].circuitId = circuitId+""+contained[0].circuitId;
                     console.log(links[index]);
                     */
                    /*links.splice(index,1);
                     link = {};
                     console.log(circuitId);
                     console.log(contained[0].circuitId);
                     var newCircuitId = "";
                     newCircuitId = circuitId+""+contained[0].circuitId;
                     link = {source: newSource, target: dest1, type: "new_path"+newSource.id+""+dest1.id, circuitId: newCircuitId};
                     console.log(link);
                     links.push(link);
                     
                     }*/
                }
                newSource = dest1;
            }
        }
        console.log(circuitId);
        link = {source: newSource, target: lastNode, type: "new_path" + newSource.id + "" + lastNode.id, circuitId: circuitId};
        var contained = links.filter(function(l) {
            return l.type == link.type;
        }).length;
console.log("Contained final: "+contained)        ;
        if (contained === 0) {
            links.push(link);
        }
    }
    console.log(links);
    if (type === 0) {
//        cleanDisplayedLink();
    }

    // select new link
    selected_link = link;
    selected_node = null;
//    updateTopology(storedNodes);
console.log("CALLL UPDATE LINKS FUNCTION -------------------------------");
    updateLinks();
    createCSSStyle(circuitId);
}

function linkGivenSwitchAndPort(port, node) {
    return links.filter(function(l) {
        if (((port == l.srcPort || port == l.dstPort) && node === l.source) || (
                (port == l.srcPort || port == l.dstPort) && node === l.target)) {
            return l;
        }
    })[0];
}

function showAllCircuits(jsonObject) {
    cleanDisplayedLink();
    console.log(jsonObject);
    for (var i = 0; i < jsonObject.circuits.circuit.length; i++) {
        getSpecificCircuit(jsonObject.circuits.circuit[i].circuitId, 1);
    }
//    showGraphicRoute(circuitId, json);
}

function mouseOverLink() {
    console.log("MouseOverLink");
    $('.link2').tipsy({
        fade: true,
        html: true,
        gravity: 'w',
        title: function() {
            var d = this.__data__;
            var info = "<b>CircuitId:</b> " + d.circuitId + "<br>";

            return info;
        }
    });
}

function convertNameOfSwitch(returnedRoutes){
    var newReturned = [];
    console.log(returnedRoutes);
    var re = new RegExp(/:(\w+)/);
    var node = {};
//    returnedRoutes.name = returnedRoutes.sort();
    for(var i=0; i<returnedRoutes.length; i++){
        if(returnedRoutes[i].name != "no exist"){
                var swName = re.exec(returnedRoutes[i].name)[1];
                node = {};
//              node = returnedRoutes[i].name;
                node.name = swName;
                newReturned.push(node);
        }
    }
    return newReturned;
}  

function createCSSStyle(circuitId){
    var rule = '.new_link'+circuitId +'{stroke: #0029FF}';
    var div = $("<div />", {
        html: '&shy;<style>' + rule + '</style>'
      }).appendTo("body");
    var someClass = {"stroke": "#0029FF"};
    $(this).css(someClass);
    listClasses.push = "new_link"+circuitId;
}

