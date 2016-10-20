var ai = new Worker("js/Ai.js");
var curGame = new GameModel();

var boardGraphic;
function setSimBoard(board){
  boardGraphic = board;
}


ai.onmessage = function (oEvent) {
  console.log("Worker said : " + oEvent.data);
};

function startThinking(i){
  console.log("thinking..");
  ai.postMessage({game:curGame,move:i});
}
// ai.postMessage(curGame);
