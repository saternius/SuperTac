importScripts('Positions.js');
importScripts('GameModel.js');
class SearchTree{
  constructor(gameModel,playerMove,depth){
    this.game = new GameModel(gameModel);
    this.game.makeMove(parseInt(playerMove));
    this.legals = this.game.legals;
    this.depth = depth;
    this.winFeel = this.game.getWinFeel();
  }

  traverse(){
    if(!think || game.gameOver){
      return this.winFeel;
    }

    var game = this.game;
    var legals = this.legals;
    var depth = this.depth+1;
    var queue = [];

    var bestFeel = -99999;
    var bestMove = -1;
    for(var i=0; i<legals.length;i++){
      var subTree = new SearchTree(game,legals[i],depth)
      queue.push(subTree);
      if(subTree.winFeel>bestFeel){
        bestFeel = subTree.winFeel;
        bestMove = legals[i];
      }
    }
    return bestMove;
  }
}

var search;
var depth = 0;
var delay = 5;
function simulateGame(){
  search = new SearchTree(curGame,0);
  setTimeout(()=>{
      search.traverse();
  },500)
}

var thinkTime = 2000;
var think = false;
onmessage = function(msg){
  var game = msg.data.game;
  var playerMove = msg.data.move;
  var think = true;
  var search = new SearchTree(game,playerMove,0);
  setTimeout(()=>{
    think = false;
  },thinkTime);
  var bestMove = search.traverse();
  postMessage(bestMove);
}
//  postMessage("Well hello there");
//simulateGame();
