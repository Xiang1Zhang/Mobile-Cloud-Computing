const admin = require('firebase-admin');
var serviceAccount = require("../mcc-fall-2019-g25-firebase-adminsdk-gl5cy-4a4ef7d329.json");

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://mcc-fall-2019-g25.firebaseio.com"
});

const db = admin.firestore();

module.exports = { admin, db };
