const express = require('express');
const router = express.Router();
const { createTask, getAllTasks, getTaskById, updateTask, deleteTask, getTaskReport } = require('../controllers/taskController');
const { createTaskValidator, updateTaskValidator } = require('../middleware/taskMiddelware');
const { verifyToken, checkRole } = require('../middleware/authMiddleware');

// Create a new task
router.post('/', verifyToken, checkRole('admin', 'dep_manager'), createTaskValidator, createTask);

// Update a task
router.put('/:id', verifyToken, checkRole('admin', 'dep_manager'), updateTaskValidator, updateTask);

// Get all tasks with pagination and filtering
router.get('/', verifyToken, getAllTasks);

// Get task progress report
router.get('/report', verifyToken, checkRole('admin', 'dep_manager'), getTaskReport);

// Get task by ID
router.get('/:id', verifyToken, getTaskById);

// Delete a task
router.delete('/:id', verifyToken, checkRole('admin', 'dep_manager'), deleteTask);

module.exports = router;