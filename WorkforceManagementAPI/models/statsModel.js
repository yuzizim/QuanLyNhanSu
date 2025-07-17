const db = require('../config/db.js');

class Stats {
    static async getDashboardStats() {
        try {
            const stats = {
                totalEmployees: 0,
                departmentCount: 0,
                activeJobs: 0,
                avgPerformance: 0,
                attendanceRate: 0,
                taskCompletionRate: 0
            };

            // Total Employees
            const [employeeRows] = await db.execute(`
                SELECT COUNT(*) as count 
                FROM employees 
                WHERE status = 'active'
            `);
            stats.totalEmployees = employeeRows[0].count;

            // Department Count
            const [deptRows] = await db.execute(`
                SELECT COUNT(*) as count 
                FROM departments
            `);
            stats.departmentCount = deptRows[0].count;

            // Active Jobs
            const [jobRows] = await db.execute(`
                SELECT COUNT(*) as count 
                FROM tasks 
                WHERE status IN ('pending', 'in_progress', 'review')
            `);
            stats.activeJobs = jobRows[0].count;

            // Average Performance
            const [perfRows] = await db.execute(`
                SELECT AVG(overall_score) as avg_score 
                FROM evaluations 
                WHERE status = 'finalized'
            `);
            stats.avgPerformance = parseFloat(perfRows[0].avg_score || 0).toFixed(1);

            // Attendance Rate (last 30 days)
            const [attendanceRows] = await db.execute(`
                SELECT 
                    (COUNT(CASE WHEN status = 'present' THEN 1 END) / COUNT(*)) * 100 as rate
                FROM attendance 
                WHERE date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
            `);
            stats.attendanceRate = parseFloat(attendanceRows[0].rate || 0).toFixed(1);

            // Task Completion Rate
            const [taskRows] = await db.execute(`
                SELECT 
                    (COUNT(CASE WHEN status = 'completed' THEN 1 END) / COUNT(*)) * 100 as rate
                FROM tasks
            `);
            stats.taskCompletionRate = parseFloat(taskRows[0].rate || 0).toFixed(1);

            return stats;
        } catch (error) {
            console.error('Error fetching dashboard stats:', error);
            throw new Error('Failed to fetch dashboard statistics');
        }
    }
}

module.exports = Stats;