/*
    Base file of d3js
*/
var auto = "Automatic";
var man = "Manual";
// set up SVG for D3
/*for each type of view, different size? */
var width = 760,
    height = 450,
    colors = d3.scale.category10();

// set up initial nodes and links
//  - nodes are known by 'id', not by index in array.
//  - reflexive edges are indicated on the node (as a bold black circle).
//  - links are always source < target; edge directions are set by 'left' and 'right'.
var svg = d3.select('#chart')
    .append('svg')
    .attr('width', width)
    .attr('height', height)
    /*.on('click', cleanDrag())*/;

// init D3 force layout
var force = d3.layout.force()
    .nodes(nodes)
    .links(links)
    .size([width, height])
    .linkDistance(350)
    .charge(-500)
    .on('tick', tick);

// update force layout (called automatically each iteration)
function tick() {
    // draw directed edges with proper padding from node centers
    path.attr('d', function (d) {
        var deltaX = d.target.x - d.source.x,
            deltaY = d.target.y - d.source.y,
            dist = Math.sqrt(deltaX * deltaX + deltaY * deltaY),
            normX = deltaX / dist,
            normY = deltaY / dist,
            sourcePadding = d.left ? 17 : 12,
            targetPadding = d.right ? 17 : 12,
            sourceX = d.source.x + (sourcePadding * normX),
            sourceY = d.source.y + (sourcePadding * normY),
            targetX = d.target.x - (targetPadding * normX),
            targetY = d.target.y - (targetPadding * normY);
        return 'M' + sourceX + ',' + sourceY + 'L' + targetX + ',' + targetY;
    });

    circle.attr('transform', function (d) {
        return 'translate(' + d.x + ',' + d.y + ')';
    });
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

// only respond once per keydown
var lastKeyDown = -1;

function keydown() {
    d3.event.preventDefault();

    if (lastKeyDown !== -1) return;
    lastKeyDown = d3.event.keyCode;
    
    if (d3.event.keyCode === 17) {// ctrl
        circle.call(force.drag);
        svg.classed('ctrl', true);
    }
//console.log("KeyDown: "+selected_link.type);
//    if (!selected_node && !selected_link) return;
//    if (selected_link.type != "new_link") return;
}

function keyup() {
    lastKeyDown = -1;
    if (d3.event.keyCode === 17) {// ctrl
        circle
            .on('mousedown.drag', null)
            .on('touchstart.drag', null);
        svg.classed('ctrl', false);
    }
}

function mousedown() {
    // prevent I-bar on drag
    //d3.event.preventDefault();

    // because :active only works in WebKit?
    svg.classed('active', true);

    if (d3.event.ctrlKey || mousedown_node || mousedown_link) return;

    // insert new node at point
    var point = d3.mouse(this),
        node = {
            id: ++lastNodeId,
            reflexive: false
        };
    node.x = point[0];
    node.y = point[1];
    //nodes.push(node);

    restart();
}
function mouseup() {
    if (mousedown_node && file != "home") {
        // hide drag line
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
function cleanDrag(){
//console.log("Removing actual paths..");
    if((typeof(mode) == "undefined")){
        d3.selectAll('.link2').attr('d', 'M0,0L0,0');
    } else if(mode == man){
        d3.selectAll('.link2').attr('d', 'M0,0L0,0');
        setActive(null);
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
console.log("IP: "+ip+" Subnet: "+subnet);
    var mask, base_ip, long_ip = ip2long(ip);
    var regex = /^(.*?)\/(\d{1,2})$/;
    if( (mask = subnet.match(regex)) && ((base_ip=ip2long(mask[1])) >= 0) ){
        var freedom = Math.pow(2, 32 - parseInt(mask[2]));
        return (long_ip > base_ip) && (long_ip < base_ip + freedom - 1);
    }
    else return false;
}

function ValidateIPaddress(ipaddress){
console.log(ipaddress);
    var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
    if(ipaddress.match(ipformat)){
        return true;
    }
    return false;
}  