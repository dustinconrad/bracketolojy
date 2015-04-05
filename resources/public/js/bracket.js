var COL_WIDTH = 2;
var NUM_TEAMS = 4;

function init(container) {
    initBracket(container);
}

function initBracket(container) {
    $.get("/api/bracket", function(data) {
        drawBracketHelper(container, data);
    });
}

function treeDepth(bracket) {
    return bracket["data"]["round"];
}

function drawBracketHelper(container, bracket) {
    var depth = treeDepth(bracket);
    var rows = Math.pow(2, depth);

    //roundHeaders
    var roundHeaders = $('<div class="row" id="roundHeaders"></div>');
    for (var r = 0; r < depth; r++) {
        var roundHeader = $('<span>Round ' + (depth - r)  + '</span>')
            .addClass('col-md-' + COL_WIDTH);
        roundHeaders.append(roundHeader);
    }
    $(container).append(roundHeaders);

    for (var i = rows - 1; i >= 0; i--) {
        $(container).append('<div class="row" id="row-' + i + '"></div>');
    }
    drawBracket(container, bracket, 0, 0, rows)
}

function drawBracket(container, bracket, col, rowMin, rowMax) {
    if (bracket && bracket["data"]["round"] > 0) {
        var midRow = Math.round((rowMin + rowMax) / 2);
        drawTable(container, bracket["data"]["teams"], col, midRow);
        // upper
        drawBracket(container, bracket["upper"], col + 1, midRow, rowMax);
        // lower
        drawBracket(container, bracket["lower"], col + 1, rowMin, midRow);
    }
}

function drawTable(container, teams, col, row) {
    var table = $(makeTable(teams));
    var styled = styleTable(table);
    placeTable(container, styled, col, row);
}

function placeTable(container, table, col, row) {
    var tableDiv = $('<div/>')
        .addClass("col-md-offset-" + col*COL_WIDTH)
        .addClass("col-md-" + COL_WIDTH)
        .append(table);
    $(container).find("#row-"+ row).append(tableDiv);
}

function styleTable(table) {
    return $(table).addClass("table")
        .addClass("table-bordered")
        .addClass("table-condensed")
        .addClass("small-text");
}

function makeTable(teams) {
    var rows = [];
    for (var i = 0; i < Math.min(NUM_TEAMS, teams.length); i++) {
        var team = teams[i];
        var name = team.name;
        var avgPts = team["avg-pts"];
        var expVal = team["expected-value"];
        var prob = team["weight"];

        rows.push('<tr>');
        rows.push('<td>' + name + '</td>');
        rows.push('<td>' + avgPts.toFixed(2) + '</td>');
        rows.push('<td>' + expVal.toFixed(2) + '</td>');
        rows.push('<td>' + prob.toFixed(2) + '</td>');
        rows.push('</tr>');
    }
    var table = '<table>' +
        '<tr><th>Team</th><th>AVG</th><th>EV</th><th>Prob.</th></tr>' +
        rows.join("") +
        '</table>';
    return table;
}


