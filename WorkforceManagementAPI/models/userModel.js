// models/userModel.js - User model and database operations
const dotenv = require('dotenv');
const db = require('../config/db.js');
const bcrypt = require('bcryptjs');

class User {

    static async getAllUsers(search = '', filter = 'all', page = 1, limit = 10) {
        try {
            let query = `
                SELECT id, username, email, role, is_active, created_at 
                FROM users 
                WHERE 1=1
            `;
            const params = [];

            // Search
            if (search) {
                query += ' AND (username LIKE ? OR email LIKE ?)';
                params.push(`%${search}%`, `%${search}%`);
            }

            // Filter
            if (filter === 'active') {
                query += ' AND is_active = ?';
                params.push(true);
            } else if (filter === 'inactive') {
                query += ' AND is_active = ?';
                params.push(false);
            } else if (filter === 'admin') {
                query += ' AND role = ?';
                params.push('admin');
            } else if (filter === 'employee') {
                query += ' AND role = ?';
                params.push('employee');
            } else if (filter === 'hr') {
                query += ' AND role = ?';
                params.push('hr');
            } else if (filter === 'dep_manager') {
                query += ' AND role = ?';
                params.push('dep_manager');
            }

            // Pagination
            const offset = (page - 1) * limit;
            query += ' LIMIT ? OFFSET ?';
            params.push(limit, offset);

            const [users] = await db.execute(query, params);

            // Get stats
            const [stats] = await db.execute(`
                SELECT 
                    COUNT(*) as total_users,
                    COUNT(CASE WHEN is_active = true THEN 1 END) as active_users,
                    COUNT(CASE WHEN is_active = false THEN 1 END) as inactive_users,
                    COUNT(CASE WHEN created_at >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) THEN 1 END) as new_users
                FROM users
            `);

            return {
                users,
                stats: stats[0]
            };
        } catch (error) {
            console.error('Error fetching users:', error);
            throw new Error('Failed to fetch users');
        }
    }

    static async createUser(username, email, password, role) {
        try {
            const hashedPassword = await bcrypt.hash(password, 10);
            const [result] = await db.execute(
                'INSERT INTO users (username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)',
                [username, email, hashedPassword, role, true]
            );
            return result.insertId;
        } catch (error) {
            console.error('Error creating user:', error);
            throw new Error('Failed to create user');
        }
    }

    static async create(userData) {
        try {
            const salt = await bcrypt.genSalt(10);
            const hashedPassword = await bcrypt.hash(userData.password, salt);

            const [result] = await db.execute(
                'INSERT INTO users (username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)',
                [userData.username, userData.email, hashedPassword, userData.role || 'employee', true]
            );

            if (result.insertId) {
                return this.findById(result.insertId);
            }
            return null;
        } catch (error) {
            console.error('Error creating user:', error);
            throw new Error('Failed to create user');
        }
    }

    static async findById(id) {
        try {
            const [rows] = await db.execute('SELECT * FROM users WHERE id = ?', [id]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding user by ID:', error);
            throw error;
        }
    }

    static async login(email, password) {
        try {
            const user = await this.findByEmail(email);
            console.log('[login] user:', user);
            if (!user) return null;

            // Defensive: If password_hash is missing, fail
            if (!user.password_hash) {
                console.error('[login] user.password_hash is missing');
                return null;
            }

            const isMatch = await bcrypt.compare(password, user.password_hash);
            console.log('[login] isMatch:', isMatch);
            if (!isMatch) return null;

            return user;
        } catch (error) {
            console.error('Error during login:', error);
            throw new Error('Login failed');
        }
    }

    static async update(id, userData) {
        try {
            const updateFields = [];
            const updateValues = [];

            if (userData.username) {
                updateFields.push('username = ?');
                updateValues.push(userData.username);
            }

            if (userData.email) {
                updateFields.push('email = ?');
                updateValues.push(userData.email);
            }

            if (userData.password) {
                const salt = await bcrypt.genSalt(10);
                const hashedPassword = await bcrypt.hash(userData.password, salt);
                updateFields.push('password_hash = ?');
                updateValues.push(hashedPassword);
            }

            if (userData.role) {
                updateFields.push('role = ?');
                updateValues.push(userData.role);
            }

            if (userData.is_active !== undefined) {
                updateFields.push('is_active = ?');
                updateValues.push(userData.is_active);
            }

            updateValues.push(id);

            if (updateFields.length === 0) {
                const user = await this.findById(id);
                if (!user) throw new Error('User not found');
                return user;
            }

            const [result] = await db.execute(
                `UPDATE users SET ${updateFields.join(', ')} WHERE id = ?`,
                updateValues
            );

            const updatedUser = await this.findById(id);
            if (!updatedUser) throw new Error('User not found after update');
            return updatedUser;
        } catch (error) {
            console.error('Error updating user:', error);
            throw error;
        }
    }

    static async activate(id) {
        try {
            const [result] = await db.execute(
                'UPDATE users SET is_active = TRUE WHERE id = ?',
                [id]
            );

            if (result.affectedRows) {
                return this.findById(id);
            }
            return null;
        } catch (error) {
            console.error('Error activating user:', error);
            throw new Error('Failed to activate user');
        }
    }

    static async saveOTP(email, otp, expiresAt) {
        await db.execute('UPDATE users SET reset_otp = ?, otp_expires_at = ? WHERE email = ?', [otp, expiresAt, email]);
    }

    static async verifyOTP(email, otp) {
        const [rows] = await db.execute(
            'SELECT * FROM users WHERE email = ? AND reset_otp = ? AND otp_expires_at > NOW()',
            [email, otp]
        );
        return rows[0];
    }

    static async updatePassword(email, newPassword) {
        const salt = await bcrypt.genSalt(10);
        const hashedPassword = await bcrypt.hash(newPassword, salt);
        await db.execute(
            'UPDATE users SET password_hash = ?, reset_otp = NULL, otp_expires_at = NULL WHERE email = ?',
            [hashedPassword, email]
        );
    }

    // Lấy profile admin (chỉ thông tin user)
    static async getAdminProfile(userId) {
        try {
            const [rows] = await db.execute(
                'SELECT id, username, email, role FROM users WHERE id = ? AND role = "admin"',
                [userId]
            );
            return rows[0] || null;
        } catch (error) {
            throw error;
        }
    }

    // Lấy profile đầy đủ cho HR, Employee, Dep_Manager
    static async getEmployeeProfile(userId) {
        try {
            const query = `
                SELECT 
                    u.id as user_id,
                    u.username,
                    u.email as user_email,
                    u.role,
                    e.id as employee_id,
                    e.employee_code,
                    e.first_name,
                    e.last_name,
                    e.email as employee_email,
                    e.phone,
                    e.position,
                    e.hire_date,
                    e.status as employee_status,
                    d.name as department_name,
                    d.code as department_code
                FROM users u
                LEFT JOIN employees e ON u.id = e.user_id
                LEFT JOIN departments d ON e.department_id = d.id
                WHERE u.id = ? AND u.role IN ('hr', 'employee', 'dep_manager')
            `;

            const [rows] = await db.execute(query, [userId]);
            return rows[0] || null;
        } catch (error) {
            throw error;
        }
    }

    static async updateUser(id, data) {
        try {
            const { username, email, role, is_active, password } = data;
            const updates = [];
            const params = [];

            if (username) {
                updates.push('username = ?');
                params.push(username);
            }
            if (email) {
                updates.push('email = ?');
                params.push(email);
            }
            if (role) {
                updates.push('role = ?');
                params.push(role);
            }
            if (is_active !== undefined && is_active !== null) {
                updates.push('is_active = ?');
                params.push(is_active);
            }
            if (password) {
                const hashedPassword = await bcrypt.hash(password, 10);
                updates.push('password_hash = ?');
                params.push(hashedPassword);
            }

            if (updates.length === 0) {
                return false; // No fields to update
            }

            params.push(id);
            const query = `UPDATE users SET ${updates.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, params);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error updating user:', error);
            throw new Error('Failed to update user');
        }
    }

    static async findByUsername(username) {
        try {
            if (!username) return null;
            const [rows] = await db.execute('SELECT * FROM users WHERE username = ?', [username]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding user by username:', error);
            throw new Error('Failed to find user');
        }
    }

    static async findByEmail(email) {
        try {
            if (!email) return null;
            console.log('[findByEmail] email:', email);
            const [rows] = await db.execute('SELECT * FROM users WHERE email = ?', [email]);
            console.log('[findByEmail] result:', rows);
            return rows[0] || null;
        } catch (error) {
            console.error('Error in findByEmail:', error);
            throw error;
        }
    }

    static async deleteUser(id) {
        try {
            await db.execute('UPDATE users SET is_active = ? WHERE id = ?', [false, id]);
        } catch (error) {
            console.error('Error deleting user:', error);
            throw new Error('Failed to delete user');
        }
    }

    // Cập nhật thông tin employee
    static async updateEmployee(userId, employeeData) {
        try {
            const { first_name, last_name, phone, position } = employeeData;
            const [result] = await db.execute(
                `UPDATE employees SET 
                    first_name = ?, 
                    last_name = ?, 
                    phone = ?, 
                    position = ?,
                    updated_at = CURRENT_TIMESTAMP 
                WHERE user_id = ?`,
                [first_name, last_name, phone, position, userId]
            );
            return result.affectedRows > 0;
        } catch (error) {
            throw error;
        }
    }
}

module.exports = User;