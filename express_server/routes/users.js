const express = require('express');
const router = express.Router();
const passport = require('passport');
const jwt = require('jsonwebtoken');

const User = require('../models/user');
const dbConfig = require('../config/database');

// /users/register
router.post('/register', async (req, res, next) => {
	try {
		let newUser = new User({
			name: req.body.name,
			email: req.body.email,
			username: req.body.username,
			password: req.body.password
		});

		const savedUser = await User.addUser(newUser);

		return res.json({
			success: true,
			msg: 'user registered',
			user: savedUser
		});
	}
	catch (err) {
		console.error('Mongoose registration error details: ', err);

		return res.status(500).json({
			success: false,
			msg: 'failed to register',
			error: err.message
		});
	}
});

// /users/authenticate - NOTE: sessions must be disabled if using token
// NOTE: client must consume the id_token,
//       whereas the client sends the access_token to the server
router.post('/authenticate', async (req, res, next) => {
	const username = req.body.username;
	const password = req.body.password;

	try {
		// get user
		const user = await User.getUserByUsername(username);

		if (!user) {
			return res.json({
				success: false,
				msg: 'user not found'
			});
		}

		// validate password
		const isMatch = await User.comparePassword(password, user.password);
		
		if (!isMatch) {
			return res.json({
				success: false,
				msg: 'wrong password'
			});
		}

		/** generate token w/ 1 week expiration
		 * use user.toJSON() or user.toObject() if user is a Mongoose document
		 * passing a raw Mongoose document directly to jwt.sign() can sometimes append internal Mongoose metadata, leading to massive tokens or errors
		 */
		// TODO: though cookies have their own pitfalls, localStorage is susceptible to XSS via raw forms or compromised 3rd party CDN JS.
		//       see https://stackoverflow.com/questions/44133536/is-it-safe-to-store-a-jwt-in-localstorage-with-reactjs for mitigations for either
		const token = jwt.sign(user.toJSON ? user.toJSON() : user, dbConfig.secret, {
			expiresIn: 604800
		});

		// return user data for client
		return res.json({
			success: true,
			token: 'Bearer ' + token,
			user: {
				id: user._id,
				username: user.username
			}
		});
	}
	catch (err) {
		console.error('Authentication error details:', err);
        return res.status(500).json({
            success: false,
            msg: 'An error occurred during authentication',
            error: err.message
        });
	}
});

// /users/profile - protected by token authentication; the token arrives at the server via authorization header
// TODO: this route is example for an Angular client and should be adapted for use with Android if being used or otherwise removed
//       it may serve as an example for other protected routes used in the API, e.g. Events
// NOTE: REMOVE COMMENT --> (inserted into header upon making a client-side post request to users/authenticate at page users/login;
//                      --> Login.onLoginSubmit is called by the form submission which in turn calls and subscribes to Auth.authenticateUser,
//                      --> which in response stores the token in local client storage)
router.get('/profile', passport.authenticate('jwt', { session: false }), (req, res, next) => {
	res.json({
		user: req.user
	});
});

module.exports = router;
