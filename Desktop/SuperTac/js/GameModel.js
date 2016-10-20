class Square{
    constructor(n){
      this.icon = "none";
      this.id = n;
      this.pointer = Positions.getPointer(n);
    }
}

class SubBoard{
  constructor(grid,pos){
    this.init();
    if(arguments.length == 1){
      this.subGrid = grid;
      return;
    }
    var squares = Positions.getSubBoardCords(pos);
    this.subGrid = {topLeft:grid[squares[0]],topCenter:grid[squares[1]],topRight:grid[squares[2]],centerLeft:grid[squares[3]],center:grid[squares[4]],centerRight:grid[squares[5]],bottomLeft:grid[squares[6]],bottomCenter:grid[squares[7]],bottomRight:grid[squares[8]]};
  }

  init(){
    this.winner = "none";
    this.winning = "none";
    this.bestRank = 0;
  }

  isFilled(){
    for(var key in this.subGrid){
      if(this.subGrid[key].icon === "none"){
        return false;
      }
    }
    return true;
  }

  addGrid(spot,icon){
    var rank = this.getRank(spot,icon);
    if(rank>this.bestRank){
      this.bestRank = rank;
      this.winning = icon;
      if(rank == 3){
        this.winner = icon;
      }
    }else if(rank == this.bestRank && this.winning!==icon){
      this.winning = "none";
    }
    return this.winner;
  }

  exists(icon,spot){
    return this.subGrid[spot].icon === icon;
  }

  addRank(icon,params){
    var goodness = new Set();
    for(var i=0; i<params.length;i++){
      var param = params[i];
      goodness.add(param[0]);
      goodness.add(param[1]);
      if(this.exists(icon,param[0]) && this.exists(icon,param[1])){
        return 3;
      }
    }
    for (let item of goodness){
      if(this.exists(icon,item)){
        return 2;
      }
    }
    return 1;
  }

  getRank(spot,icon){
    var exists = this.exists;
    var rank = 1;
    if(spot === "topLeft"){
      rank = this.addRank(icon,[["topCenter","topRight"],["center","bottomRight"],["centerLeft","bottomLeft"]]);
    }else if(spot === "topCenter"){
      rank = this.addRank(icon,[["topLeft","topRight"],["center","bottomCenter"]]);
    }else if(spot === "topRight"){
      rank = this.addRank(icon,[["topLeft","topCenter"],["centerRight","bottomRight"],["center","bottomLeft"]]);
    }else if(spot === "centerLeft"){
      rank = this.addRank(icon,[["topLeft","bottomLeft"],["center","centerRight"]]);
    }else if(spot === "center"){
      rank = this.addRank(icon,[["centerLeft","centerRight"],["topCenter","bottomCenter"],["topLeft","bottomRight"],["topRight","bottomLeft"]]);
    }else if(spot === "centerRight"){
      rank = this.addRank(icon,[["topRight","bottomRight"],["center","centerLeft"]]);
    }else if(spot === "bottomLeft"){
      rank = this.addRank(icon,[["centerLeft","topLeft"],["bottomCenter","bottomRight"],["center","topRight"]]);
    }else if(spot === "bottomCenter"){
      rank = this.addRank(icon,[["center","topCenter"],["bottomLeft","bottomRight"]]);
    }else if(spot === "bottomRight"){
      rank = this.addRank(icon,[["bottomLeft","bottomCenter"],["centerRight","topRight"],["center","topLeft"]]);
    }
    return rank;
  }
}

class GameModel{
    constructor(){
      this.grid = Array.from(Array(81).keys()).map((n)=>{return new Square(n)});
      this.subBoards = {
          topLeft:new SubBoard(this.grid,"topLeft"),
          topCenter:new SubBoard(this.grid,"topCenter"),
          topRight:new SubBoard(this.grid,"topRight"),
          centerLeft:new SubBoard(this.grid,"centerLeft"),
          center:new SubBoard(this.grid,"center"),
          centerRight:new SubBoard(this.grid,"centerRight"),
          bottomLeft:new SubBoard(this.grid,"bottomLeft"),
          bottomCenter:new SubBoard(this.grid,"bottomCenter"),
          bottomRight:new SubBoard(this.grid,"bottomRight")
        };
      this.macroBoard = new SubBoard({
        topLeft:{icon:"none"},
        topCenter:{icon:"none"},
        topRight:{icon:"none"},
        centerLeft:{icon:"none"},
        center:{icon:"none"},
        centerRight:{icon:"none"},
        bottomLeft:{icon:"none"},
        bottomCenter:{icon:"none"},
        bottomRight:{icon:"none"},
      });
      this.turn = "x";
      this.legals = [0,1,2,3,4,5,9,10,11,12,13,14,18,19,20,21,22,23,30,31,32,39,40,41,48,49,50];
      this.gameOver = false;
      this.winner = "none";
      this.selectAny = false;
    }

    makeMove(id){
      var grid = this.grid[id];
      var lastPointer = (this.lastPointer===undefined || this.selectAny)?Positions.getSubBoard(id):this.lastPointer;
      grid.icon = this.turn;
      var winner = this.subBoards[lastPointer].addGrid(grid.pointer,this.turn);
      if(winner !=="none"){
        this.macroBoard.subGrid[lastPointer].icon = winner;
        var won = this.macroBoard.addGrid(lastPointer,winner);
        if(won !== "none"){
          this.gameOver = true;
          this.winner = won;
        }
      }
      this.turn = (this.turn === "x")?"o":"x";
      this.selectAny = false;
      this.legals = this.getLegals(grid.pointer);
    }

    getLegals(pointer){
      if(this.gameOver){
        return [];
      }
      var legals = [];
      var cands = this.getSubsWithWins(pointer);
      for(var i=0; i<cands.length;i++){
        if(this.grid[cands[i]].icon === "none"){
          legals.push(cands[i]);
        }
      }
      this.lastPointer = pointer;
      return legals;
    }

   getSubsWithWins(pointer){
     if(this.subBoards[pointer].winner!=="none" || this.subBoards[pointer].isFilled()){
       this.selectAny = true;
        var full = Array.from(Array(81).keys());
        for(var key in this.subBoards){
          if(this.subBoards[key].winner !== "none"){
            var toRemove = Positions.getSubBoardCords(key);
            for(var i=0; i<toRemove.length;i++){
              var index = full.indexOf(toRemove[i]);
              full.splice(index,1);
            }
          }
        }
        return full;
      }
      return Positions.getSubBoardCords(pointer);
   }

    getWinFeel(){
      return 0;
    }
}
