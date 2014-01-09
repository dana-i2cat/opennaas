/**
 * Home page. Show the topology and allows to click on the nodes in order to see the information about it.
 *
 */
var file = "home";
// handles to link and node element groups
var path = svg.append('svg:g').selectAll('path'),
    circle = svg.append('svg:g').selectAll('g');

// update graph (called when needed)
function restart() {
    // path (link) group
    path = path.data(links);

    // update existing links
    path.classed('selected', function (d) {
        return d === selected_link;
    })
    //    .style('marker-start', function(d) { return d.left ? 'url(#start-arrow)' : ''; })
    .style('marker-end', function (d) {
        return d.right ? 'url(#end-arrow)' : '';
    });

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
        .on('mouseover', function (d) {
console.log("Id num " + d.id_num + "node: " + d.id);
        })
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
            d3.select(this).attr('transform', '');
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            if(d.type === "switch")
                //getFlowTable(d.dpid);
                updateSwInfoTxt(d.dpid, d.controller);
            // select node
            mousedown_node = d;
            if (mousedown_node === selected_node) selected_node = null;
            else selected_node = mousedown_node;
            selected_link = null;
            $(document).on("dragstart", function () {
                return false;
            }); //disable drag in Firefox 3.0 and later
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
 * Call OpenNaaS get Flow Table
 * @param {type} dpid
 * @returns {undefined}
 */
function getFlowTable(dpid) {
    $.ajax({
        type: "GET",
        url: "getInfoSw/" + dpid,
        success: function (data) {
            $('#ajaxUpdate').html(data);
        }
    });
}