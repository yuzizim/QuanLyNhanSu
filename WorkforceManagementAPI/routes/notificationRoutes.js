// notificationRoutes.js

const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notificationController');
const { authenticate } = require('../middleware/authMiddleware');
const { createNotificationValidator, updateNotificationValidator } = require('../middleware/notificationMiddleware');

router.get('/', authenticate, notificationController.getNotifications);
router.post('/', authenticate, createNotificationValidator, notificationController.createNotification);
router.put('/:id', authenticate, updateNotificationValidator, notificationController.updateNotification);

module.exports = router;