class SearchTree{
  constructor(gameModel,depth){
    this.game = gameModel;
    this.legals = gameModel.legals;
    this.depth = depth;
    this.sight = {};
    for(var i=0; i<this.legals.length; i++){
      this.sight[this.legals[i]] = null;
    }
    this.leaf = false;
    this.winner = "none";
    this.winFeel = gameModel.getWinFeel();
  }

  traverse(){
    var game = this.game;
    var node = this.getNextNullNode();
    game.makeMove(parseInt(node));
    boardGraphic.setState({model:game})
    if(game.gameOver){
      this.leaf = true;
      this.winner = game.winner;
      return;
    }
    var reldepth = this.depth+1;
    this.sight[node] = new SearchTree(game,reldepth);

    setTimeout(()=>{
      this.sight[node].traverse();
    },delay);
  }

  getNextNullNode(){
    for(var i in this.sight){
      if(this.sight[i] == null){
        return i;
      }
    }
  }
}

var curGame = new GameModel();
var search;
var depth = 0;
var delay = 100;
function simulateGame(){
  search = new SearchTree(curGame,0);
  setTimeout(()=>{
      search.traverse();
  },500)
}

var boardGraphic;
function setSimBoard(board){
  boardGraphic = board;
}
simulateGame();
