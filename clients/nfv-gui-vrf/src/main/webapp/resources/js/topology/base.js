/**
 *  Base file of d3js
 */
console.log("Loading BASE.js");
var mininetIP = "84.88.40.153:5642";

/*
if(sessvars.nodes){
    nodes = sessvars.nodes;
    links = sessvars.links;
}
*/

//var storedNodes = nodes;
//if(undefined == linkColor || linkColor == null){
//    linkColor = "black";
//}
var ctrlLinkColor = "#ccc";
var auto = "Automatic";
var man = "Manual";
var ctrlKey = false;//ctrl key is pressed?

/* Images size */
var node_size_width = 50, node_size_height_sw = 50, node_size_height_h = 35;
var node_size_width_big = node_size_width + 25, node_size_height_sw_big = node_size_height_sw+25, node_size_height_h_big = 35;

var switchImage = urlVar+"/topology/switch2.png";//urlVar obtained in header!
var hostImage = urlVar+"/topology/laptop.png";
var controllerImage = urlVar+"/topology/controller2.png";
var controllerFDLImage = urlVar+"/topology/controller_fdl.png";
var controllerODLImage = urlVar+"/topology/controller_odl.png";
var packetImage = urlVar+"/topology/movie_tape.gif";
var linkImage = urlVar+"/topology/link_green.png";
var helpImage = urlVar+"/topology/helpImage.png";
var cloudONImage = urlVar+"/topology/opennaas_cloud.png";
var domainCloudImage = urlVar+"/topology/cloud.png";
var i2CAT_Logo = urlVar+"/logo-color-transparent.png";
var PSNC_Logo = urlVar+"/psnc_logo_3_new.png";

// set up SVG for D3
var width = 700,
    height = 447,
    colors = d3.scale.category10(),
    padding = 30;

// set up initial nodes and links
//  - nodes are known by 'id', not by index in array.
//  - reflexive edges are indicated on the node (as a bold black circle).
//  - links are always source < target; edge directions are set by 'left' and 'right'.
var svg = d3.select('#chart')
    .append('svg')
    .attr('width', width)
    .attr('height', height);
/*.on('click', cleanDrag());*/

// init D3 force layout
var force = d3.layout.force()
    .nodes(nodes)
    .links(links)
    .size([width, height])
    .linkDistance(350)
    .charge(-500)
    .on('tick', tick);

/**
 * Drawing topology (nodes, links, controllers)
 */
var drag_line = svg.append('svg:path')
    .attr('class', 'dragline hidden')
    .attr('d', 'M0,0L0,0');
var link = svg.append("svg:g").selectAll("link.sw");
var node = svg.append("svg:g").selectAll(".node");
var help = svg.append("svg:g").selectAll(".help");

d3.json("", function (error, json) {
    force.start();
    root = json;
    update();
});

function update(){
    /* Links between switches and hosts */
    link = link.data(links);
    link.classed('selected', function (d) {return d === selected_link;})
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});
    
    link.enter().append("svg:line")
        .attr('id', function (d) {return d.id;})
        .attr('class', function (d) { return (d.type === "static") ? 'link' : 'link2';})
        .classed('selected', function (d) {return d === selected_link;})
	.attr("stroke", "black");
    //remove selected/drawed links
    d3.selectAll(".link2").remove();
       
    link.attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});

    /* Drawing nodes (switchs and hosts) */
    var node_x = "-30", node_y = "-30";
    var node_width = 50, node_height = 50, node_height_h = 35;
    var node_width_big = node_width + 25, node_height_big = node_height;
    var node_txt_x_sw = "-24", node_txt_y_sw = "-35";//9
    var node_txt_x_h = "-10", node_txt_y_h = "25";
    node = node.data(nodes);
    node.enter().append("g")
        .attr("class", "node")
        .style("cursor", "pointer")
        .call(d3.behavior.drag()
            .on("dragstart", function(d){
console.log("Dragstart");                
                d3.event.sourceEvent.stopPropagation();
                d3.select(this).classed("dragging", true);
            })
            .on("drag", function(d) {
                if(ctrlKey){
                    d.px += d3.event.dx;
                    d.py += d3.event.dy;
                    d.x = d3.event.x, d.y = d3.event.y;
                    var t = d;
                    nodes[d.id].x = d.x;
                    nodes[d.id].y = d.y;
                    nodes[d.id].fixed = true;
//                    nodes[d.id].px = d.x;
//                    nodes[d.id].py = d.y;
                    node.filter(function(d) { return d.name === t.name; }).attr("transform", transform);
//      	        d3.select(this).attr("cx", d.x).attr("cy", d.y).attr("transform", function(d) { return "translate(" + d.x + ", "+d.y+")"; });
                    link.filter(function(l) { return l.source === d; }).attr("x1", d.x).attr("y1", d.y);
                    link.filter(function(l) { return l.target === d; }).attr("x2", d.x).attr("y2", d.y);
                    
                }
            })
            .on("dragend", function(d){
                console.log("Calls");
                updateTopology(nodes, links);
                d3.select(this).classed("dragging", false);
            })
	);

    node.append("image")
        .attr('class', function (d) {return d.type;})
        .attr('id', function (d) {return d.name;})
        .attr("x", node_x)
        .attr("y", node_y)
        .attr("width", node_width)
        .attr('height', function (d) { return (d.type === "switch") ? node_height : node_height_h;})
        .attr('xlink:href', function (d) { return (d.type === "switch") ? switchImage : hostImage;})
        .on('mouseup', function (d) {
            d3.selectAll(".switch").attr("width", node_width); //image normal
            d3.selectAll(".host").attr("width", node_width).attr("height", 45); //image normal
            d3.selectAll(".id_txt_sw").attr("x", node_txt_x_sw).attr("y", node_txt_y_sw);
            d3.selectAll(".id_txt_host").attr("x", node_txt_x_h).attr("y", node_txt_y_h);
            d3.select(this).attr("width", node_width_big).attr("height", node_height_big); //image big

            if (d.type === "switch")
                d3.select("#" + d.name + "_text").attr("x", -15).attr("y", -40); //move text when big  12
            else if (d.type === "host")
                d3.select("#" + d.name + "_text").attr("x", -9).attr("y", 35); //move text when big
            if (!mousedown_node) return;
            if (d.type === "switch") {
                d3.selectAll('.link2').attr('d', 'M0,0L0,0');
                //show a warning message in order to inform that the connection to a destination switch is not allowed.
                //http request to OpenNaaS
                return;
            }
            selected_node = null;
        });
        
    /* Drawing text of each node */
    node.append("text")
        .attr('id', function (d) { return d.name + "_text";})
        .attr('class', function (d) { return (d.type === "switch") ? 'id_txt_sw' : "id_txt_host";})
        .attr("dx", 12)
        .attr("dy", function (d) { return (d.type === "switch") ? "1.10em" : ".30em";})
        .attr('x',  function (d) { return (d.type === "switch") ? node_txt_x_sw : node_txt_x_h;})
        .attr('y', function (d) { return (d.type === "switch") ? node_txt_y_sw :  node_txt_y_h;})
        .text(function (d) { return d.name;});

    node.attr("transform", function (d) {
        var new_x = d.x;
        var new_y = d.y;
        if(d.x < 30) new_x = 40 + Math.floor((Math.random()*15)+1);
        else if(d.x > 620) new_x = 580 - Math.floor((Math.random()*15)+1);
        if(d.y < 30) new_y = 40 + Math.floor((Math.random()*15)+1);
        else if(d.y > 390) new_y = 390 - Math.floor((Math.random()*15)+1);
        if(d.id === 2){
            console.log(d.x+" "+new_x);
        }
        return "translate(" + new_x + "," + new_y + ")";
    });
    
    help = help.data([0]);
    help.enter().append("svg:image")
        .attr('x', 20)
        .attr('y', height - 30)
        .attr("id", "helpImage")
        .attr("width", 20)
        .attr("height", 20)
        .attr('xlink:href', helpImage)
        .on('mouseover', mouseoverhelp);
    
    force.on("tick", function () {
        runtime(node, links/*, controller*/);
    });

}

/**
 * Redraw the links
 * @returns {undefined}
 */
function updateLinks(){
console.log("Update links");
    link = link.data(links);
    link.classed('selected', function (d) {return d === selected_link;})
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});
    
    link.enter().append("svg:line")
        .attr('id', function (d) {return d.id;})
        .attr('class', function (d) { return (d.type === "static") ? 'link' : 'link2';})
        .classed('selected', function (d) {return d === selected_link;});
    
    link.attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});
}

/** End drawing topology **/

// update force layout (called automatically each iteration)
function tick() {
    console.log("tick");
}

function keydown() {
    if(!selected_node)return;
    d3.event.preventDefault();

    // ctrl
    if (d3.event.keyCode === 17) {
        console.log("Ctrl key selected node "+selected_node);
        d3.select(selected_node).attr("x", d3.event.x);
//        dragmove(selected_node);
    }
}

d3.select(window)
    .on('keydown', keydown);

// mouse event vars
var selected_node = null,
    selected_link = null,
    mousedown_link = null,
    mousedown_node = null,
    mouseup_node = null;

function resetMouseVars() {
    mousedown_node = null;
    mouseup_node = null;
    mousedown_link = null;
}

d3.select(window)
    .on('keydown', keydown)
    .on('keyup', keyup);

function spliceLinksForNode(node) {
    var toSplice = links.filter(function (l) {
        return (l.source === node || l.target === node);
    });
    toSplice.map(function (l) {
        links.splice(links.indexOf(l), 1);
    });
}

function keydown() {
  ctrlKey = d3.event.ctrlKey || d3.event.metaKey;
}

function keyup() {
  ctrlKey = false;
}

function mouseup() {
console.log("MouseUp");
    d3.selectAll('.dragline').attr('d', 'M0,0L0,0'); //Remove the requested path
    if (mousedown_node) {
        drag_line
            .classed('hidden', true)
            .style('marker-end', '');
    }
    // because :active only works in WebKit?
    svg.classed('active', false);

    // clear mouse event vars
    resetMouseVars();
}

/* Remove the drag line used when we create links.. THis function is enabled when we click in any place of the div(<p>)*/
function cleanDrag() {
console.log("Removing actual paths..");
    if ((typeof(mode) == "undefined")) {
        d3.selectAll('.link2').attr('d', 'M0,0L0,0');
    } else if (mode === man) {
        d3.selectAll('.link2').attr('d', 'M0,0L0,0');
    }
}

function ip2long(ip){
    var components;
    var regex = /^(\d{1,3})\.(\d{1,3})\.(\d{1,3})\.(\d{1,3})$/;
    if(components = ip.match(regex)){
        var iplong = 0;
        var power  = 1;
        for(var i=4; i>=1; i-=1){
            iplong += power * parseInt(components[i]);
            power  *= 256;
        }
        return iplong;
    }
    else return -1;
}

function inSubNet(ip, subnet){
    var mask, base_ip, long_ip = ip2long(ip);
    var regex = /^(.*?)\/(\d{1,2})$/;
    if( (mask = subnet.match(regex)) && ((base_ip=ip2long(mask[1])) >= 0) ){
        var freedom = Math.pow(2, 32 - parseInt(mask[2]));
        return (long_ip > base_ip) && (long_ip < base_ip + freedom - 1);
    }
    else return false;
}

function ValidateIPaddress(ipaddress){
    var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if(ipaddress.match(ipformat)){
        return true;
    }
    return false;
}  

/**
 * Sort returned Routes according to the topology drawed using d3js
 *
 * @param {type} routes
 * @returns {Array|sortReturnedRoutes.newRoute}
 */
function sortReturnedRoutes(routes){
    var newRoute = [];
    var node = {};
    var set = true;
    newRoute.push({ip: routes[0]['ip']});

    var nodeSrc = nodes.filter(function (n) {return n.ip === routes[0]['ip']; })[0];
    var nodeDst;
    var type;

    for (var j = 1; j < routes.length; ++j) {//each node where the packet goes through
        if ( routes[j]['dpid'] ){
            nodeDst = nodes.filter(function (n) {return n.dpid === routes[j]['dpid'];})[0];
            type = "dpid";
        }else{
            nodeDst = nodes.filter(function (n) {return n.ip === routes[j]['ip'];})[0];
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
            }else{//if not exists a match, move the dpid to the final of the array in order to analyze later
                set = true;
            }
        }
        //The follow defined node is not connected with the source node. Moving this node to the next position (j+1)
        if ( set ) {
            routes = arraymove(routes, j, routes.length);
            set = false;
            j--;
        }
    }
//console.log(newRoute);
    return newRoute;
}

/**
 * Move element of array from src to dst.
 * @param {type} arr
 * @param {type} fromIndex
 * @param {type} toIndex
 * @returns {Array|arraymove.arr}
 */
function arraymove(arr, fromIndex, toIndex) {
    var element = arr[fromIndex];
    arr.splice(fromIndex, 1);
    arr.splice(toIndex, 0, element);
    return arr;
}

function dragmove(d) {
    console.log(d);
    d3.select(d)
        .attr("x", d.x = d3.event.x)
        .attr("y", d.y = d3.event.y);
}

function transform(d) {
    return "translate(" + d.x + "," + d.y + ")";
}

function mouseoverhelp(){
    $('#helpImage').tipsy({
        live: true,
        fade: true,
        html: true, 
        gravity: 'w', 
        title: function() {
            var d = this.__data__;
            var info = "Press <b>control key</b> while you drag the nodes.";
            return info;
        }
    });
}

/**
 * Update session vars.
 * @param {type} storedNodes
 * @param {type} storedLinks
 * @returns {undefined}
 */
function updateTopology(storedNodes, storedLinks){
    console.log("Update Topology");
    sessvars.$.clearMem();
    sessvars.nodes = storedNodes;
    sessvars.links = storedLinks;
    sessvars.$.flush()
}

// Resolve collisions between nodes.
function collide(alpha) {
    var maxRadius = 12;
  var quadtree = d3.geom.quadtree(nodes);
  return function(d) {
    var r = d.radius + maxRadius + padding,
        nx1 = d.x - r,
        nx2 = d.x + r,
        ny1 = d.y - r,
        ny2 = d.y + r;
    quadtree.visit(function(quad, x1, y1, x2, y2) {
      if (quad.point && (quad.point !== d)) {
        var x = d.x - quad.point.x,
            y = d.y - quad.point.y,
            l = Math.sqrt(x * x + y * y),
            r = d.radius + quad.point.radius + (d.color !== quad.point.color) * padding;
        if (l < r) {
          l = (l - r) / l * alpha;
          d.x -= x *= l;
          d.y -= y *= l;
          quad.point.x += x;
          quad.point.y += y;
        }
      }
      return x1 > nx2 || x2 < nx1 || y1 > ny2 || y2 < ny1;
    });
  };
}
