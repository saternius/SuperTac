class SearchTree{
  constructor(legals, depth){
    this.legals = legals;
    this.depth = depth;
    this.sight = {};
    for(var i=0; i<legals.length; i++){
      this.sight[legals[i]] = null;
    }
  }

  traverse(turn,depth){
    var node = this.getNextNullNode();
    curGame.makeMove(parseInt(node));
    boardGraphic.setState({model:curGame})
    // console.log("traversing..");
    // console.log(this.sight);
    // console.log(node);
    this.sight[node] = new SearchTree(curGame.legals,depth);

    if(turn === "x"){
      oSearch = this.sight[node];
    }else{
      xSearch = this.sight[node];
    }
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
var xSearch;
var oSearch;
var depth = 0;
var delay = 100;
function simulateGame(){
// while(!curGame.gameOver){
    setTimeout(()=>{
        nextMove(curGame.turn);
    },500)

// }
}

function nextMove(turn){
  console.log("depth: "+depth)
  if(depth == 0){
    xSearch = new SearchTree(curGame.legals);
  }else if(depth == 1){
    oSearch = new SearchTree(curGame.legals);
  }

  var searchTree = turn==="x"?xSearch:oSearch;
  searchTree.traverse(turn,depth);
  depth++;

  if(depth<44){//!curGame.gameOver){
    setTimeout(()=>{
        nextMove(curGame.turn);
    },delay)
  }
}

var boardGraphic;
function setSimBoard(board){
  boardGraphic = board;
}
simulateGame();
