'use strict';


const Language = require('@google-cloud/language');
const Storage = require('@google-cloud/storage');
var wtf_wikipedia = require("wtf_wikipedia");
var express = require('express');
var app = express();
var exec = require('exec');
var bodyParser = require('body-parser');
var child_process = require('child_process');
var needle = require('needle');

app.set("view engine", "ejs");
app.use(express.static(__dirname + "/public"));
console.log(__dirname + "/public");
// Use the body-parser package in our application
app.use(bodyParser.urlencoded({
  extended: true
}));
// Add headers
app.use(function (req, res, next) {
  //console.log("here");
  res.header("Access-Control-Allow-Origin", "*");
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
  res.header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
  next();
});

// Create our Express router
var router = express.Router();
app.use('/', router);



 
//////////////////Server requests/////////////////
app.get('/fetchWiki', function(req,res){
  var page = req.query.page;
  wtf_wikipedia.from_api(page, "en", function(markup){
    var text= wtf_wikipedia.plaintext(markup);
    res.send(text);
  })
});

app.post('/fetchEntities',function(req,res){
  var content = req.body.content;
   // Instantiates a client
  const language = Language();

  // Instantiates a Document, representing the provided text
  const document = language.document({
    // The document text, e.g. "Hello, world!"
    content: content
  });

  // Detects entities in the document
  document.detectEntities()
    .then((results) => {
      var semBank = {};
      var ents = results[1].entities;
      console.log(ents);
      for(var i=0; i<ents.length; i++){
        var ent = ents[i];
        var name = ent.name;
        var mentions = ent.mentions;
        var isProper = false;
        var semTypes = [ent.type];

        for(var m=0; m<mentions.length; m++){
          var mention = mentions[m];
          var proper = (mention.type !== "COMMON");
          if(proper){
            isProper = true;
          }else{
            semTypes.push(mention.text.content);
          }
        }

        if(isProper){
          for(var s=0; s<semTypes.length; s++){
            var semType = semTypes[s];
            if(semBank[semType] == undefined){
              semBank[semType] = [name];
            }else{
              if(semBank[semType].indexOf(name) == -1){
                semBank[semType].push(name);
              }
            }
          }
        }
      }
      console.log(semBank);
      res.send(semBank);
    });
});


/////////////////WebPages/////////////////////////
app.get('/', function(req, res) {
  res.render('index.ejs');
});


//////////Start the server
var server = app.listen('8027', '0.0.0.0', function() {
  console.log('App listening at http://%s:%s', server.address().address,
    server.address().port);
  console.log('Press Ctrl+C to quit.');
});;