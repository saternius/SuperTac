importScripts('Positions.js');
importScripts('GameModel.js');
class SearchTree{
  constructor(gameModel,playerMove){
    this.game = new GameModel(gameModel);
    this.game.makeMove(parseInt(playerMove));
    this.legals = this.game.legals;
    this.winFeel = this.game.getWinFeel();
    this.move = playerMove;
  }

  traverse(maxDepth, min){
    var game = this.game;
    if(maxDepth==0 || game.gameOver){
      return {feel: this.winFeel, move: this.move, searchTree:this.game};
    }
    var legals = this.legals;
    var bestFeel = min?99999:-99999;
    var bestMove = -1;
    var searchTree = {};
    for(var i=0; i<legals.length;i++){
      var bestSub = new SearchTree(game,legals[i]).traverse(maxDepth-1,!min);
      searchTree[legals[i]] = bestSub.searchTree;
      if(min){
        if(bestSub.feel<bestFeel){
          bestFeel = bestSub.feel;
          bestMove = legals[i];
        }
      }else{
        if(bestSub.feel>bestFeel){
          bestFeel = bestSub.feel;
          bestMove = legals[i];
        }
      }
    }
    var dir = min?"min":"max";
    return {feel:bestFeel, move:bestMove, searchTree:{dir:dir, pick:bestFeel, move:bestMove, tree:searchTree}};
  }
}


var thinkTime = 2000;
var think = false;
var maxDepth = 5;
onmessage = function(msg){
  var game = msg.data.game;
  var playerMove = msg.data.move;
  think = true;
  var search = new SearchTree(game,playerMove,0);
  var bestMove = search.traverse(maxDepth,true);
  console.log(bestMove);
  postMessage(bestMove.move);
}
