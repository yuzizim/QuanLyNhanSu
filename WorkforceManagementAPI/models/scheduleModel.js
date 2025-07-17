// scheduleModel.js

const db = require('../config/db');
const moment = require('moment');

class Schedule {
    static async create(scheduleData) {
        try {
            const {
                employee_id, date, shift_start, shift_end, break_duration, status, created_by
            } = scheduleData;

            if (!employee_id || !date || !shift_start || !shift_end || !created_by) {
                throw new Error('Thiếu các trường bắt buộc');
            }

            const query = `
                INSERT INTO schedules (
                    employee_id, date, shift_start, shift_end, break_duration, status, created_by
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
            `;
            const values = [
                employee_id, date, shift_start, shift_end,
                break_duration ?? 60, status ?? 'draft', created_by
            ];
            const [result] = await db.execute(query, values);
            return result.insertId ? await this.findById(result.insertId) : null;
        } catch (error) {
            throw new Error(`Không thể tạo lịch làm việc: ${error.message}`);
        }
    }

    static async findById(id) {
        try {
            const [rows] = await db.execute(`
                SELECT s.*, 
                       CONCAT(e.first_name, ' ', e.last_name) as employee_name,
                       d.name as department_name,
                       CONCAT(c.first_name, ' ', c.last_name) as creator_name
                FROM schedules s
                LEFT JOIN employees e ON s.employee_id = e.id
                LEFT JOIN departments d ON e.department_id = d.id
                LEFT JOIN employees c ON s.created_by = c.id
                WHERE s.id = ?
            `, [id]);
            return rows[0] || null;
        } catch (error) {
            throw new Error('Không thể tìm lịch làm việc');
        }
    }

    static async update(id, scheduleData) {
        try {
            const { employee_id, date, shift_start, shift_end, break_duration, status } = scheduleData;

            const fields = [];
            const values = [];

            if (employee_id) {
                fields.push('employee_id = ?');
                values.push(employee_id);
            }
            if (date) {
                fields.push('date = ?');
                values.push(moment(date).format('YYYY-MM-DD'));
            }
            if (shift_start) {
                fields.push('shift_start = ?');
                values.push(shift_start);
            }
            if (shift_end) {
                fields.push('shift_end = ?');
                values.push(shift_end);
            }
            if (break_duration !== undefined) {
                fields.push('break_duration = ?');
                values.push(break_duration);
            }
            if (status) {
                if (!['draft', 'confirmed', 'completed'].includes(status)) {
                    throw new Error('Trạng thái không hợp lệ');
                }
                fields.push('status = ?');
                values.push(status);
            }

            if (fields.length === 0) {
                throw new Error('Không có trường nào để cập nhật');
            }

            values.push(id);
            const query = `UPDATE schedules SET ${fields.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, values);

            return result.affectedRows > 0;
        } catch (error) {
            throw new Error(`Không thể cập nhật lịch làm việc: ${error.message}`);
        }
    }

    static async delete(id) {
        try {
            const [result] = await db.execute('DELETE FROM schedules WHERE id = ?', [id]);
            return result.affectedRows > 0;
        } catch (error) {
            throw new Error('Không thể xóa lịch làm việc');
        }
    }

    static async getAll({ employee_id, start_date, end_date, status, department_id, page = 1, limit = 10 }) {
        try {
            const offset = (page - 1) * limit;
            let query = `
                SELECT s.*, 
                       CONCAT(e.first_name, ' ', e.last_name) as employee_name,
                       d.name as department_name,
                       CONCAT(c.first_name, ' ', c.last_name) as creator_name
                FROM schedules s
                LEFT JOIN employees e ON s.employee_id = e.id
                LEFT JOIN departments d ON e.department_id = d.id
                LEFT JOIN employees c ON s.created_by = c.id
                WHERE 1=1
            `;
            const values = [];

            if (employee_id) {
                query += ' AND s.employee_id = ?';
                values.push(employee_id);
            }
            if (start_date) {
                query += ' AND s.date >= ?';
                values.push(start_date);
            }
            if (end_date) {
                query += ' AND s.date <= ?';
                values.push(end_date);
            }
            if (status) {
                query += ' AND s.status = ?';
                values.push(status);
            }
            if (department_id) {
                query += ' AND e.department_id = ?';
                values.push(department_id);
            }

            query += ' ORDER BY s.date DESC LIMIT ? OFFSET ?';
            values.push(limit, offset);

            const [rows] = await db.execute(query, values);
            const [countResult] = await db.execute(
                `SELECT COUNT(*) as total FROM schedules s
                 LEFT JOIN employees e ON s.employee_id = e.id
                 WHERE 1=1 ${employee_id ? 'AND s.employee_id = ?' : ''} ${start_date ? 'AND s.date >= ?' : ''} 
                 ${end_date ? 'AND s.date <= ?' : ''} ${status ? 'AND s.status = ?' : ''} 
                 ${department_id ? 'AND e.department_id = ?' : ''}`,
                values.slice(0, -2)
            );

            return { records: rows, total: countResult[0].total, page, limit };
        } catch (error) {
            throw new Error('Không thể lấy danh sách lịch làm việc');
        }
    }

    static async checkOverlap(employee_id, date, shift_start, shift_end, exclude_id = null) {
        try {
            const formattedDate = moment(date).format('YYYY-MM-DD');
            const query = `
                SELECT id FROM schedules
                WHERE employee_id = ? AND date = ?
                AND (
                    (shift_start <= ? AND shift_end >= ?)
                    OR (shift_start <= ? AND shift_end >= ?)
                    OR (shift_start >= ? AND shift_end <= ?)
                )
                ${exclude_id ? 'AND id != ?' : ''}
            `;
            const values = [
                employee_id, formattedDate, shift_end, shift_start,
                shift_end, shift_start, shift_start, shift_end
            ];
            if (exclude_id) values.push(exclude_id);

            const [rows] = await db.execute(query, values);
            return rows.length > 0;
        } catch (error) {
            throw new Error(`Không thể kiểm tra lịch trùng lặp: ${error.message}`);
        }
    }

    static async createNotification(user_id, title, message, type) {
        try {
            const query = `
                INSERT INTO notifications (user_id, title, message, type)
                VALUES (?, ?, ?, ?)
            `;
            await db.execute(query, [user_id, title, message, type]);
        } catch (error) {
            throw new Error(`Không thể tạo thông báo: ${error.message}`);
        }
    }
}

module.exports = Schedule;