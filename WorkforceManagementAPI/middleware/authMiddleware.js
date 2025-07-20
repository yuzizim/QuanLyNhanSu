// middleware/authMiddleware.js
// const jwt = require('jsonwebtoken');
// const User = require('../models/userModel');

// exports.verifyToken = async (req, res, next) => {
//     try {
//         let token;
//         if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
//             token = req.headers.authorization.split(' ')[1];
//         }
//         if (!token) {
//             return res.status(401).json({
//                 success: false,
//                 message: 'No access token provided, please login'
//             });
//         }
//         const decoded = jwt.verify(token, process.env.JWT_SECRET);
//         const user = await User.findById(decoded.id);
//         console.log('Retrieved user:', user); // Debug log
//         if (!user) {
//             return res.status(401).json({
//                 success: false,
//                 message: 'User does not exist'
//             });
//         }
//         req.user = user;
//         next();
//     } catch (error) {
//         return res.status(401).json({
//             success: false,
//             message: 'Invalid token'
//         });
//     }
// };

// // Check role middleware
// exports.checkRole = (...roles) => {
//     return (req, res, next) => {
//         console.log('User role:', req.user.role); // Debug log
//         console.log('Allowed roles:', roles); // Debug log
//         if (!roles.includes(req.user.role)) {
//             return res.status(403).json({
//                 success: false,
//                 message: `Role '${req.user.role}' is not authorized to perform this action`
//             });
//         }
//         next();
//     };
// };

const dotenv = require('dotenv');
const jwt = require('jsonwebtoken');
const db = require('../config/db.js');
const UserModel = require('../models/userModel');

exports.verifyToken = async (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'Không tìm thấy token' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        const [rows] = await db.execute(`
            SELECT u.*, e.id as employee_id
            FROM users u
            LEFT JOIN employees e ON u.id = e.user_id
            WHERE u.id = ?
        `, [decoded.id]);

        if (!rows[0]) {
            return res.status(401).json({ message: 'Người dùng không tồn tại' });
        }

        req.user = rows[0];
        console.log('Retrieved user:', {
            id: req.user.id,
            username: req.user.username,
            role: req.user.role,
            employee_id: req.user.employee_id
        }); // Debug: Log user details
        next();
    } catch (error) {
        console.error('Lỗi xác thực token:', error);
        return res.status(401).json({ message: 'Token không hợp lệ' });
    }
};

exports.authenticate = async (req, res, next) => {
    try {
        const token = req.headers.authorization?.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'Không tìm thấy token xác thực' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        const [rows] = await db.execute(
            'SELECT u.id, u.username, u.role, u.email, e.id as employee_id, e.department_id ' +
            'FROM users u ' +
            'LEFT JOIN employees e ON u.id = e.user_id ' +
            'WHERE u.id = ? AND u.is_active = TRUE',
            [decoded.id]
        );

        if (!rows[0]) {
            return res.status(401).json({ message: 'Người dùng không tồn tại hoặc không hoạt động' });
        }

        req.user = rows[0];
        next();
    } catch (error) {
        console.error('Lỗi xác thực:', error);
        return res.status(401).json({ message: 'Token không hợp lệ' });
    }
};

exports.checkRole = (...allowedRoles) => {
    return (req, res, next) => {
        console.log('User role:', req.user.role);
        console.log('Allowed roles:', allowedRoles);
        if (!req.user || !allowedRoles.includes(req.user.role)) {
            return res.status(403).json({ message: 'Không có quyền truy cập' });
        }
        next();
    };
};

exports.authenticateToken = async (req, res, next) => {
    try {
        const authHeader = req.headers['authorization'];
        const token = authHeader && authHeader.split(' ')[1]; // Bearer TOKEN

        if (!token) {
            return res.status(401).json({
                success: false,
                message: 'Access token required'
            });
        }

        // Verify token
        const decoded = jwt.verify(token, process.env.JWT_SECRET);

        // Lấy thông tin user từ database
        const user = await UserModel.findById(decoded.id);

        if (!user) {
            return res.status(401).json({
                success: false,
                message: 'Invalid token'
            });
        }

        if (!user.is_active) {
            return res.status(401).json({
                success: false,
                message: 'Account is not active'
            });
        }

        // Gán thông tin user vào request
        req.user = {
            id: user.id,
            username: user.username,
            email: user.email,
            role: user.role
        };

        next();

    } catch (error) {
        if (error.name === 'JsonWebTokenError') {
            return res.status(401).json({
                success: false,
                message: 'Invalid token'
            });
        }

        if (error.name === 'TokenExpiredError') {
            return res.status(401).json({
                success: false,
                message: 'Token expired'
            });
        }

        console.error('Auth middleware error:', error);
        return res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
}

// Kiểm tra quyền admin
exports.requireAdmin = (req, res, next) => {
    if (req.user.role !== 'admin') {
        return res.status(403).json({
            success: false,
            message: 'Admin access required'
        });
    }
    next();
}

// Kiểm tra quyền HR
exports.requireHR = (req, res, next) => {
    if (!['admin', 'hr'].includes(req.user.role)) {
        return res.status(403).json({
            success: false,
            message: 'HR access required'
        });
    }
    next();
}

// Kiểm tra quyền Department Manager
exports.requireDepManager = (req, res, next) => {
    if (!['admin', 'hr', 'dep_manager'].includes(req.user.role)) {
        return res.status(403).json({
            success: false,
            message: 'Department manager access required'
        });
    }
    next();
}
