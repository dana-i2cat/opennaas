/**
 * Home page. Show the topology and allows to click on the nodes in order to see the information about it.
 *
 */
var file = "home";
var getAllocatedFlowsUrl = "ajax/getAllocatedFlows";

function runtime(node, links, controller) {
    node
        .on('mousedown', function (d) {
            console.log(d);
            if (d3.event.ctrlKey) return;
            if (d.type === "switch"){
//                getFlowTable(d.dpid);
                switchSelected(d.name, getControllerInfo(d.controller));
            }
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
        
    controller
            .on('mousedown', function (d){
                //list of switches connected with this controller
                var ctrlName = d.name;
                var listSw = new Array();
                listSw = nodes.filter(function (d) { return (d.controller === ctrlName);});
                updateCtrlInfo(listSw, controllers);
            });
}

/** Show Legend **/
legendData = [{"label": "Static link"},
            {"label": "OpenFlow Switch"},
            {"label": "OpenFlow Controller"},
            {"label": "Host"}];
var legend_x = 470,
    legend_y = 270,
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
    .attr('y', function (d, i) { return legend_y + i * 40;})
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
    .attr("x", 40)
    .attr("y", 10)
    .attr("dy", ".35em")
    .text(function (d) { return d.label; });

var rectangleData = [{ "x": legend_x - 20, "y": legend_y - 20, 
                        "rx": 20, "ry": 20, "height": 0, "width": legend_width + 30 }];
var rectangles = svg.selectAll("rect")
    .data(rectangleData)
    .enter()
    .append("rect")
    .attr("class", "legendRect")
    .attr("x", function (d) {return d.x;})
    .attr("y", function (d) {return d.y + 200;})
    .attr("rx", function (d) {return d.rx;})
    .attr("ry", function (d) {return d.ry;})
    .attr("height", function (d) {return d.height;})
    .attr("width", function (d) {return d.width;})
    .on('mousedown', function () { toggleHideLegend(); });

legendLink = svg.append("foreignObject")
    .attr("x", legend_x+100).attr("y", legend_y+140)
    .attr("class", "legendLink")
    .attr("width", 100)
    .attr("height", 60)
    .append("xhtml:body")
    .html("<a id='legend-link' href='javascript:toggleHideLegend()' height='50px'>Legend</a>");
    
function toggleHideLegend(){
    if ( hidden ) {
        rectangles.transition().duration(1000)
            .attr("transform", function(d) { return "translate(0,-190)"; })
            .attr('height', legend_height+60);
	legend.transition().delay(500).duration(800).style('opacity', 1);
	hidden = false;
    }else{
	legend.transition().duration(800).style('opacity', 0);
	rectangles.transition().delay(500).duration(1000)
            .attr("transform", function(d) { return "translate(0, 0)"; })
            .attr('height', 0);
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
        url: getAllocatedFlowsUrl+"/" + dpid,
        success: function (data) {
            $('#ajaxUpdate').html(data);
        }
    });
}

function getControllerInfo(name){
    var node = controllers.filter(function (l) {return (l.name === name ); })[0];
    return node.controller;
}
