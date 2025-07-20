// routes/dashboardRoutes.js
const express = require('express');
const router = express.Router();
const dashboardController = require('../controllers/dashboardController');
const { authenticate } = require('../middleware/authMiddleware');

// Route: GET /api/dashboard/dep-manager
router.get('/dep-manager', authenticate, dashboardController.getDepManagerDashboard);

module.exports = router;