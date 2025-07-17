// notificationModel.js

const db = require('../config/db');

class Notification {
    static async create(notificationData) {
        try {
            const { user_id, title, message, type } = notificationData;
            const query = `
                INSERT INTO notifications (user_id, title, message, type, is_read)
                VALUES (?, ?, ?, ?, ?)
            `;
            const values = [user_id, title, message, type, 0];
            console.log('Creating notification with values:', values);
            const [result] = await db.execute(query, values);
            return result.insertId ? await this.findById(result.insertId) : null;
        } catch (error) {
            throw new Error(`Không thể tạo thông báo: ${error.message}`);
        }
    }

    static async findById(id) {
        try {
            const [rows] = await db.execute(`
                SELECT n.*, u.username
                FROM notifications n
                LEFT JOIN users u ON n.user_id = u.id
                WHERE n.id = ?
            `, [id]);
            return rows[0] || null;
        } catch (error) {
            throw new Error(`Không thể tìm thông báo: ${error.message}`);
        }
    }

    static async getAll({ user_id, type, is_read, page = 1, limit = 10 }) {
        try {
            const offset = (page - 1) * limit;
            let query = `
                SELECT n.*, u.username
                FROM notifications n
                LEFT JOIN users u ON n.user_id = u.id
                WHERE 1=1
            `;
            const values = [];

            if (user_id) {
                query += ' AND n.user_id = ?';
                values.push(user_id);
            }
            if (type) {
                query += ' AND n.type = ?';
                values.push(type);
            }
            if (is_read !== undefined) {
                query += ' AND n.is_read = ?';
                values.push(is_read ? 1 : 0);
            }

            query += ' ORDER BY n.created_at DESC LIMIT ? OFFSET ?';
            values.push(limit, offset);

            const [rows] = await db.execute(query, values);

            const [countResult] = await db.execute(`
                SELECT COUNT(*) as total
                FROM notifications n
                WHERE 1=1
                ${user_id ? 'AND n.user_id = ?' : ''}
                ${type ? 'AND n.type = ?' : ''}
                ${is_read !== undefined ? 'AND n.is_read = ?' : ''}
            `, values.slice(0, -2));

            return {
                records: rows,
                total: countResult[0].total,
                page,
                limit
            };
        } catch (error) {
            throw new Error(`Không thể lấy danh sách thông báo: ${error.message}`);
        }
    }

    static async update(id, notificationData) {
        try {
            const { title, message, type, is_read } = notificationData;
            const fields = [];
            const values = [];

            if (title) {
                fields.push('title = ?');
                values.push(title);
            }
            if (message) {
                fields.push('message = ?');
                values.push(message);
            }
            if (type) {
                fields.push('type = ?');
                values.push(type);
            }
            if (is_read !== undefined) {
                fields.push('is_read = ?, read_at = ?');
                values.push(is_read ? 1 : 0, is_read ? new Date() : null);
            }

            if (fields.length === 0) {
                throw new Error('Không có trường nào để cập nhật');
            }

            values.push(id);
            console.log('Updating notification with values:', values);
            const query = `UPDATE notifications SET ${fields.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, values);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Không thể cập nhật thông báo: ${error.message}`);
        }
    }
}

module.exports = Notification;