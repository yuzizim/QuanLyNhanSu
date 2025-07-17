// performanceController.js

const db = require('../config/db');
const moment = require('moment-timezone');

exports.getPerformanceReport = async (req, res) => {
    try {
        const { employee_id, department_id, start_date, end_date } = req.query;
        const user = req.user;

        let filters = {};
        if (employee_id) filters.employee_id = parseInt(employee_id);
        if (department_id) filters.department_id = parseInt(department_id);
        if (start_date) filters.start_date = start_date;
        if (end_date) filters.end_date = end_date;

        if (user.role === 'dep_manager') {
            filters.department_id = user.department_id;
        } else if (!['admin', 'hr'].includes(user.role)) {
            return res.status(403).json({ message: 'Không có quyền xem báo cáo hiệu suất' });
        }

        let query = `
            SELECT 
                e.id as employee_id,
                CONCAT(e.first_name, ' ', e.last_name) as employee_name,
                d.name as department_name,
                AVG(ev.overall_score) as avg_overall_score,
                AVG(ev.performance_score) as avg_performance_score,
                AVG(ev.kpi_score) as avg_kpi_score,
                COUNT(ev.id) as evaluation_count,
                SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) as completed_tasks,
                AVG(t.progress) as avg_task_progress
            FROM employees e
            LEFT JOIN evaluations ev ON e.id = ev.employee_id
            LEFT JOIN tasks t ON e.id = t.assignee_id
            LEFT JOIN departments d ON e.department_id = d.id
            WHERE 1=1
        `;
        const values = [];

        if (filters.employee_id) {
            query += ' AND e.id = ?';
            values.push(filters.employee_id);
        }
        if (filters.department_id) {
            query += ' AND e.department_id = ?';
            values.push(filters.department_id);
        }
        if (filters.start_date) {
            query += ' AND (ev.evaluation_date >= ? OR t.deadline >= ?)';
            values.push(moment.utc(filters.start_date).format('YYYY-MM-DD'), moment.utc(filters.start_date).format('YYYY-MM-DD'));
        }
        if (filters.end_date) {
            query += ' AND (ev.evaluation_date <= ? OR t.deadline <= ?)';
            values.push(moment.utc(filters.end_date).format('YYYY-MM-DD'), moment.utc(filters.end_date).format('YYYY-MM-DD'));
        }

        query += ' GROUP BY e.id, e.first_name, e.last_name, d.name HAVING evaluation_count > 0 OR completed_tasks > 0';
        const [employeeBreakdown] = await db.execute(query, values);

        query = `
            SELECT 
                d.id as department_id,
                d.name as department_name,
                AVG(ev.overall_score) as avg_overall_score,
                AVG(ev.performance_score) as avg_performance_score,
                AVG(ev.kpi_score) as avg_kpi_score,
                COUNT(ev.id) as evaluation_count,
                SUM(CASE WHEN t.status = 'completed' THEN 1 ELSE 0 END) as completed_tasks
            FROM departments d
            LEFT JOIN employees e ON d.id = e.department_id
            LEFT JOIN evaluations ev ON e.id = ev.employee_id
            LEFT JOIN tasks t ON e.id = t.assignee_id
            WHERE 1=1
        `;
        const departmentValues = [];

        if (filters.department_id) {
            query += ' AND d.id = ?';
            departmentValues.push(filters.department_id);
        }
        if (filters.start_date) {
            query += ' AND (ev.evaluation_date >= ? OR t.deadline >= ?)';
            departmentValues.push(moment.utc(filters.start_date).format('YYYY-MM-DD'), moment.utc(filters.start_date).format('YYYY-MM-DD'));
        }
        if (filters.end_date) {
            query += ' AND (ev.evaluation_date <= ? OR t.deadline <= ?)';
            departmentValues.push(moment.utc(filters.end_date).format('YYYY-MM-DD'), moment.utc(filters.end_date).format('YYYY-MM-DD'));
        }

        query += ' GROUP BY d.id, d.name HAVING evaluation_count > 0 OR completed_tasks > 0';
        const [departmentBreakdown] = await db.execute(query, departmentValues);

        return res.status(200).json({
            message: 'Báo cáo hiệu suất được tạo thành công',
            report: {
                employee_breakdown: employeeBreakdown,
                department_breakdown: departmentBreakdown
            }
        });
    } catch (error) {
        console.error('Lỗi khi tạo báo cáo hiệu suất:', error);
        return res.status(500).json({ message: error.message || 'Lỗi máy chủ nội bộ' });
    }
};