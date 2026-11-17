package com.zybooks.eventtracker.clientmodel;

// TODO:  at bare minimum, this acts as application variable for the JWT access token.
//        the application should save it for running duration of the app and destroyed upon close
//        it should not be stored in database

// TODO: this data should be stored encrypted in local storage keystore amd not unencrypted running
//       in the app where it could be intercepted if the client is compromised.
//       see https://medium.com/@mohammad.hasan.mahdavi81/securely-storing-jwt-tokens-in-android-with-datastore-and-manual-encryption-741b104a93d3
public class AccessToken {
}
