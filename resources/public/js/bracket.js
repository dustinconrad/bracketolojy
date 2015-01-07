function init(container) {
    initBracket(container);
}

function drawBracket(container, bracket) {
    console.log(bracket);
    var width = $(container).width();
    console.log(width);

    $(container)
        .append('<div style="margin-left:auto;margin-right:auto;width:100px">test</div>');
}

function initBracket(container) {
    $.get("/api/bracket", function(data) {
        drawBracket(container, data);
    });
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

