// notificationController.js

const Notification = require('../models/notificationModel');
const User = require('../models/userModel');

exports.getNotifications = async (req, res) => {
    try {
        const { type, is_read, page, limit } = req.query;
        const user = req.user;

        let filters = { page: parseInt(page) || 1, limit: parseInt(limit) || 10 };
        filters.user_id = user.id;
        if (type) filters.type = type;
        if (is_read !== undefined) filters.is_read = is_read === 'true';

        const result = await Notification.getAll(filters);
        return res.status(200).json({
            message: 'Lấy danh sách thông báo thành công',
            ...result
        });
    } catch (error) {
        console.error('Lỗi khi lấy danh sách thông báo:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.createNotification = async (req, res) => {
    try {
        const { user_id, title, message, type } = req.body;
        const user = req.user;

        if (!['admin', 'hr', 'dep_manager'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền tạo thông báo' });
        }

        const targetUser = await User.findById(user_id);
        if (!targetUser) {
            return res.status(400).json({ message: 'Người dùng không tồn tại' });
        }

        if (user.role === 'dep_manager') {
            const [rows] = await db.execute(
                'SELECT department_id FROM employees WHERE user_id = ?',
                [user_id]
            );
            if (rows[0]?.department_id !== user.department_id) {
                return res.status(403).json({ message: 'Không có quyền gửi thông báo cho người dùng ngoài phòng ban' });
            }
        }

        const notificationData = { user_id, title, message, type };
        const newNotification = await Notification.create(notificationData);

        if (!newNotification) {
            return res.status(400).json({ message: 'Không thể tạo thông báo' });
        }

        return res.status(201).json({ message: 'Tạo thông báo thành công', notification: newNotification });
    } catch (error) {
        console.error('Lỗi khi tạo thông báo:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};

exports.updateNotification = async (req, res) => {
    try {
        const { id } = req.params;
        const { title, message, type, is_read } = req.body;
        const user = req.user;

        const notification = await Notification.findById(id);
        if (!notification) {
            return res.status(404).json({ message: 'Không tìm thấy thông báo' });
        }

        if (notification.user_id !== user.id && !['admin', 'hr'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền cập nhật thông báo này' });
        }

        const updateData = { title, message, type, is_read };
        const isUpdated = await Notification.update(id, updateData);

        if (!isUpdated) {
            return res.status(400).json({ message: 'Không có thay đổi hoặc cập nhật thất bại' });
        }

        const updatedNotification = await Notification.findById(id);
        return res.status(200).json({ message: 'Cập nhật thông báo thành công', notification: updatedNotification });
    } catch (error) {
        console.error('Lỗi khi cập nhật thông báo:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};