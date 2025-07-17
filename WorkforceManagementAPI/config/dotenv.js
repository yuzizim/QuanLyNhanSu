// dotenv.js
const dotenv = require('dotenv');
const path = require('path');

// Load environment variables
dotenv.config({ path: path.resolve('./.env') });

console.log("🔹 ENV Loaded:", process.env.PORT);

