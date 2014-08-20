/*
 * Animation page. Show the topology and the Routing Table.
 *
 */
var file = 'animation';//this javascript file corresponds to a animation page
document.getElementById("ui-id-3").className += " ui-state-highlight";
var counterStream = 0;//counter used in the animation of video streaming
// line displayed when dragging new nodes
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

// handles to link and node element groups
var packet = svg.selectAll('.packet').data([0]);

/*packet.enter().append('image')
        .attr('class', 'packet')
        .attr('x', '0')
        .attr('y', '145')
        .attr('width', '30')
        .attr('height', '30')
        .style("z-index", 999)
        .style('opacity', 0)
        .attr('xlink:href', packetImage);
*/
function runtime(node) {
console.log("Runtime");

    node.on('mousedown', function (d) {
        console.log(shellMode);
        if(d.type === "host"){
            if(shellMode === "tab"){
                addTab(d.name);
            }else if(shellMode === "window"){
                var myWindow = window.open(openHostShell(d.name));
                myWindow.document.write('<html><head><title>Host '+d.name+'</title></head><body height="100%" width="100%"><iframe src="' + openHostShell(d.name) + '" height="95%" width="100%" style="border: 0;"></iframe></body></html>');
            }
        }
    });
    //update();
}

/**
 * Enable the animation. Represents a stream of video that move the image (packet) through the network
 * @param {type} returnedRoutes
 */
function streamPacket(returnedRoutes){
//    var returnedRoutes = [{ip:'192.168.1.1'},{dpid:'00:00:00:00:00:00:00:01'},{dpid:'00:00:00:00:00:00:00:02'},{ip:'192.168.2.51'}];
    returnedRoutes = sortReturnedRoutes(returnedRoutes);
    var initial = nodes.filter(function (n) {return n.ip === returnedRoutes[0]['ip'];})[0];
    setTimeout(function () {
        var obj = returnedRoutes[counterStream];
        for(var key in obj){
            if(key === "dpid"){
                dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
            }else if (key === "ip"){
                dest1 = nodes.filter(function (n) {return n.ip === obj[key];})[0];
            }
            packet.style('opacity', 1);
            packet.transition().duration(1000).attr('x', dest1.px).attr('y', dest1.py);
            console.log("streeam");
        }

        counterStream++;
        if (counterStream < returnedRoutes.length) {
            streamPacket(returnedRoutes);
        }else if( packet.attr('x') == dest1.px && packet.attr('y') == dest1.py){
            packet.transition().duration(500).style('opacity', 0);
            packet.transition().delay(500).attr('x', initial.px).attr('y', initial.py);
            counterStream = 0;
            streamPacket(returnedRoutes);
        }else{
            streamPacket(returnedRoutes);
        }
    }, 1000);  
}

/**
 * Uri format: //mininetIP/h1
 * @param {type} nameHost
 * @returns {String}
 */
function openHostShell(nameHost){
	if(nameHost == "h1"){
		var url = "http://"+mininetIP;
	}else{
		var url = "http://"+mininetIP;
	}
	var uri = url + "/" + nameHost;
return uri;
//send uri to src of iframe

//addTab...

}
