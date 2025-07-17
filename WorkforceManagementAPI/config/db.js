// config/db.js
const mysql = require('mysql2/promise');
const dotenv = require('dotenv');

dotenv.config();

const db = mysql.createPool({
    host: process.env.HOST,
    user: process.env.USER,
    password: process.env.PASSWORD,
    database: process.env.DATABASE,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    timezone: '+07:00'
});

// Test connection on startup
(async () => {
    try {
        const connection = await db.getConnection();
        console.log('✅ MySQL Connected...');
        connection.release(); // Release the connection back to the pool
    } catch (err) {
        console.error('❌ MySQL Connection Error:', err);
        process.exit(1);
    }
})();

module.exports = db;