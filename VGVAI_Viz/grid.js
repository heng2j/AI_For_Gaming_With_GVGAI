
const rowNum = 5;
const colNum = 5;


var stateMap =
    [
    ["wall","wall","wall","wall","wall"],
        ["wall","goal","empty","empty","wall"],
        ["wall","empty","empty","empty","wall"],
        ["wall","key","empty","nokey","wall"],
        ["wall","wall","wall","wall","wall"]
];




function getGridData(stateMap) {
    var data = new Array();
    var xpos = 1; //starting xpos and ypos at 1 so the stroke will show when we make the grid below
    var ypos = 1;
    var width = 50;
    var height = 50;
    var click = 0;

    // iterate for rows
    for (var row = 0; row < rowNum; row++) {
        data.push( new Array() );

        // iterate for cells/columns inside rows
        for (var column = 0; column < colNum; column++) {
            data[row].push({
                x: xpos,
                y: ypos,
                width: width,
                height: height,
                imgStr: stateMap[row][column],
                id: String(row) + String(column),

                click: click
            })
            // increment the x position. I.e. move it over by 50 (width variable)
            xpos += width;
        }
        // reset the x position after a row is complete
        xpos = 1;
        // increment the y position for the next row. Move it down 50 (height variable)
        ypos += height;
    }
    return data;
}

var gridData = getGridData(stateMap);
// I like to log the data to the console for quick debugging
// console.log(gridData);



var config = {
    "avatar_size" : 50
}





var grid = d3.select("#grid")
    .append("svg")
    .attr("class", "grid_svg")
    .attr("preserveAspectRatio", "xMinYMin meet")
    .attr("viewBox", "0 0 300 300")
    .attr("height", 25)
    .attr("width", 25)
    .on( 'mouseenter', function() {
        // select element in current context
        d3.select( this )
            .transition()
            .attr("height", 150)
            .attr("width", 150);
    })
    // set back
    .on( 'mouseleave', function() {
        d3.select( this )
            .transition()
            .attr("height", 25)
            .attr("width", 25);
    });

var row = grid.selectAll(".row")
    .data(gridData)
    .enter().append("g")
    .attr("class", "row");


var cell = row.selectAll(".cell")
    .data(function(d) { return d; })
    .enter().append("svg:g")
    .attr("class", "cell");



var column = cell.append("rect")
    .attr("class","square")
    .attr("x", function(d) { return d.x; })
    .attr("y", function(d) { return d.y; })
    .attr("width", function(d) { return d.width; })
    .attr("height", function(d) { return d.height; })
    .style("fill",  function(d) { return   "url(#grump_avatar" + d.id + ")" ; })
    .style("stroke", "#222")
    .text(function(d) { return d.imgStr; });


var   defs = cell.append('svg:defs');



defs.append("svg:pattern")
    .attr("id",  function(d) { return   "grump_avatar" + d.id ; })
    .attr("width", config.avatar_size)
    .attr("height", config.avatar_size)
    .attr("patternUnits", "userSpaceOnUse");

cell.append("svg:image")
    .attr("xlink:href",  function(d) { return  'images/sprites/' + d.imgStr + '.png'; } )
    .attr("width", config.avatar_size)
    .attr("height", config.avatar_size)
    .attr("x", function(d) { return d.x; })
    .attr("y", function(d) { return d.y; });



    // .on('click', function(d) {
    //     d.click ++;
    //     if ((d.click)%4 == 0 ) { d3.select(this).style("fill","#fff"); }
    //     if ((d.click)%4 == 1 ) { d3.select(this).style("fill","#2C93E8"); }
    //     if ((d.click)%4 == 2 ) { d3.select(this).style("fill","#F56C4E"); }
    //     if ((d.click)%4 == 3 ) { d3.select(this).style("fill","#838690"); }
    // })
    ;

// var cell = column.append("svg:image")
//     .attr("xlink:href", 'http://placekitten.com/g/48/48')
//
// ;
