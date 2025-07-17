// attendanceRoutes.js

const express = require('express');
const router = express.Router();
const { createAttendance, getAttendance, updateAttendance, getAttendanceReport } = require('../controllers/attendanceController');
const { createAttendanceValidator, updateAttendanceValidator } = require('../middleware/attendanceMiddleware');
const { verifyToken, checkRole } = require('../middleware/authMiddleware');

router.post('/', createAttendanceValidator, verifyToken, createAttendance);
router.get('/', verifyToken, getAttendance);
router.get('/report', verifyToken, checkRole('admin', 'hr', 'dep_manager'), getAttendanceReport);
router.put('/:id', updateAttendanceValidator, verifyToken, checkRole('admin', 'hr', 'dep_manager'), updateAttendance);

module.exports = router;