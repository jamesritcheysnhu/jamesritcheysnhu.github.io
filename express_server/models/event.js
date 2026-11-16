const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const config = require('../config/database');

// event schema
const EventSchema = mongoose.Schema(
	{
		ownerId: {  // JWT user id
			type: String,
			required: true
		},
		title: {
			type: String,
			required: true
		},
		location: {
			type: String
		},
	    datetime: {  // Unix timestamp in ms
			type: BigInt,
			required: true,
			default: Date.now()
	    },
		description: {
			type: String
		}
	},
	{
		// datetime BigInt to String JSON conversion
	    toJSON: {
			transform(doc, ret) {
				if (typeof ret.datetime === 'bigint') {
					ret.datetime = ret.datetime.toString();
				}

				return ret;
			}
		}
	}
);

const Event = module.exports = mongoose.model('Event', EventSchema);

// getEventById: single Event lookup
module.exports.getEventById = async function(id) {
	try {
		return await Event.findById(id);	
	}
	catch (err) {
		throw err;
	}
	
};

// getEventByTitle: basic title search
module.exports.getEventByTitle = async function(title) {
	try {
		const query = { title: title };
	
		return await Event.findOne(query);	
	}
	catch (err) {
		return err;
	}
	
};

// addEvent: saves the event to the database
module.exports.addEvent = async function(newEvent) {
	try {
		return await newEvent.save();
	}
	catch (err) {
		throw err;
	}
};
