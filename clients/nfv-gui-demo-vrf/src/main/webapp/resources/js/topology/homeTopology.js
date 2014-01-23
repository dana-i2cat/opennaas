/**
 * Home page. Show the topology and allows to click on the nodes in order to see the information about it.
 *
 */
var file = "config";

console.log("test");

function runtime(node) {

    node
        .on('mouseover', function (d) {
            console.log("Id num " + d.id_num + "node: " + d.id);
        })
        .on('mouseout', function (d) {
            if (!mousedown_node || d === mousedown_node) return;
            // unenlarge target node
            //            d3.select(this).attr('transform', '');//put the nodes at [0,0]
        })
        .on('mousedown', function (d) {
            if (d3.event.ctrlKey) return;
            if (d.type === "switch")
//                getFlowTable(d.dpid);
            updateSwInfoTxt(d.dpid, getControllerInfo(d.controller));
            // select node
            mousedown_node = d;
            if (mousedown_node === selected_node) selected_node = null;
            else selected_node = mousedown_node;
            selected_link = null;
            $(document).on("dragstart", function () {
                return false;
            }); //disable drag in Firefox 3.0 and later
            //restart();
        });
}

/** Show Legend **/
legendData = [{
        "label": "Static link"
    },
    {
        "label": "OpenFlow Switch"
    },
    {
        "label": "OpenFlow Controller"
    },
    {
        "label": "Host"
    }];
var legend_x = 500,
    legend_y = 290,
    legend_width = 150,
    legend_height = 120;
var hidden = true;
var legend = svg.selectAll('.legend').data(legendData),
    legendLink = svg.selectAll(".legendText").data([0]);

legend.enter().append("svg")
    .attr("class", "legend")
    .attr("width", legend_width)
    .attr("height", legend_y)
    .attr('x', legend_x)
    .attr('y', function (d, i) {
        return legend_y + i * 40;
    })
    .style("opacity", 0)
    .selectAll("g")
    .append("g");

legend.append("image")
    .attr("width", 30)
    .attr("height", 20)
    .attr('xlink:href', function (d, i) {
        if (i === 0)
            return linkImage;
        if (i === 1)
            return switchImage;
        if (i === 2)
            return controllerImage;
        if (i === 3)
            return hostImage;
    });

legend.append("text")
    .attr("x", 34)
    .attr("y", 10)
    .attr("dy", ".35em")
    .text(function (d) {
        return d.label;
    });

var rectangleData = [{
    "x": legend_x - 20,
    "y": legend_y - 15,
    "rx": 20,
    "ry": 20,
    "height": 0,
    "width": legend_width + 30
}];
var rectangles = svg.selectAll("rect")
    .data(rectangleData)
    .enter()
    .append("rect")
    .attr("x", function (d) {
        return d.x;
    })
    .attr("y", function (d) {
        return d.y;
    })
    .attr("rx", function (d) {
        return d.rx;
    })
    .attr("ry", function (d) {
        return d.ry;
    })
    .attr("height", function (d) {
        return d.height;
    })
    .attr("width", function (d) {
        return d.width;
    })
    .style("fill", "blue").style("stroke", "black")
    .style("stroke-width", 2).style("fill-opacity", 0.1).style("stroke-opacity", 0.9)
    .on('mousedown', function () {
        toggleHideLegend();
    });

legendLink = svg.append("foreignObject")
    .attr("x", legend_x+40).attr("y", legend_y - 35)
    .attr("width", 480)
    .attr("height", 500)
    .append("xhtml:body")
    .style("font", "14px 'Helvetica Neue'")
    .html("<a id='legend-link' style='text-decoration: none;color: gray;}' href='javascript:toggleHideLegend()'>Legend</a>");

    
function toggleHideLegend(){
    if ( hidden ) {
	rectangles.transition().duration(1000).attr('height', legend_height+50);
	legend.transition().delay(500).duration(800).style('opacity', 1);
	hidden = false;
    }else{
	legend.transition().duration(800).style('opacity', 0);
	rectangles.transition().delay(500).duration(1000).attr('height', 0);
	hidden = true;
    }
}

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

function getControllerInfo(name){
    var node = controllers.filter(function (l) {return (l.id === name ); })[0];
    return node.controller;
}