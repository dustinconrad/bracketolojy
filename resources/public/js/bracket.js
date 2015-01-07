function init(stageId) {
    var stage = new createjs.Stage(stageId);
    initBracket(stage);
}

function drawBracket(stage, bracket) {
    console.log(bracket);
    addMatchup(stage, bracket.data.teams, 100, 100);
    stage.update();
}

function initBracket(stage) {
    $.get("/api/bracket", function(data) {
        drawBracket(stage, data);
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

