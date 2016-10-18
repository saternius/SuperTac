class Square{
    constructor(n){
      this.icon = "none";
      this.id = n;
      this.pointer = Square.getPointer(n);
    }

    static getPointer(n){
      switch(n){
        case 0: case 3: case 6: case 27: case 30: case 33: case 54: case 57: case 60:
          return "topLeft";
        case 1: case 4: case 7: case 28: case 31: case 34: case 55: case 58: case 61:
          return "topCenter";
        case 2: case 5: case 8: case 29: case 32: case 35: case 56: case 59: case 62:
          return "topRight";
        case 9: case 12: case 15: case 36: case 39: case 42: case 63: case 66: case 69:
          return "centerLeft";
        case 10: case 13: case 16: case 37: case 40: case 43: case 64: case 67: case 70:
          return "center";
        case 11: case 14: case 17: case 38: case 41: case 44: case 65: case 68: case 71:
          return "centerRight"; //Aka, Hillary Clinton
        case 18: case 21: case 24: case 45: case 48: case 51: case 72: case 75: case 78:
          return "bottomLeft";
        case 19: case 22: case 25: case 46: case 49: case 52: case 73: case 76: case 79:
          return "bottomCenter";
        case 20: case 23: case 26: case 47: case 50: case 53: case 74: case 77: case 80:
          return "bottomRight";
      }
    }
}

class SubBoard{
  constructor(grid,pos){
    this.winner = "none";
    this.winning = "none";
    this.bestRank = 0;
    var squares = GameModel.getSubBoardCords(pos);
    this.subGrid = {topLeft:grid[squares[0]],topCenter:grid[squares[1]],topRight:grid[squares[2]],centerLeft:grid[squares[3]],center:grid[squares[4]],centerRight:grid[squares[5]],bottomLeft:grid[squares[6]],bottomCenter:grid[squares[7]],bottomRight:grid[squares[8]]};
  }

  addGrid(spot,icon){
    this.subGrid[spot].icon = icon;
    var rank = this.getRank(spot,icon);
    if(rank>this.bestRank){
      this.bestRank = rank;
      this.winning = icon;
      if(rank == 3){
        winner = icon;
      }
    }else if(rank == this.bestRank){
      this.winning = "none";
    }
  }

  exists(spot,icon){
    return this.subGrid[spot].icon === icon;
  }

  addRank(params){
    var exists = this.exists;
    var goodness = new Set();
    for(var i=0; i<params.length;i++){
      var param = params[i];
      goodness.add(params[0]);
      goodness.add(params[1]);
      if(exists(param[0]) && exists(param[1])){
        return 3;
      }
    }
    for (let item of mySet){
      if(exists(item)){
        return 2;
      }
    }
    return 1;
  }

  getRank(spot,icon){
    var exists = this.exists;
    console.log(spot);
    var rank = 1;
    if(spot === "topLeft"){
      rank = this.addRank(["topCenter","topRight"],["center","bottomRight"],["centerLeft","bottomLeft"]);
    }else if(spot === "topCenter"){
      rank = this.addRank(["topLeft","topRight"],["center","bottomCenter"]);
    }else if(spot === "topRight"){
      rank = this.addRank(["topLeft","topCenter"],["centerRight","bottomRight"],["center","bottomRight"]);
    }else if(spot === "centerLeft"){
      rank = this.addRank(["topLeft","bottomLeft"],["center","centerRight"]);
    }else if(spot === "center"){
      rank = this.addRank(["centerLeft","centerRight"],["topCenter","bottomCenter"],["topLeft","bottomRight"],["topRight","bottomLeft"]);
    }else if(spot === "centerRight"){
      rank = this.addRank(["topRight","bottomRight"],["center","centerLeft"]);
    }else if(spot === "bottomLeft"){
      rank = this.addRank(["centerLeft","topLeft"],["bottomCenter","bottomRight"],["center","topRight"]);
    }else if(spot === "bottomCenter"){
      rank = this.addRank(["center","topCenter"],["bottomLeft","bottomRight"]);
    }else if(spot === "bottomRight"){
      rank = this.addRank(["bottomLeft","bottomCenter"],["centerRight","topRight"],["center","topLeft"]);
    }
    return rank;
  }

  static getSubBoard(n){
    switch(n){
      case 0: case 1: case 2: case 9: case 10: case 11: case 18: case 19: case 20:
        return "topLeft";
      case 3: case 4: case 5: case 12: case 13: case 14: case 21: case 22: case 23:
        return "topCenter";
      case 6: case 7: case 8: case 15: case 16: case 17: case 24: case 25: case 26:
        return "topRight";
      case 27: case 28: case 29: case 36: case 37: case 38: case 45: case 46: case 47:
        return "centerLeft";
      case 30: case 31: case 32: case 39: case 40: case 41: case 48: case 49: case 50:
        return "center";
      case 33: case 34: case 35: case 42: case 43: case 44: case 51: case 52: case 53:
        return "centerRight";
      case 54: case 55: case 56: case 63: case 64: case 65: case 72: case 73: case 74:
        return "bottomLeft";
      case 57: case 58: case 59: case 66: case 67: case 68: case 75: case 76: case 77:
        return "bottomCenter";
      case 60: case 61: case 62: case 69: case 70: case 71: case 78: case 79: case 80:
        return "bottomRight";
    }
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

      this.turn = "x";
      this.legals = [0,1,2,3,4,5,9,10,11,12,13,14,18,19,20,21,22,23,30,31,32,39,40,41,48,49,50];
    }

    gotClicked(id, board){
      var grid = this.grid[id];
      var lastPointer = this.lastPointer===undefined?SubBoard.getSubBoard(id):this.lastPointer;
      this.subBoards[lastPointer].addGrid(grid.pointer,this.turn);
      this.turn = (this.turn === "x")?"o":"x";
      this.legals = this.getLegals(grid.pointer);
      board.setState({model:this});
    }


    getLegals(pointer){
      var legals = [];
      var cands = GameModel.getSubBoardCords(pointer);
      for(var i=0; i<9;i++){
        if(this.grid[cands[i]].icon === "none"){
          legals.push(cands[i]);
        }
      }
      this.lastPointer = pointer;
      return legals;
    }

    static getSubBoardCords(pointer){
      switch(pointer){
        case "topLeft":
          return [0,1,2,9,10,11,18,19,20];
        case "topCenter":
          return[3,4,5,12,13,14,21,22,23];
        case "topRight":
          return [6,7,8,15,16,17,24,25,26];
        case "centerLeft":
          return [27,28,29,36,37,38,45,46,47];
        case "center":
          return [30,31,32,39,40,41,48,49,50];
        case "centerRight":
          return [33,34,35,42,43,44,51,52,53];
        case "bottomLeft":
          return [54,55,56,63,64,65,72,73,74];
        case "bottomCenter":
          return [57,58,59,66,67,68,75,76,77];
        case "bottomRight":
          return [60,61,62,69,70,71,78,79,80];
      }
    }
}
