const express = require('express');
const cors = require('cors');
require('./config/dotenv.js');
const db = require('./config/db.js');
const firebaseAdmin = require('./config/firebase');

const app = express();
const PORT = process.env.PORT || 9999;

// Cấu hình CORS cho emulator
app.use(cors({
    origin: '*',
    credentials: true
}));

app.use(express.json());

// Routes
app.use('/api/users', require('./routes/userRoutes'));
app.use('/api/employees', require('./routes/employeeRoutes'));
app.use('/api/departments', require('./routes/departmentRoutes.js'));
app.use('/api/tasks', require('./routes/taskRoutes.js'));
app.use('/api/attendance', require('./routes/attendanceRoutes.js'));
app.use('/api/schedules', require('./routes/scheduleRoutes.js'));
app.use('/api/evaluations', require('./routes/evaluationRoutes.js'));
app.use('/api/notifications', require('./routes/notificationRoutes.js'));
app.use('/api/performance', require('./routes/performanceRoutes.js'));
app.use('/api/stats', require('./routes/statsRoutes.js'));

app.get('/', (req, res) => {
    res.send("✅ Server is running...");
});

// QUAN TRỌNG: Listen trên tất cả network interfaces
app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Server running on http://0.0.0.0:${PORT}`);
    console.log(`📱 Emulator access: http://10.0.2.2:${PORT}`);
    console.log(`📞 Device access: http://192.168.1.94:${PORT}`);
});