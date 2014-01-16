/*
 * Config Route page. Show the topology and the Routing Table.
 *
 */
var file = "config";
// line displayed when dragging new nodes
var drag_line = svg.append('svg:path')
    .attr('class', 'link2 dragline hidden')
    .attr('d', 'M0,0L0,0');

// handles to link and node element groups
var path = svg.append('svg:g').selectAll('path'),
    circle = svg.append('svg:g').selectAll('g');

// update graph (called when needed)
function restart() {
    // path (link) group
    path = path.data(links);

    // update existing links
    path.classed('selected', function (d) {return d === selected_link;})
    .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';});

    // add new links
    path.enter().append('svg:path')
        .attr('class', function (d) {
            if (d.type === "static") {
                return 'link';
            } else {
                return 'link2';
            }
        })
        .classed('selected', function (d) {return d === selected_link;})
        .style('marker-start', function (d) {return d.left ? 'url(#start-arrow)' : '';})
        .style('marker-end', function (d) {return d.right ? 'url(#end-arrow)' : '';})
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            // select link
            mousedown_link = d;
console.log("Adding new link. Click on Link " + d.type);
            if (mousedown_link === selected_link) selected_link = null;
            else selected_link = mousedown_link;
            selected_node = null;
            restart();
        });

    // remove old links
    path.exit().remove();

    // circle (node) group
    // NB: the function arg is crucial here! nodes are known by id, not by index!
    circle = circle.data(nodes, function (d) {return d.id;});

    // update existing nodes (reflexive & selected visual states)
    circle.selectAll('circle')
        .style('fill', function (d) {
            return (d === selected_node) ? d3.rgb(colors(d.id)).brighter().toString() : colors(d.id);
        })
        .classed('reflexive', function (d) {return d.reflexive;});

    // add new nodes
    var g = circle.enter().append('svg:g');

    g.append('svg:image')
        .attr('class', function (d) {return d.type;})
        .attr('x', "-40")
        .attr('y', "-40")
        .attr('width', "75")
        .attr('height', "75")
        .attr('xlink:href', function (d) {
            if (d.type === "switch")
                return switchImage;
            else
                return hostImage;
        })
        .on('mouseover', mouseoverimage)
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
            d3.select(this).attr('transform', '');
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
console.log("Mousedown");
            // select node
            mousedown_node = d;
            if (mousedown_node === selected_node) selected_node = null;
            else selected_node = mousedown_node;
            selected_link = null;
            $(document).on("dragstart", function () {
                return false;
            }); //disable drag in Firefox 3.0 and later
            // reposition drag line
            drag_line
                .style('marker-end', 'url(#end-arrow)')
                .classed('hidden', false)
                .attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + mousedown_node.x + ',' + mousedown_node.y);
            restart();
        })
        .on('mouseup', function (d) {
            d3.selectAll(".switch").attr("width", 75); //image big
            d3.selectAll(".host").attr("width", 75); //image big
            d3.selectAll(".id_txt_sw").attr("x", "-20").attr("y", "9");
            d3.selectAll(".id_txt_host").attr("x", "-20").attr("y", "30");
            d3.select(this).attr("width", 100); //image big
            if (d.type === "switch")
                d3.select("#" + d.id).attr("x", "-9").attr("y", "12"); //move text
            else if (d.type === "host")
                d3.select("#" + d.id).attr("x", "-9").attr("y", "35"); //move text
            if (!mousedown_node) return;
            if (d.type === "switch") {
                d3.selectAll('.link2').attr('d', 'M0,0L0,0');
                //show a warning message in order to inform that the connection to a destination switch is not allowed.
                //http request to OpenNaaS
                return;
            }
console.log("Click on node " + d.id);
            // needed by FF
            drag_line
                .classed('hidden', true)
                .style('marker-end', '');
            // check for drag-to-self
            mouseup_node = d;
            if (mouseup_node === mousedown_node) {
                resetMouseVars();
                return;
            }
            // unenlarge target node
            d3.select(this).attr('transform', '');
console.log("MouseUp Click");
            // add link to graph (update if exists)
            // NB: links are strictly source < target; arrows separately specified by booleans
            var source, target, direction;
            if (mousedown_node.id < mouseup_node.id) {
                source = mousedown_node;
                target = mouseup_node;
                direction = 'right';
            } else {
                source = mouseup_node;
                target = mousedown_node;
                direction = 'left';
            }
console.log("Source h " + source.id+" Dest h " + target.id);
            var link;
            link = links.filter(function (l) {return (l.source === source && l.target === target); })[0];

            if (link) {
                link[direction] = true;
            }
//request to OpenNaaS
            swNode = nodes.filter(function (n) {return n.id === source.SW; });
            var returnedRoutes = eval('(' + getRoute(source.ip, target.ip, swNode[0].dpid, source.port) + ')');
//            returnedRoutes = [{ip:'192.168.1.1'},{dpid:'00:00:00:00:00:00:00:01'},{dpid:'00:00:00:00:00:00:00:03'},{ip:'192.168.2.51'}];
console.log(returnedRoutes);
            for(var i=1;i<returnedRoutes.length;i++){//i=1 because the first position is the source
                var obj = returnedRoutes[i];
                 for(var key in obj){
console.log("Json key: "+key+" Json value: "+obj[key]);
                    dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
                    if(key === "dpid"){
                        dest1 = nodes.filter(function (n) {return n.dpid === obj[key];})[0];
                    }else if (key === "ip"){
                        dest1 = nodes.filter(function (n) {return n.ip === obj[key];})[0];
                        highlight(dest1.ip);
                    }
                    link = {source: source, target: dest1, left: false, right: false, type: "new_link"};
                    link[direction] = true;
console.log(link);
                    links.push(link);
console.log(source.ip);
                    source = dest1;
                }
            }
            d3.selectAll('.link2').attr('d', 'M0,0L0,0'); //Remove the requested path

            // select new link
            selected_link = link;
            selected_node = null;
            restart();
            mousedown();
            mouseup();
        });

    // show node IDs
    g.append('svg:text')
        .attr('x', "-20")
        .attr('y', function (d) {
            if (d.type === "switch") {
                return "9";
            } else {
                return "30";
            }
        })
        .attr('class', function (d) {
            if (d.type === "switch") {
                return 'id_txt_sw';
            } else {
                return "id_txt_host";
            }
        })
        .attr('id', function (d) {
            return d.id;
        })
        .style('fill', 'red')
        .text(function (d) {
            return d.id;
        });

    g.append('svg:image')
        .attr('class', "controller")
        .attr('x', "-20")
        .attr('y', "-60")
        .attr('width', "40")
        .attr('height', "40")
        .attr('xlink:href', controllerImage);
    // remove old nodes
    circle.exit().remove();

    // set the graph in motion
    force.start();
}

function mousemove() {
    if (!mousedown_node) return;
    // update drag line
    drag_line.attr('d', 'M' + mousedown_node.x + ',' + mousedown_node.y + 'L' + d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
    restart();
}

// app starts here
svg.on('mousedown', mousedown)
    .on('mousemove', mousemove)
    .on('mouseup', mouseup);
d3.select(window)
    .on('keydown', keydown)
    .on('keyup', keyup);
restart();

/**
 * Call OpenNaaS get Route Table
 * @param {type} dpid
 * @returns {undefined}
 */
function getRouteTable(dpid) {
    $.ajax({
        type: "GET",
        url: "getInfoSw/" + dpid,
        success: function (data) {
            $('#ajaxUpdate').html(data);
        }
    });
}
/**
 * Get Route to OpenNaaS
 * @param {type} ipSrc
 * @param {type} ipDst
 * @param {type} dpid
 * @param {type} inPort
 * @returns {data}
 */
function getRoute(ipSrc, ipDst, dpid, inPort) {
    var response;
    $.ajax({
        type: "GET",
        async:false,
        url: "getRoute/" + ipSrc+"/"+ipDst+"/"+dpid+"/"+inPort,
        success: function (data) {
            response = data;
        }
    });
    return response;
}

/**
 * Highlight the rows according to the requested route
 * @param {type} word
 * @returns {undefined}
 */
function highlight(word){
    var table = document.getElementById('jsonTable');
    var tbody = table.getElementsByTagName('tbody')[0];
    var items = tbody.getElementsByTagName('tr');
    var tds = null;

    for (var j = 0; j < items.length; j++) {
        tds = items[j].getElementsByTagName('td');
        for (var i = 0; i < tds.length-1; i++) {
            if(tds[i].innerHTML === word){
                table.getElementsByTagName('tr')[j+1].style.background = 'yellow';
            }else if(inSubNet(word, tds[i].innerHTML)){
                table.getElementsByTagName('tr')[j+1].style.background = 'yellow';
            }
        }
    }
}

function mouseoverimage(){
    $('svg image').tipsy({
        fade: true,
        html: true, 
        gravity: 'w', 
        title: function() {
            var d = this.__data__;
            var info ="<b>Id:</b> "+d.id+"<br>";
            if(d.type != "host"){
                info +="<b>DPID:</b> "+d.dpid+"<br>";
                info +="<b>Controller:</b> "+d.controller+"<br>";
            } else {
                info +="<b>IP:</b> "+d.ip+"<br>";
                info +="<b>Connected with:</b> "+d.SW+"<br>";
            }
            return info;
        }
    });
}