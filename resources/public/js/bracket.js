function init(container) {
    initBracket(container);
}

function initBracket(container) {
    $.get("/api/bracket", function(data) {
        drawBracket(container, data);
    });
}

function drawBracket(container, bracket) {
    makeRoot(container, bracket);
}

function makeRoot(container, bracket) {
    var root = $(makeTable(bracket.data.teams));
    root.addClass("table")
        .addClass("table-bordered")
        .addClass("table-centered")
        .addClass("table-condensed")
        .addClass("small-text");
    $(container).append(root);
}

function makeTable(teams) {
    var rows = [];
    for (var i = 0; i < Math.min(4, teams.length); i++) {
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
    return '<table>' +
                '<tr><th>Team</th><th>AVG</th><th>EV</th><th>Prob.</th></tr>' +
                rows.join("") +
           '</table>';
}



