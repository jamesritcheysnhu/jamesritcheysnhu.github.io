const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const cors = require('cors');
const passport = require('passport');
const mongoose = require('mongoose');
const dbConfig = require('./config/database');

// connect to database
mongoose.connect(dbConfig.database);

// notify of connection upon connecting
mongoose.connection.on('connected', () => {
	console.log('connected to database ' + dbConfig.database);
});

// notify of error if connection fails
mongoose.connection.on('error', (err) => {
	console.log('database error: ' + err);
});

// load Express
const app = express();

// load API routes
const users = require('./routes/users');
//const events = require('./routes/events');

// Express port
const port = 3000;

// CORS middleware: allow app to use different domains
app.use(cors());

// set static folder
// TODO: unused by API
app.use(express.static(path.join(__dirname, 'public')));

// body parser middleware
app.use(bodyParser.json());

// Passport middleware
app.use(passport.initialize());
//app.use(passport.session());

require('./config/passport')(passport);

// use API routes
app.use('/users', users);
//app.use('/events', events);

// index route
app.get('/', (req, res) => {
	res.send('Invalid endpoint');
});

// start server
app.listen(port, () => {
	console.log('server started on port ' + port);
});
