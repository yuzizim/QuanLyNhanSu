// performanceRoutes.js

const express = require('express');
const router = express.Router();
const performanceController = require('../controllers/performanceController');
const { authenticate } = require('../middleware/authMiddleware');

router.get('/report', authenticate, performanceController.getPerformanceReport);

module.exports = router;