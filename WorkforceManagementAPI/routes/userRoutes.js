// routes/userRoutes.js - User routes
const dotenv = require('dotenv');
const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');
const authMiddleware = require('../middleware/authMiddleware');

// Public routes
router.post('/', userController.registerUser);
router.post('/login', userController.loginUser);

// Lấy danh sách tất cả người dùng (chỉ admin và hr)
router.get('/', authMiddleware.verifyToken, authMiddleware.checkRole('admin', 'hr'), userController.getUsers);

// Protected routes
router.get('/me', authMiddleware.verifyToken, userController.getCurrentUser);
// Lấy profile của user hiện tại
router.get('/profile', authMiddleware.authenticateToken, userController.getProfile);

// Cập nhật profile của user hiện tại
router.put('/profile', authMiddleware.authenticateToken, userController.updateProfile);

// Lấy profile của user khác (chỉ admin và hr)
router.get('/profile/:userId', authMiddleware.authenticateToken, userController.getUserProfile);

router.get('/:id', authMiddleware.verifyToken, userController.getUserById);
router.put('/:id', authMiddleware.verifyToken, authMiddleware.checkRole('admin'), userController.updateUser);
router.delete('/:id', authMiddleware.verifyToken, authMiddleware.checkRole('admin'), userController.deleteUser);
router.post('/create', authMiddleware.authenticateToken, authMiddleware.checkRole('admin'), userController.createUser);

router.post('/request-password-reset', userController.requestPasswordReset);
router.post('/reset-password', userController.resetPassword);

router.post('/google', userController.googleLogin);

module.exports = router;