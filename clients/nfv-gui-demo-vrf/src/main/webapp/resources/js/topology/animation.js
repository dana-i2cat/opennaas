/*
 * Animation page. Show the topology and the Routing Table.
 *
 */
var file = 'animation';//this javascript file corresponds to a animation page
var counterStream = 0;//counter used in the animation of video streaming
// line displayed when dragging new nodes
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

// handles to link and node element groups
var packet = svg.selectAll('.packet').data([0]);

packet.enter().append('image')
        .attr('class', 'packet')
        .attr('x', '0')
        .attr('y', '110')
        .attr('width', '30')
        .attr('height', '30')
        .style("z-index", 999)
        .attr('xlink:href', packetImage);

function runtime(node) {
console.log("Runtime");
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
 * Sort returned Routes according to the topology drawed using d3js
 *
 * @param {type} routes
 * @returns {Array|sortReturnedRoutes.newRoute}
 */
function sortReturnedRoutes(routes){
    var newRoute = [];
    var node = {}
    var set = true;
    newRoute.push({ip: routes[0]['ip']});

    var nodeSrc = nodes.filter(function (n) {return n.ip === routes[0]['ip']})[0];
    var nodeDst;
    var type;

    for (var j = 1; j < routes.length; ++j) {//each node where the packet goes through
        if ( routes[j]['dpid'] ){
            nodeDst = nodes.filter(function (n) {return n.dpid === routes[j]['dpid']})[0];
            type = "dpid";
        }else{
            nodeDst = nodes.filter(function (n) {return n.ip === routes[j]['ip']})[0];
            type = "ip";
        }
        for ( var i = 0; i < links.length; i++){//find the dest node given a source node. Initial node is the source host
            if( links[i].source == nodeSrc && links[i].target == nodeDst || 
              links[i].target == nodeSrc && links[i].source == nodeDst ){//try to match with a link defined in the GUI
                nodeSrc = nodeDst;
                node = {};
                node[type] = routes[j][type];
                newRoute.push(node);
                set = false;
                break;
            }
        }
        //The follow defined node is not connected with the source node. Moving this node to the next position (j+1)
        if (set) {
            routes = arraymove(routes, j, j+1);
            set = false;
            j--;
        }
    }
//console.log(newRoute);
    return newRoute;
}
/**
 * Move position of array from src to dst.
 * @param {type} arr
 * @param {type} fromIndex
 * @param {type} toIndex
 * @returns {Array|arraymove.arr}
 */
function arraymove(arr, fromIndex, toIndex) {
    var element = arr[fromIndex]
    arr.splice(fromIndex, 1);
    arr.splice(toIndex, 0, element);
    return arr;
}