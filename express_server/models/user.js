const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const config = require('../config/database');

// user schema
const UserSchema = mongoose.Schema({
	username: {
		type: String,
		required: true
	},
	password: {
		type: String,
		required: true
	}
});

const User = module.exports = mongoose.model('User', UserSchema);

module.exports.getUserById = async function(id) {
	try {
		return await User.findById(id);	
	}
	catch (err) {
		throw err;
	}
	
};

module.exports.getUserByUsername = async function(username) {
	try {
		const query = { username: username };
	
		return await User.findOne(query);	
	}
	catch (err) {
		return err;
	}
	
};

module.exports.addUser = async function(newUser) {
	try {
		const salt = await bcrypt.genSalt(10);
		const hash = await bcrypt.hash(newUser.password, salt);
			
		newUser.password = hash;

		return await newUser.save();
	}
	catch (err) {
		throw err;
	}
};

module.exports.comparePassword = async function(candidatePassword, hash) {
	try {
		const isMatch = await bcrypt.compare(candidatePassword, hash);

		return isMatch;
	}
	catch (err) {
		throw err;
	}
};
