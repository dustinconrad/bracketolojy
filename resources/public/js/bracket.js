function init(container) {
    initBracket(container);
}

function initBracket(container) {
    $.get("/api/bracket", function(data) {
        drawBracket(container, data);
    });
}

function drawBracket(container, bracket) {
    console.log(bracket);
    makeRoot(container, bracket);

}

function makeRoot(container, bracket) {
    $(container).append(makeTable(bracket.data.teams));
}

function makeTable(teams) {
    var rows = [];
    for (i = 0; i < teams.length; i++) {
        var team = teams[i];
        rows.push('<tr>');
        rows.push('<td>' + team.name + '</td>')
        rows.push('<td>' + team["avg-pts"] + '</td>')
        rows.push('</tr>');
    }
    return '<table>' +
                '<tr><th>Team</th><th>Avg. pts</th></tr>' +
                rows.join("") +
           '</table>'

}

function addMatchup(stage, teams, x, y) {
    var padding = 10;
    var textSize = 14;
    var lineLength = 0;
    for (i = 0; i < teams.length; i++) {
        var newY = y + padding + textSize * i;
        var team = teams[i];
        var text = new createjs.Text(team.name, textSize + "px Arial");
        text.x = x + padding;
        text.y = newY;
        if (text.getMeasuredWidth() > lineLength) {
            lineLength = text.getMeasuredWidth();
        }
        stage.addChild(text);
    }
    var square = new createjs.Shape();
    var width = 2 * padding + lineLength;
    var height = 2 * padding + teams.length * textSize;
    square.graphics
        .beginStroke("black")
        .drawRect(x, y, width, height);
    stage.addChild(square);
}

