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
    var textSize = 50;
    for (i = 0; i < teams.length; i++) {
        var newY = y + padding + textSize * i;
        var team = teams[i];
        var text = new createjs.Text(team.name);
        text.x = x + padding;
        text.y = newY;
        stage.addChild(text);
    }
    var square = new createjs.Shape();
    square.graphics
        .beginStroke("black")
        .drawRect(x, y, 100, 2 * padding + teams.length * textSize);
    stage.addChild(square);
}

