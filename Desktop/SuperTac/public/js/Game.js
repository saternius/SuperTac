var ai = new Worker("js/Ai.js");
var curGame = new GameModel();

var boardGraphic;
function setSimBoard(board){
  boardGraphic = board;
}

ai.onmessage = function (oEvent) {
  var aiMove = parseInt(oEvent.data);
  curGame.makeMove(aiMove);
  boardGraphic.setState({model:curGame});
};

function startThinking(i){
  console.log("thinking..");
  ai.postMessage({game:Object.assign({"ai":true}, curGame) ,move:i});
}
// ai.postMessage(curGame);
