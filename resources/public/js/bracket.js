function init(stageId) {
    var stage = new createjs.Stage(stageId);
    addMatchup(stage, 100, 100);
    stage.update();
}

function initBracketCallback(data) {

}

function initBracket() {
    $.get("/api/bracket", initBracketCallback());
}

function addMatchup(stage, x, y) {
    var square = new createjs.Shape();
    square.graphics.beginFill("DeepSkyBlue").drawRect(x, y, 100, 50);
    stage.addChild(square);
}

