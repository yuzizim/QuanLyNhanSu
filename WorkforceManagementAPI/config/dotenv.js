const dotenv = require('dotenv');
const path = require('path');

// Load environment variables from .env in the config folder
dotenv.config({ path: path.resolve(__dirname, '.env') });

console.log("🔹 ENV Loaded:", process.env.PORT);