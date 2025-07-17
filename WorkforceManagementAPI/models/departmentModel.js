const db = require('../config/db.js');

class Department {
    // Create a new department
    static async create(depData) {
        try {
            const { name, code, description, manager_id } = depData;
            const [result] = await db.execute(
                'INSERT INTO departments (name, code, description, manager_id) VALUES (?, ?, ?, ?)',
                [name, code, description || null, manager_id || null]
            );

            if (result.insertId) {
                return this.findById(result.insertId);
            }
            return null;
        } catch (error) {
            console.error('Error creating department:', error);
            throw new Error('Failed to create department');
        }
    }

    // Find department by ID
    static async findById(id) {
        try {
            const [rows] = await db.execute(
                'SELECT * FROM departments WHERE id = ?',
                [id]
            );
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding department by ID:', error);
            throw new Error('Failed to find department');
        }
    }

    // Find department by code
    static async findByCode(code) {
        try {
            const [rows] = await db.execute(
                'SELECT * FROM departments WHERE code = ?',
                [code]
            );
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding department by code:', error);
            throw new Error('Failed to find department');
        }
    }

    // Get all departments
    static async findAll() {
        try {
            const [rows] = await db.execute('SELECT * FROM departments');
            return rows;
        } catch (error) {
            console.error('Error fetching all departments:', error);
            throw new Error('Failed to fetch departments');
        }
    }

    // Update a department
    static async update(id, depData) {
        try {
            const { name, code, description, manager_id } = depData;
            const [result] = await db.execute(
                'UPDATE departments SET name = ?, code = ?, description = ?, manager_id = ? WHERE id = ?',
                [name, code, description || null, manager_id || null, id]
            );

            if (result.affectedRows > 0) {
                return this.findById(id);
            }
            return null;
        } catch (error) {
            console.error('Error updating department:', error);
            throw new Error('Failed to update department');
        }
    }

    // Delete a department
    static async delete(id) {
        try {
            const [result] = await db.execute('DELETE FROM departments WHERE id = ?', [id]);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error deleting department:', error);
            throw new Error('Failed to delete department');
        }
    }


    static async getAllDepartments(search = '', page = 1, limit = 10) {
        try {
            let query = `
                SELECT d.id, d.name, d.code, d.description, d.manager_id, 
                       e.first_name AS manager_first_name, e.last_name AS manager_last_name
                FROM departments d
                LEFT JOIN employees e ON d.manager_id = e.id
                WHERE 1=1
            `;
            const params = [];

            if (search) {
                query += ' AND (d.name LIKE ? OR d.code LIKE ?)';
                params.push(`%${search}%`, `%${search}%`);
            }

            const offset = (page - 1) * limit;
            query += ' LIMIT ? OFFSET ?';
            params.push(limit, offset);

            const [departments] = await db.execute(query, params);

            const [stats] = await db.execute(`
                SELECT 
                    COUNT(*) as total_departments,
                    COUNT(d.manager_id) as departments_with_managers
                FROM departments d
            `);

            return {
                departments,
                stats: stats[0]
            };
        } catch (error) {
            console.error('Error fetching departments:', error);
            throw new Error('Failed to fetch departments');
        }
    }

    static async getDepartmentById(id) {
        try {
            const [rows] = await db.execute(`
                SELECT d.id, d.name, d.code, d.description, d.manager_id,
                       e.first_name AS manager_first_name, e.last_name AS manager_last_name
                FROM departments d
                LEFT JOIN employees e ON d.manager_id = e.id
                WHERE d.id = ?
            `, [id]);
            return rows[0];
        } catch (error) {
            console.error('Error fetching department:', error);
            throw new Error('Failed to fetch department');
        }
    }

    static async createDepartment(department) {
        try {
            const { name, code, description, manager_id } = department;
            const [result] = await db.execute(`
                INSERT INTO departments (name, code, description, manager_id)
                VALUES (?, ?, ?, ?)
            `, [name, code, description || null, manager_id || null]);
            return result.insertId;
        } catch (error) {
            console.error('Error creating department:', error);
            throw new Error('Failed to create department');
        }
    }

    static async updateDepartment(id, updates) {
        try {
            const { name, code, description, manager_id } = updates;
            const [result] = await db.execute(`
                UPDATE departments 
                SET name = ?, code = ?, description = ?, manager_id = ?
                WHERE id = ?
            `, [name, code, description || null, manager_id || null, id]);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error updating department:', error);
            throw new Error('Failed to update department');
        }
    }

    static async deleteDepartment(id) {
        try {
            const [result] = await db.execute(`
                DELETE FROM departments WHERE id = ?
            `, [id]);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error deleting department:', error);
            throw new Error('Failed to delete department');
        }
    }

    static async getEligibleManagers() {
        try {
            const [rows] = await db.execute(`
                SELECT e.id, e.first_name, e.last_name
                FROM employees e
                JOIN users u ON e.user_id = u.id
                WHERE u.role IN ('hr', 'dep_manager', 'admin')
            `);
            return rows;
        } catch (error) {
            console.error('Error fetching eligible managers:', error);
            throw new Error('Failed to fetch eligible managers');
        }
    }
}

module.exports = Department;