// // config/firebase.js - Firebase Admin SDK setup
// const admin = require('firebase-admin');
// require('dotenv').config();
// const path = require('path');

// // Kiểm tra biến môi trường
// if (!process.env.FIREBASE_SERVICE_ACCOUNT) {
//     console.error('❌ ERROR: FIREBASE_SERVICE_ACCOUNT is not defined in .env');
//     process.exit(1);
// }

// const serviceAccountPath = path.join(__dirname, process.env.FIREBASE_SERVICE_ACCOUNT);
// console.log('Service Account Path:', serviceAccountPath);

// // Kiểm tra tệp tồn tại
// const fs = require('fs');
// if (!fs.existsSync(serviceAccountPath)) {
//     console.error(`❌ ERROR: serviceAccountKey.json not found at ${serviceAccountPath}`);
//     process.exit(1);
// }

// admin.initializeApp({
//     credential: admin.credential.cert(require(serviceAccountPath)),
//     databaseURL: `https://${process.env.FIREBASE_PROJECT_ID}.firebaseio.com`
// });

// console.log('✅ Firebase initialized successfully!');
// module.exports = admin;
