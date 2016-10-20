'use strict';
var express = require('express');
var fs = require('fs');
var app = express();
var fileupload = require('fileupload').createFileUpload('/tmp').middleware;
var exec = require('exec');
var bodyParser = require('body-parser');
var child_process = require('child_process');

app.set("view engine", "ejs");
app.use(express.static(__dirname + "/public"));
console.log(__dirname + "/public");
// Use the body-parser package in our application
app.use(bodyParser.urlencoded({
  extended: true
}));
// Add headers
app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
  res.header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");
  next();
});

// Create our Express router
var router = express.Router();
app.use('/', router);



app.get("/",function(req,res){
	res.send("running..");
});
//////////Start the server
var server = app.listen(process.env.PORT || '8069', '0.0.0.0', function() {
  console.log('App listening at http://%s:%s', server.address().address,
    server.address().port);
  console.log('Press Ctrl+C to quit.');
});
