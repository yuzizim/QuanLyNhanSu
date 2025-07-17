// employeeModel.js
const db = require('../config/db.js');

class Employee {
    // Create a new employee
    static async create(employeeData) {
        try {
            const [result] = await db.execute(
                'INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, department_id, position, hire_date ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)',
                [employeeData.user_id, employeeData.employee_code, employeeData.first_name,
                employeeData.last_name, employeeData.email, employeeData.phone, employeeData.department_id, employeeData.position, employeeData.hire_date]
            );

            if (result.insertId) {
                return this.findById(result.insertId);
            }
            return null;
        } catch (error) {
            console.error('Error creating employee:', error);
            throw new Error('Failed to create employee');
        }
    }
    // // Get list of employees
    // static async getAll() {
    //     try {
    //         const [rows] = await db.execute('SELECT * FROM employees');
    //         return rows;
    //     } catch (error) {
    //         console.error('Error fetching employees:', error);
    //         throw new Error('Failed to fetch employees');
    //     }
    // }

    // // Find employee by ID
    // static async findById(id) {
    //     try {
    //         const [rows] = await db.execute('SELECT * FROM employees WHERE id = ?', [id]);
    //         return rows[0] || null;
    //     } catch (error) {
    //         console.error('Error finding employee by id:', error);
    //         throw new Error('Failed to find employee');
    //     }
    // }

    static async getAll({ page = 1, limit = 10, search = '', filter = '' }) {
        try {
            const offset = (page - 1) * limit;
            let query = `
                SELECT 
                    e.id, 
                    e.user_id, 
                    e.employee_code, 
                    e.first_name, 
                    e.last_name, 
                    e.email, 
                    e.phone, 
                    e.department_id, 
                    e.position, 
                    e.hire_date, 
                    e.status
                FROM employees e
                JOIN users u ON e.user_id = u.id
                WHERE u.role IN ('employee', 'dep_manager', 'hr') 
                AND e.status = 'active'
                AND u.is_active = TRUE
            `;
            const values = [];

            // Add search condition
            if (search) {
                query += ` AND (e.first_name LIKE ? OR e.last_name LIKE ? OR e.employee_code LIKE ? OR e.email LIKE ?)`;
                const searchTerm = `%${search}%`;
                values.push(searchTerm, searchTerm, searchTerm, searchTerm);
            }

            // Add filter condition (e.g., by department_id or position)
            if (filter) {
                query += ` AND e.position = ?`;
                values.push(filter);
            }

            // Add pagination
            query += ` LIMIT ? OFFSET ?`;
            values.push(parseInt(limit), parseInt(offset));

            const [rows] = await db.execute(query, values);

            // Get total count for pagination
            let countQuery = `
                SELECT COUNT(*) as total 
                FROM employees e
                JOIN users u ON e.user_id = u.id
                WHERE u.role IN ('employee', 'dep_manager', 'hr') 
                AND e.status = 'active'
                AND u.is_active = TRUE
            `;
            const countValues = [];
            if (search) {
                countQuery += ` AND (e.first_name LIKE ? OR e.last_name LIKE ? OR e.employee_code LIKE ? OR e.email LIKE ?)`;
                const searchTerm = `%${search}%`;
                countValues.push(searchTerm, searchTerm, searchTerm, searchTerm);
            }
            if (filter) {
                countQuery += ` AND e.position = ?`;
                countValues.push(filter);
            }

            const [[{ total }]] = await db.execute(countQuery, countValues);

            return {
                employees: rows,
                total,
                page: parseInt(page),
                limit: parseInt(limit)
            };
        } catch (error) {
            throw new Error(`Error fetching employees: ${error.message}`);
        }
    }


    static async findById(id) {
        try {
            const query = `
                SELECT e.id, e.user_id, e.employee_code, e.first_name, e.last_name, e.email, 
                       e.phone, e.department_id, e.position, e.hire_date, e.status
                FROM employees e
                WHERE e.id = ?
            `;
            const [rows] = await db.execute(query, [id]);
            return rows[0] || null;
        } catch (error) {
            throw new Error(`Error fetching employee: ${error.message}`);
        }
    }

    // Find employee by user ID
    static async findByUserId(userId) {
        try {
            const [rows] = await db.execute('SELECT * FROM employees WHERE user_id = ?', [userId]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding employee by user ID:', error);
            throw new Error('Failed to find employee');
        }
    }

    static async findByEmail(email) {
        try {
            if (!email) return null;
            const [rows] = await db.execute('SELECT * FROM employees WHERE email = ?', [email]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding employee by email:', error);
            throw new Error('Failed to find employee');
        }
    }

    static async update(id, employeeData) {
        try {
            const fields = [];
            const values = [];

            if (employeeData.first_name !== undefined && employeeData.first_name !== null) {
                fields.push('first_name = ?');
                values.push(employeeData.first_name);
            }
            if (employeeData.last_name !== undefined && employeeData.last_name !== null) {
                fields.push('last_name = ?');
                values.push(employeeData.last_name);
            }
            if (employeeData.email !== undefined && employeeData.email !== null) {
                fields.push('email = ?');
                values.push(employeeData.email);
            }
            if (employeeData.phone !== undefined && employeeData.phone !== null) {
                fields.push('phone = ?');
                values.push(employeeData.phone);
            }
            if (employeeData.position !== undefined && employeeData.position !== null) {
                fields.push('position = ?');
                values.push(employeeData.position);
            }

            if (fields.length === 0) {
                return false;
            }

            values.push(id);
            const query = `UPDATE employees SET ${fields.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, values);

            if (result.affectedRows > 0) {
                const [rows] = await db.execute('SELECT * FROM employees WHERE id = ?', [id]);
                return rows[0] || null;
            }
            return null;
        } catch (error) {
            console.error('Error updating employee:', error);
            throw new Error('Failed to update employee');
        }
    }

    // Delete employee
    static async delete(id) {
        try {
            const [result] = await db.execute('DELETE FROM employees WHERE id = ?', [id]);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error deleting employee:', error);
            throw new Error('Failed to delete employee');
        }
    }
}

module.exports = Employee;