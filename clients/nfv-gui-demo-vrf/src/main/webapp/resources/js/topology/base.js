/**
 *  Base file of d3js
 */
var auto = "Automatic";
var man = "Manual";

var switchImage = urlVar+"/topology/switch2.png";//urlVar obtained in header!
var hostImage = urlVar+"/topology/laptop.png";
var controllerImage = urlVar+"/topology/controller2.png";
var packetImage = urlVar+"/topology/movie_tape.gif";
var linkImage = urlVar+"/topology/link_green.png";

// set up SVG for D3
var width = 700,
    height = 447,
    colors = d3.scale.category10();

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
    .nodes(nodes, controllers)
    .links(links, controllersLinks)
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
var controllerLink =  svg.append("svg:g").selectAll("link.ctrl");
var node = svg.append("svg:g").selectAll(".node");

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
        .classed('selected', function (d) {return d === selected_link;});

    /* Links between switchs and controllers */
    controllerLink = controllerLink.data(controllersLinks);
    controllerLink.enter().append("svg:line")
        .attr("class", "linkCtrl")
        .attr("stroke", "green");
    
    link.attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});
    
    controllerLink.attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});

    /* Drawing Controller nodes */
    var controller = svg.selectAll(".nodeCtrl")
        .data(controllers)
        .enter().append("g")
        .attr("class", "nodeCtrl");

    controller.append("image")
        .attr("x", function (d) {return d.x - 20;})
        .attr("y", function (d) {return d.y - 10;})
        .attr("width", 50)
        .attr('height', 50)
        .attr('xlink:href', function (d) {return controllerImage;});

    /* Drawing nodes (switchs and hosts) */
    var node_x = "-30", node_y = "-30";
    var node_width = "75", node_height = "75";
    var node_width_big = "100", node_height_big = node_height;
    var node_txt_x_sw = "-24", node_txt_y_sw = "9";
    var node_txt_x_h = "-10", node_txt_y_h = "30";
    node = node.data(nodes);
    node.enter().append("g")
        .attr("class", "node")
        .style("cursor", "pointer");

    node.append("image")
        .attr('class', function (d) {return d.type;})
        .attr('id', function (d) {return d.id;})
        .attr("x", node_x)
        .attr("y", node_y)
        .attr("width", 75)
        .attr('height', function (d) { return (d.type === "switch") ? "75" : "45";})
        .attr('xlink:href', function (d) { return (d.type === "switch") ? switchImage : hostImage;})
        .on('mouseup', function (d) {
            d3.selectAll(".switch").attr("width", node_width); //image normal
            d3.selectAll(".host").attr("width", node_width).attr("height", 45); //image normal
            d3.selectAll(".id_txt_sw").attr("x", node_txt_x_sw).attr("y", node_txt_y_sw);
            d3.selectAll(".id_txt_host").attr("x", node_txt_x_h).attr("y", node_txt_y_h);
            d3.select(this).attr("width", node_width_big).attr("height", node_height_big); //image big

            if (d.type === "switch")
                d3.select("#" + d.id + "_text").attr("x", -15).attr("y", 12); //move text when big
            else if (d.type === "host")
                d3.select("#" + d.id + "_text").attr("x", -9).attr("y", 55); //move text when big
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
        .attr('id', function (d) { return d.id + "_text";})
        .attr('class', function (d) { return (d.type === "switch") ? 'id_txt_sw' : "id_txt_host";})
        .attr("dx", 12)
        .attr("dy", function (d) { return (d.type === "switch") ? "1.10em" : ".30em";})
        .attr('x',  function (d) { return (d.type === "switch") ? node_txt_x_sw : node_txt_x_h;})
        .attr('y', function (d) { return (d.type === "switch") ? node_txt_y_sw :  node_txt_y_h;})
        .text(function (d) { return d.id;});

    node.attr("transform", function (d) {
        return "translate(" + d.x + "," + d.y + ")";
    });
    
    force.on("tick", function () {
        runtime(node, links);
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
console.log("tinck");

}

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

function spliceLinksForNode(node) {
    var toSplice = links.filter(function (l) {
        return (l.source === node || l.target === node);
    });
    toSplice.map(function (l) {
        links.splice(links.indexOf(l), 1);
    });
}

function mouseup() {
console.log("MouseUp");
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