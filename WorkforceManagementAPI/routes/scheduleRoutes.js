// scheduleRoutes.js

const express = require('express');
const router = express.Router();
const { createSchedule, getSchedules, updateSchedule, deleteSchedule } = require('../controllers/scheduleController');
const { createScheduleValidator, updateScheduleValidator } = require('../middleware/scheduleMiddleware');
const { verifyToken, checkRole } = require('../middleware/authMiddleware');

// Create schedule
router.post('/', createScheduleValidator, verifyToken, checkRole('admin', 'hr', 'dep_manager'), createSchedule);

// Get schedules
router.get('/', verifyToken, getSchedules);

// Update schedule
router.put('/:id', updateScheduleValidator, verifyToken, checkRole('admin', 'hr', 'dep_manager'), updateSchedule);

// Delete schedule
router.delete('/:id', verifyToken, checkRole('admin', 'hr'), deleteSchedule);

module.exports = router;