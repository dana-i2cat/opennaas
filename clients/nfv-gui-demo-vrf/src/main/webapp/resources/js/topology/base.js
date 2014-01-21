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
/*.on('click', cleanDrag())*/
;

// init D3 force layout
var force = d3.layout.force()
    .nodes(nodes, controllers)
    .links(links, controllersLinks)
    .size([width, height])
    .linkDistance(350)
    .charge(-500)
    .on('tick', tick);

/////////////////////
/**
 * Drawing topology (nodes, links, controllers)
 */
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
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
console.log("Update image");
    link = link.data(links);
    link.classed('selected', function (d) {return d === selected_link;})
    .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});
    
    link.enter().append("svg:line")
        .attr('id', function (d) {return d.id;})
        .attr('class', function (d) {
            if (d.type === "static") {
                return 'link';
            } else {
                return 'link2';
            }
        })
        .classed('selected', function (d) {return d === selected_link;});
    
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

    var controller = svg.selectAll(".nodeCtrl")
        .data(controllers)
        .enter().append("g")
        .attr("class", "nodeCtrl");

    controller.append("image")
        .attr("x", function (d) {return d.x - 20;})
        .attr("y", function (d) {return d.y - 15;})
        .attr("width", 50)
        .attr('height', 50)
        .attr('xlink:href', function (d) {return controllerImage;});

    node = node.data(nodes);
    node.enter().append("g")
        .attr("class", "node");

    node.append("image")
        .attr('class', function (d) {
            return d.type;
        })
        .attr('id', function (d) {
            return d.id;
        })
        .attr("x", -30)
        .attr("y", -30)
        .attr("width", 75)
        .attr('height', function (d) {
            if (d.type === "switch")
                return "75";
            else
                return "45";
        })
        .attr('xlink:href', function (d) {
            if (d.type === "switch")
                return switchImage;
            else
                return hostImage;
        }).on('mouseup', function (d) {
            d3.selectAll(".switch").attr("width", 75); //image big
            d3.selectAll(".host").attr("width", 75).attr("height", 45); //image big
            d3.selectAll(".id_txt_sw").attr("x", "-20").attr("y", "9");
            d3.selectAll(".id_txt_host").attr("x", "-20").attr("y", "30");
            d3.select("#" + d.id).attr("width", 100).attr("height", 75); //image big
            if (d.type === "switch")
                d3.select("#" + d.id + "_text").attr("x", "-9").attr("y", "12"); //move text
            else if (d.type === "host")
                d3.select("#" + d.id + "_text").attr("x", "-9").attr("y", "35"); //move text
            if (!mousedown_node) return;
            if (d.type === "switch") {
                d3.selectAll('.link2').attr('d', 'M0,0L0,0');
                //show a warning message in order to inform that the connection to a destination switch is not allowed.
                //http request to OpenNaaS
                return;
            }
            selected_node = null;
            //restart();
            //mousedown();
            //mouseup();
        });

    node.append("text")
        .attr('class', function (d) {
            if (d.type === "switch") {
                return 'id_txt_sw';
            } else {
                return "id_txt_host";
            }
        })
        .attr('id', function (d) {
            return d.id + "_text";
        })

    .attr("dx", 12)
        .attr("dy", ".80em")
        .style('fill', 'red')
        .attr('x', "-20")
        .attr('y', function (d) {
            if (d.type === "switch") {
                return "9";
            } else {
                return "30";
            }
        })
        .text(function (d) {
            return d.id;
        });


    node.attr("transform", function (d) {
        return "translate(" + d.x + "," + d.y + ")";
    });
    
    
    
    force.on("tick", function () {
        runtime(node, links);
    });
}

function updateLinks(){
    console.log("Update image");
    link = link.data(links);
    link.classed('selected', function (d) {return d === selected_link;})
    .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});
    
    link.enter().append("svg:line")
        .attr('id', function (d) {return d.id;})
        .attr('class', function (d) {
            if (d.type === "static") {
//                console.log("STAAAAAAATIC");
                return 'link';
            } else {
                console.log("NOOOOO STAAAAAAATIC");
                return 'link2';
            }
        })
        .classed('selected', function (d) {return d === selected_link;});
    
    controllerLink = controllerLink.data(controllersLinks);
    controllerLink.enter().append("svg:line")
        .attr("class", "linkCtrl")
        .attr("stroke", "green");
    
    link.attr("x1", function (d) {return d.source.x;})
        .attr("y1", function (d) {return d.source.y;})
        .attr("x2", function (d) {return d.target.x;})
        .attr("y2", function (d) {return d.target.y;});
}

/** End drawing topology **/

/////////////////////////

// update force layout (called automatically each iteration)
function tick() {
    console.log("tinck");

/*        node.attr('transform', function (d) {
        return 'translate(' + d.x + ',' + d.y + ')';
    });*/
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

    // ctrl
    if (d3.event.keyCode === 17) {
        circle.call(force.drag);
        svg.classed('ctrl', true);
    }
    console.log("KeyDown: " + selected_link.type);
    if (!selected_node && !selected_link) return;
    if (selected_link.type != "new_link") return;
}

function keyup() {
    lastKeyDown = -1;

    // ctrl
    if (d3.event.keyCode === 17) {
        circle
            .on('mousedown.drag', null)
            .on('touchstart.drag', null);
        svg.classed('ctrl', false);
    }
}

function mousedown() {
    console.log("MouseDown");
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
    //    restart();
}

function mouseup() {
    console.log("MouseUp");
    if (mousedown_node && file != "home") {
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
    if ((typeof (mode) == "undefined")) {
        d3.selectAll('.link2').attr('d', 'M0,0L0,0');
    } else if (mode == man) {
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