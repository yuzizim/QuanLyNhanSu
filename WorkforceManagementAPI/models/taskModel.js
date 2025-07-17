// // taskModel.js
// const db = require('../config/db.js');

// class Task {
//     // Create a new task
//     static async create(taskData) {
//         try {
//             const {
//                 title,
//                 description,
//                 start_date,
//                 deadline,
//                 assignee_id,
//                 creator_id,
//                 priority = 'medium',
//                 estimated_hours,
//                 status = 'pending',
//                 progress = 0
//             } = taskData;

//             // Validate required fields
//             if (!title) throw new Error('Title is required');
//             if (!creator_id) throw new Error('Creator ID is required');
//             if (!['low', 'medium', 'high', 'urgent'].includes(priority)) {
//                 throw new Error('Invalid priority value');
//             }
//             if (!['pending', 'in_progress', 'review', 'completed', 'cancelled'].includes(status)) {
//                 throw new Error('Invalid status value');
//             }
//             if (progress < 0 || progress > 100) {
//                 throw new Error('Progress must be between 0 and 100');
//             }

//             const fields = ['title', 'creator_id', 'priority', 'status', 'progress'];
//             const values = [title, creator_id, priority, status, progress];
//             const placeholders = ['?', '?', '?', '?', '?'];

//             if (description) {
//                 fields.push('description');
//                 values.push(description);
//                 placeholders.push('?');
//             }
//             if (start_date) {
//                 fields.push('start_date');
//                 values.push(start_date);
//                 placeholders.push('?');
//             }
//             if (deadline) {
//                 fields.push('deadline');
//                 values.push(deadline);
//                 placeholders.push('?');
//             }
//             if (assignee_id) {
//                 fields.push('assignee_id');
//                 values.push(assignee_id);
//                 placeholders.push('?');
//             }
//             if (estimated_hours) {
//                 fields.push('estimated_hours');
//                 values.push(estimated_hours);
//                 placeholders.push('?');
//             }

//             const query = `INSERT INTO tasks (${fields.join(', ')}) VALUES (${placeholders.join(', ')})`;
//             const [result] = await db.execute(query, values);

//             if (result.insertId) {
//                 return this.findById(result.insertId);
//             }
//             return null;
//         } catch (error) {
//             console.error('Error creating task:', error);
//             throw new Error(`Failed to create task: ${error.message}`);
//         }
//     }

//     // Find task by ID
//     static async findById(id) {
//         try {
//             const [rows] = await db.execute('SELECT * FROM tasks WHERE id = ?', [id]);
//             return rows[0] || null;
//         } catch (error) {
//             console.error('Error finding task by ID:', error);
//             throw new Error('Failed to find task');
//         }
//     }

//     // Get all tasks
//     static async getAll() {
//         try {
//             const [rows] = await db.execute(`
//                 SELECT t.*, 
//                        e1.first_name AS assignee_first_name, 
//                        e1.last_name AS assignee_last_name,
//                        e2.first_name AS creator_first_name,
//                        e2.last_name AS creator_last_name
//                 FROM tasks t
//                 LEFT JOIN employees e1 ON t.assignee_id = e1.id
//                 LEFT JOIN employees e2 ON t.creator_id = e2.id
//                 ORDER BY t.created_at DESC
//             `);
//             return rows;
//         } catch (error) {
//             console.error('Error fetching tasks:', error);
//             throw new Error('Failed to fetch tasks');
//         }
//     }

//     // Update a task
//     static async update(id, taskData) {
//         try {
//             const {
//                 title,
//                 description,
//                 start_date,
//                 deadline,
//                 assignee_id,
//                 creator_id,
//                 priority,
//                 status,
//                 progress,
//                 estimated_hours
//             } = taskData;

//             const fields = [];
//             const values = [];

//             if (title) {
//                 fields.push('title = ?');
//                 values.push(title);
//             }
//             if (description !== undefined) {
//                 fields.push('description = ?');
//                 values.push(description);
//             }
//             if (start_date !== undefined) {
//                 fields.push('start_date = ?');
//                 values.push(start_date);
//             }
//             if (deadline !== undefined) {
//                 fields.push('deadline = ?');
//                 values.push(deadline);
//             }
//             if (assignee_id !== undefined) {
//                 fields.push('assignee_id = ?');
//                 values.push(assignee_id);
//             }
//             if (creator_id) {
//                 fields.push('creator_id = ?');
//                 values.push(creator_id);
//             }
//             if (priority) {
//                 if (!['low', 'medium', 'high', 'urgent'].includes(priority)) {
//                     throw new Error('Invalid priority value');
//                 }
//                 fields.push('priority = ?');
//                 values.push(priority);
//             }
//             if (status) {
//                 if (!['pending', 'in_progress', 'review', 'completed', 'cancelled'].includes(status)) {
//                     throw new Error('Invalid status value');
//                 }
//                 fields.push('status = ?');
//                 values.push(status);
//             }
//             if (progress !== undefined) {
//                 if (progress < 0 || progress > 100) {
//                     throw new Error('Progress must be between 0 and 100');
//                 }
//                 fields.push('progress = ?');
//                 values.push(progress);
//             }
//             if (estimated_hours !== undefined) {
//                 fields.push('estimated_hours = ?');
//                 values.push(estimated_hours);
//             }

//             if (fields.length === 0) {
//                 return false;
//             }

//             values.push(id);
//             const query = `UPDATE tasks SET ${fields.join(', ')} WHERE id = ?`;
//             const [result] = await db.execute(query, values);

//             return result.affectedRows > 0;
//         } catch (error) {
//             console.error('Error updating task:', error);
//             throw new Error(`Failed to update task: ${error.message}`);
//         }
//     }

//     // Delete a task
//     static async delete(id) {
//         try {
//             const [result] = await db.execute('DELETE FROM tasks WHERE id = ?', [id]);
//             return result.affectedRows > 0;
//         } catch (error) {
//             console.error('Error deleting task:', error);
//             throw new Error('Failed to delete task');
//         }
//     }

//     // Get task progress report
//     // Get detailed task report
//     static async getReport() {
//         try {
//             // 1. Status Breakdown
//             const [statusRows] = await db.execute(`
//             SELECT 
//                 t.status,
//                 COUNT(t.id) as task_count,
//                 AVG(t.progress) as avg_progress,
//                 COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
//             FROM tasks t
//             GROUP BY t.status
//         `);

//             // 2. Priority Breakdown
//             const [priorityRows] = await db.execute(`
//             SELECT 
//                 t.priority,
//                 COUNT(t.id) as task_count,
//                 COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
//             FROM tasks t
//             GROUP BY t.priority
//         `);

//             // 3. Department Summary
//             const [departmentRows] = await db.execute(`
//             SELECT 
//                 d.name as department_name,
//                 d.id as department_id,
//                 COUNT(t.id) as task_count,
//                 AVG(t.progress) as avg_progress,
//                 COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
//             FROM tasks t
//             LEFT JOIN employees e ON t.assignee_id = e.id
//             LEFT JOIN departments d ON e.department_id = d.id
//             GROUP BY d.id, d.name
//         `);

//             // 4. Assignee Performance
//             const [assigneeRows] = await db.execute(`
//             SELECT 
//                 e.id as employee_id,
//                 CONCAT(e.first_name, ' ', e.last_name) as employee_name,
//                 COUNT(t.id) as total_tasks,
//                 SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) as completed_tasks,
//                 AVG(t.progress) as avg_progress,
//                 COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
//             FROM tasks t
//             LEFT JOIN employees e ON t.assignee_id = e.id
//             GROUP BY e.id, e.first_name, e.last_name
//             HAVING total_tasks > 0
//         `);

//             // 5. Overdue Task Details (limit to 50 for performance)
//             const [overdueTasks] = await db.execute(`
//             SELECT 
//                 t.id,
//                 t.title,
//                 t.deadline,
//                 CONCAT(e.first_name, ' ', e.last_name) as assignee_name,
//                 d.name as department_name
//             FROM tasks t
//             LEFT JOIN employees e ON t.assignee_id = e.id
//             LEFT JOIN departments d ON e.department_id = d.id
//             WHERE t.deadline < NOW() AND t.status != 'completed'
//             ORDER BY t.deadline ASC
//             LIMIT 50
//         `);

//             // 6. Time-Based Metrics (last 30 days)
//             const [timeMetrics] = await db.execute(`
//             SELECT 
//                 COUNT(CASE WHEN t.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as tasks_created_last_30_days,
//                 COUNT(CASE WHEN t.status = 'completed' AND t.updated_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as tasks_completed_last_30_days,
//                 AVG(CASE WHEN t.status = 'completed' 
//                         THEN TIMESTAMPDIFF(HOUR, t.created_at, t.updated_at) 
//                         ELSE NULL 
//                    END) as avg_completion_hours
//             FROM tasks t
//         `);

//             // 7. Task Distribution by Creator
//             const [creatorRows] = await db.execute(`
//             SELECT 
//                 e.id as employee_id,
//                 CONCAT(e.first_name, ' ', e.last_name) as creator_name,
//                 COUNT(t.id) as tasks_created
//             FROM tasks t
//             LEFT JOIN employees e ON t.creator_id = e.id
//             GROUP BY e.id, e.first_name, e.last_name
//             HAVING tasks_created > 0
//         `);

//             // Combine all metrics into a single report
//             return {
//                 status_breakdown: statusRows,
//                 priority_breakdown: priorityRows,
//                 department_summary: departmentRows,
//                 assignee_performance: assigneeRows,
//                 overdue_tasks: overdueTasks,
//                 time_metrics: timeMetrics[0], // Single row
//                 creator_distribution: creatorRows
//             };
//         } catch (error) {
//             console.error('Error generating detailed task report:', error);
//             throw new Error('Failed to generate detailed task report');
//         }
//     }
// }

// module.exports = Task;

// models/taskModel.js
const db = require('../config/db.js');

class Task {

    //Create a new task
    static async create(taskData) {
        try {
            const {
                title,
                description,
                start_date,
                deadline,
                assignee_id,
                creator_id,
                priority = 'medium',
                estimated_hours,
                status = 'pending',
                progress = 0
            } = taskData;

            // Validate required fields
            if (!title) throw new Error('Title is required');
            if (!['low', 'medium', 'high', 'urgent'].includes(priority)) {
                throw new Error('Invalid priority value');
            }
            if (!['pending', 'in_progress', 'review', 'completed', 'cancelled'].includes(status)) {
                throw new Error('Invalid status value');
            }
            if (progress < 0 || progress > 100) {
                throw new Error('Progress must be between 0 and 100');
            }

            const fields = ['title', 'priority', 'status', 'progress'];
            const values = [title, priority, status, progress];
            const placeholders = ['?', '?', '?', '?'];

            if (description) {
                fields.push('description');
                values.push(description);
                placeholders.push('?');
            }
            if (start_date) {
                fields.push('start_date');
                values.push(start_date);
                placeholders.push('?');
            }
            if (deadline) {
                fields.push('deadline');
                values.push(deadline);
                placeholders.push('?');
            }
            if (assignee_id) {
                fields.push('assignee_id');
                values.push(assignee_id);
                placeholders.push('?');
            }
            if (creator_id) {
                fields.push('creator_id');
                values.push(creator_id);
                placeholders.push('?');
            }
            if (estimated_hours) {
                fields.push('estimated_hours');
                values.push(estimated_hours);
                placeholders.push('?');
            }

            const query = `INSERT INTO tasks (${fields.join(', ')}) VALUES (${placeholders.join(', ')})`;
            const [result] = await db.execute(query, values);

            if (result.insertId) {
                return this.findById(result.insertId);
            }
            return null;
        } catch (error) {
            console.error('Error creating task:', error);
            throw new Error(`Failed to create task: ${error.message}`);
        }
    }

    // Find task by ID
    static async findById(id) {
        try {
            const [rows] = await db.execute('SELECT * FROM tasks WHERE id = ?', [id]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding task by ID:', error);
            throw new Error('Failed to find task');
        }
    }

    // Get all tasks
    static async getAll() {
        try {
            const [rows] = await db.execute(`
                SELECT t.*, 
                       e1.first_name AS assignee_first_name, 
                       e1.last_name AS assignee_last_name,
                       e2.first_name AS creator_first_name,
                       e2.last_name AS creator_last_name
                FROM tasks t
                LEFT JOIN employees e1 ON t.assignee_id = e1.id
                LEFT JOIN employees e2 ON t.creator_id = e2.id
                ORDER BY t.created_at DESC
            `);
            return rows;
        } catch (error) {
            console.error('Error fetching tasks:', error);
            throw new Error('Failed to fetch tasks');
        }
    }
   
    // Existing findById method (giữ nguyên)
    static async findById(id) {
        try {
            const query = `
                SELECT 
                    t.*,
                    a.first_name as assignee_first_name,
                    a.last_name as assignee_last_name,
                    c.first_name as creator_first_name,
                    c.last_name as creator_last_name
                FROM tasks t
                LEFT JOIN employees a ON t.assignee_id = a.id
                LEFT JOIN employees c ON t.creator_id = c.id
                WHERE t.id = ?
            `;
            const [rows] = await db.execute(query, [id]);
            return rows[0] || null;
        } catch (error) {
            console.error('Error finding task by ID:', error);
            throw new Error(`Failed to find task: ${error.message}`);
        }
    }

    // Get all tasks with pagination and filtering
    static async getAll({ page = 1, limit = 10, status, priority, assignee_id, search } = {}) {
        try {
            const offset = (page - 1) * limit;
            let query = `
                SELECT t.*, 
                       e1.first_name AS assignee_first_name, 
                       e1.last_name AS assignee_last_name,
                       e2.first_name AS creator_first_name,
                       e2.last_name AS creator_last_name
                FROM tasks t
                LEFT JOIN employees e1 ON t.assignee_id = e1.id
                LEFT JOIN employees e2 ON t.creator_id = e2.id
                WHERE 1=1
            `;
            const values = [];

            if (status) {
                query += ' AND t.status = ?';
                values.push(status);
            }
            if (priority) {
                query += ' AND t.priority = ?';
                values.push(priority);
            }
            if (assignee_id) {
                query += ' AND t.assignee_id = ?';
                values.push(assignee_id);
            }
            if (search) {
                query += ' AND t.title LIKE ?';
                values.push(`%${search}%`);
            }

            query += ' ORDER BY t.created_at DESC LIMIT ? OFFSET ?';
            values.push(limit, offset);

            const [rows] = await db.execute(query, values);

            // Get total count for pagination
            const [countResult] = await db.execute(`
                SELECT COUNT(*) as total 
                FROM tasks t
                WHERE 1=1
                ${status ? ' AND t.status = ?' : ''}
                ${priority ? ' AND t.priority = ?' : ''}
                ${assignee_id ? ' AND t.assignee_id = ?' : ''}
                ${search ? ' AND t.title LIKE ?' : ''}
            `, values.slice(0, -2)); // Exclude limit and offset

            return {
                tasks: rows,
                total: countResult[0].total,
                page,
                limit
            };
        } catch (error) {
            console.error('Error fetching tasks:', error);
            throw new Error('Failed to fetch tasks');
        }
    }

    // Update a task
    static async update(id, taskData) {
        try {
            const {
                title,
                description,
                start_date,
                deadline,
                assignee_id,
                creator_id,
                priority,
                status,
                progress,
                estimated_hours
            } = taskData;

            const fields = [];
            const values = [];

            if (title) {
                fields.push('title = ?');
                values.push(title);
            }
            if (description !== undefined) {
                fields.push('description = ?');
                values.push(description);
            }
            if (start_date !== undefined) {
                fields.push('start_date = ?');
                values.push(start_date);
            }
            if (deadline !== undefined) {
                fields.push('deadline = ?');
                values.push(deadline);
            }
            if (assignee_id !== undefined) {
                fields.push('assignee_id = ?');
                values.push(assignee_id);
            }
            if (creator_id) {
                fields.push('creator_id = ?');
                values.push(creator_id);
            }
            if (priority) {
                if (!['low', 'medium', 'high', 'urgent'].includes(priority)) {
                    throw new Error('Invalid priority value');
                }
                fields.push('priority = ?');
                values.push(priority);
            }
            if (status) {
                if (!['pending', 'in_progress', 'review', 'completed', 'cancelled'].includes(status)) {
                    throw new Error('Invalid status value');
                }
                fields.push('status = ?');
                values.push(status);
            }
            if (progress !== undefined) {
                if (progress < 0 || progress > 100) {
                    throw new Error('Progress must be between 0 and 100');
                }
                fields.push('progress = ?');
                values.push(progress);
            }
            if (estimated_hours !== undefined) {
                fields.push('estimated_hours = ?');
                values.push(estimated_hours);
            }

            if (fields.length === 0) {
                return false;
            }

            values.push(id);
            const query = `UPDATE tasks SET ${fields.join(', ')} WHERE id = ?`;
            const [result] = await db.execute(query, values);

            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error updating task:', error);
            throw new Error(`Failed to update task: ${error.message}`);
        }
    }

    // Delete a task
    static async delete(id) {
        try {
            const [result] = await db.execute('DELETE FROM tasks WHERE id = ?', [id]);
            return result.affectedRows > 0;
        } catch (error) {
            console.error('Error deleting task:', error);
            throw new Error('Failed to delete task');
        }
    }

    // Get detailed task report
    static async getReport() {
        try {
            // 1. Status Breakdown
            const [statusRows] = await db.execute(`
                SELECT 
                    t.status,
                    COUNT(t.id) as task_count,
                    AVG(t.progress) as avg_progress,
                    COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
                FROM tasks t
                GROUP BY t.status
            `);

            // 2. Priority Breakdown
            const [priorityRows] = await db.execute(`
                SELECT 
                    t.priority,
                    COUNT(t.id) as task_count,
                    COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
                FROM tasks t
                GROUP BY t.priority
            `);

            // 3. Department Summary
            const [departmentRows] = await db.execute(`
                SELECT 
                    d.name as department_name,
                    d.id as department_id,
                    COUNT(t.id) as task_count,
                    AVG(t.progress) as avg_progress,
                    COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
                FROM tasks t
                LEFT JOIN employees e ON t.assignee_id = e.id
                LEFT JOIN departments d ON e.department_id = d.id
                GROUP BY d.id, d.name
            `);

            // 4. Assignee Performance
            const [assigneeRows] = await db.execute(`
                SELECT 
                    e.id as employee_id,
                    CONCAT(e.first_name, ' ', e.last_name) as employee_name,
                    COUNT(t.id) as total_tasks,
                    SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) as completed_tasks,
                    AVG(t.progress) as avg_progress,
                    COUNT(CASE WHEN t.deadline < NOW() AND t.status != 'completed' THEN 1 END) as overdue_tasks
                FROM tasks t
                LEFT JOIN employees e ON t.assignee_id = e.id
                GROUP BY e.id, e.first_name, e.last_name
                HAVING total_tasks > 0
            `);

            // 5. Overdue Task Details (limit to 50 for performance)
            const [overdueTasks] = await db.execute(`
                SELECT 
                    t.id,
                    t.title,
                    t.deadline,
                    CONCAT(e.first_name, ' ', e.last_name) as assignee_name,
                    d.name as department_name
                FROM tasks t
                LEFT JOIN employees e ON t.assignee_id = e.id
                LEFT JOIN departments d ON e.department_id = d.id
                WHERE t.deadline < NOW() AND t.status != 'completed'
                ORDER BY t.deadline ASC
                LIMIT 50
            `);

            // 6. Time-Based Metrics (last 30 days)
            const [timeMetrics] = await db.execute(`
                SELECT 
                    COUNT(CASE WHEN t.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as tasks_created_last_30_days,
                    COUNT(CASE WHEN t.status = 'completed' AND t.updated_at >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN 1 END) as tasks_completed_last_30_days,
                    AVG(CASE WHEN t.status = 'completed' 
                            THEN TIMESTAMPDIFF(HOUR, t.created_at, t.updated_at) 
                            ELSE NULL 
                       END) as avg_completion_hours
                FROM tasks t
            `);

            // 7. Task Distribution by Creator
            const [creatorRows] = await db.execute(`
                SELECT 
                    e.id as employee_id,
                    CONCAT(e.first_name, ' ', e.last_name) as creator_name,
                    COUNT(t.id) as tasks_created
                FROM tasks t
                LEFT JOIN employees e ON t.creator_id = e.id
                GROUP BY e.id, e.first_name, e.last_name
                HAVING tasks_created > 0
            `);

            return {
                status_breakdown: statusRows,
                priority_breakdown: priorityRows,
                department_summary: departmentRows,
                assignee_performance: assigneeRows,
                overdue_tasks: overdueTasks,
                time_metrics: timeMetrics[0],
                creator_distribution: creatorRows
            };
        } catch (error) {
            console.error('Error generating detailed task report:', error);
            throw new Error('Failed to generate detailed task report');
        }
    }
}

module.exports = Task;